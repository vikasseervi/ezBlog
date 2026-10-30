import React, { useEffect, useRef, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { Check, X } from 'lucide-react';
import { api } from '../utils/api';
import ThemeToggle from '../components/ThemeToggle';

const VerifyEmailPage = () => {
    const [searchParams] = useSearchParams();
    const [status, setStatus] = useState('verifying');   // verifying | success | error
    const [message, setMessage] = useState('');
    const started = useRef(false);

    useEffect(() => {
        // React dev mode runs effects twice, and the token works only once.
        // Without this guard the second call would fail and hide the success.
        if (started.current) return;
        started.current = true;

        const token = searchParams.get('token');
        window.history.replaceState({}, '', window.location.pathname);   // remove the token from the address bar

        if (!token) {
            setStatus('error');
            setMessage('This link is missing its token.');
            return;
        }

        api.verifyEmail(token)
            .then(() => setStatus('success'))
            .catch((error) => {
                setStatus('error');
                setMessage(error.message);   // "Invalid or expired link"
            });
    }, [searchParams]);

    return (
        <div className="min-h-screen flex items-center justify-center px-4 py-12 bg-black">
            <div className="fixed top-4 right-4"><ThemeToggle /></div>
            <div className="w-full max-w-md text-center">
                <div className="bg-gray-custom border border-gray-800 rounded-xl p-8 shadow-lg space-y-4">
                    {status === 'verifying' && (
                        <>
                            <div className="w-8 h-8 mx-auto border-2 border-primary-400 border-t-transparent rounded-full animate-spin"></div>
                            <p className="text-gray-300">Verifying your email…</p>
                        </>
                    )}

                    {status === 'success' && (
                        <>
                            <div className="inline-flex items-center justify-center w-14 h-14 bg-green-600 rounded-full">
                                <Check className="text-white" size={28} />
                            </div>
                            <h1 className="text-2xl font-bold text-gray-100">Email verified</h1>
                            <p className="text-gray-400">Your account is ready.</p>
                            <Link to="/login" className="btn-primary inline-block">Log in</Link>
                        </>
                    )}

                    {status === 'error' && (
                        <>
                            <div className="inline-flex items-center justify-center w-14 h-14 bg-red-600 rounded-full">
                                <X className="text-white" size={28} />
                            </div>
                            <h1 className="text-2xl font-bold text-gray-100">Verification failed</h1>
                            <p className="text-gray-400">{message}</p>
                            <Link to="/register" className="btn-primary inline-block">Register again</Link>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

export default VerifyEmailPage;