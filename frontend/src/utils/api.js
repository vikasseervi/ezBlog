import { baseURL } from './baseURL.js';

const API_BASE_URL = `${baseURL}/ezblog`;

// Google login is handled by Spring Security itself, so this URL is NOT under /ezblog.
export const googleLoginUrl = `${baseURL}/oauth2/authorization/google`;

// ---------- token handling ----------

// The access token lives ONLY in memory. Nothing in localStorage means nothing for an XSS attack to steal.
let accessToken = null;
let refreshInFlight = null;
let onSessionExpired = () => {};

export const setSessionExpiredHandler = (handler) => {
    onSessionExpired = handler;
};

export class ApiError extends Error {
    constructor(message, status) {
        super(message);
        this.name = 'ApiError';
        this.status = status;
    }
}

// The backend answers errors in several shapes: plain text, {error}, {message}, {field: message}, ProblemDetail.
const readError = async (response, fallback) => {
    const text = await response.text();
    try {
        const body = JSON.parse(text);
        if (typeof body === 'string') return body;
        if (typeof body.message === 'string') return body.message;
        if (typeof body.error === 'string') return body.error;
        if (typeof body.detail === 'string') return body.detail;
        const fieldErrors = Object.values(body).filter((v) => typeof v === 'string');
        if (fieldErrors.length > 0) return fieldErrors.join(', ');   // validation: { title: "Title is required" }
    } catch {
        // not JSON: plain text
    }
    return text || fallback;
};

// Uses the HttpOnly refresh cookie to get a new access token.
// Concurrent callers share ONE request, so a burst of 401s triggers only one refresh.
export const refreshAccessToken = () => {
    if (!refreshInFlight) {
        refreshInFlight = (async () => {
            try {
                const response = await fetch(`${API_BASE_URL}/auth/refresh`, {
                    method: 'POST',
                    credentials: 'include',
                });
                if (!response.ok) {
                    accessToken = null;
                    return false;
                }
                const data = await response.json();
                accessToken = data.accessToken;
                return true;
            } catch {
                accessToken = null;
                return false;
            } finally {
                refreshInFlight = null;
            }
        })();
    }
    return refreshInFlight;
};

// ---------- one place for every request ----------

const request = async (path, { method = 'GET', body, auth = true, isRetry = false } = {}) => {
    const headers = { Accept: 'application/json' };
    if (body !== undefined) headers['Content-Type'] = 'application/json';
    if (auth && accessToken) headers.Authorization = `Bearer ${accessToken}`;

    const response = await fetch(`${API_BASE_URL}${path}`, {
        method,
        headers,
        credentials: 'include',            // send the refresh cookie (needed by /auth/refresh)
        body: body !== undefined ? JSON.stringify(body) : undefined,
    });

    // Token expired or missing: refresh once, then retry the original request once.
    if (response.status === 401 && auth && !isRetry) {
        if (await refreshAccessToken()) {
            return request(path, { method, body, auth, isRetry: true });
        }
        onSessionExpired();
    }

    if (!response.ok) {
        throw new ApiError(await readError(response, `Request failed (${response.status})`), response.status);
    }
    return response.status === 204 ? null : response.json();
};

const assertValidId = (id) => {
    if (!id || id === 'undefined') throw new ApiError('Invalid post ID', 400);
};

// ---------- public API ----------

export const api = {
    googleLoginUrl,

    // Auth
    login: async (email, password) => {
        const data = await request('/auth/login', { method: 'POST', body: { email, password }, auth: false });
        accessToken = data.accessToken;
        return api.getCurrentUser();
    },

    // Returns { message }. The user is NOT logged in; they must click the email link first.
    register: (name, email, password) =>
        request('/auth/register', { method: 'POST', body: { name, email, password }, auth: false }),

    verifyEmail: (token) =>
        request('/auth/verify-email', { method: 'POST', body: { token }, auth: false }),

    // Call once when the app starts (a page reload wipes memory) and after the Google redirect.
    restoreSession: async () => ((await refreshAccessToken()) ? api.getCurrentUser() : null),

    logout: async () => {
        try {
            await request('/auth/logout', { method: 'POST', auth: false });
        } finally {
            accessToken = null;
        }
    },

    getCurrentUser: () => request('/users/me'),
    updateProfile: (profile) => request('/users/me', { method: 'PUT', body: profile }),

    // Posts
    getPosts: ({ page = 0, size = 10, category, q } = {}) => {
        const params = new URLSearchParams({ page, size });
        if (category) params.set('category', category);
        if (q) params.set('q', q);
        return request(`/posts?${params}`, { auth: false });
    },

    getMyPosts: () => request('/posts/my-posts'),

    getPostById: (id) => {
        assertValidId(id);
        return request(`/posts/${encodeURIComponent(id)}`);   // token (if any) lets authors see their own drafts
    },

    createPost: (postData) => request('/posts', { method: 'POST', body: postData }),

    updatePost: (id, postData) => {
        assertValidId(id);
        return request(`/posts/${encodeURIComponent(id)}`, { method: 'PUT', body: postData });
    },

    deletePost: (id) => {
        assertValidId(id);
        return request(`/posts/${encodeURIComponent(id)}`, { method: 'DELETE' });
    },
};