import { useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function OAuthCallback() {
    const { user, loading } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (!loading) navigate(user ? '/' : '/login?error=oauth_failed', { replace: true });
    }, [user, loading, navigate]);

    return <p>Signing you in…</p>;
}