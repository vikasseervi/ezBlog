const getBaseURL = () => {
    const { protocol, hostname, port } = window.location;

    // Development: the React dev server (port 3000) talks to Spring on port 8080, same hostname.
    if (port === '3000') {
        return `${protocol}//${hostname}:8080`;
    }

    // Anywhere else (production behind a reverse proxy): the backend is on the same origin.
    return `${protocol}//${hostname}${port ? `:${port}` : ''}`;
};

export const baseURL = getBaseURL();