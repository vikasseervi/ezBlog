import React, { createContext, useState, useContext, useEffect } from 'react';
import { api, setSessionExpiredHandler } from '../utils/api';

const AuthContext = createContext(null);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within AuthProvider');
    }
    return context;
};


export const AuthProvider = ({ children: childrenComponent }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);   // stays true until the first session check finishes
    const updateUser = (updatedUser) => setUser(updatedUser);

    useEffect(() => {

        // If the refresh token expires or is rejected later, log the user out in the UI too
        setSessionExpiredHandler(() => setUser(null));

        // A page reload wipes the in-memory access token, so ask the server who we are.
        // This also completes the Google login after the redirect back to the app.
        let cancelled = false;
        api.restoreSession()
            .then((restoredUser) => {
                if (!cancelled) setUser(restoredUser);   // null when there's no valid refresh cookie
            })
            .catch(() => {
                if (!cancelled) setUser(null);
            })
            .finally(() => {
                if (!cancelled) setLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, []);

    // api.login() already stored the access token and returned the user from /users/me.
    // This only puts that user into React state.
    const login = (userData) => {
        setUser(userData);
    };

    const logout = async () => {
        try {
            await api.logout();   // clears the refresh cookie on the server and the token in memory
        } finally {
            setUser(null);
        }
    };

    const authData = {
        user,
        login,
        logout,
        updateUser,
        loading
    };

    return <AuthContext.Provider value={authData}>{childrenComponent}</AuthContext.Provider>;
};