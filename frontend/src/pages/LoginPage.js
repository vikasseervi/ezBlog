import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { LogIn } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { api } from '../utils/api';
import toast from 'react-hot-toast';
import ThemeToggle from '../components/ThemeToggle';
import GoogleButton from '../components/GoogleButton';

// Codes the backend puts in /login?error=<code> when Google sign-in is refused
const OAUTH_MESSAGES = {
    oauth_failed: 'Google sign-in failed. Please try again.',
    email_not_verified: 'Your Google email address is not verified.',
    account_disabled: 'This account is disabled.',
};

const LoginPage = () => {
    const [loading, setLoading] = useState(false);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();

    useEffect(() => {
        const code = searchParams.get('error');
        if (code) toast.error(OAUTH_MESSAGES[code] || 'Sign-in failed.');
    }, [searchParams]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const loggedInUser = await api.login(email, password);
            login(loggedInUser);
            toast.success('Welcome back!');
            navigate('/', { replace: true });
        } catch (error) {
            toast.error(error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center px-4 py-12 bg-black">
            <div className="fixed top-4 right-4"><ThemeToggle /></div>
            <div className="w-full max-w-md">
                <div className="text-center mb-8">
                    <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-primary-500 to-primary-700 rounded-2xl mb-4">
                        <span className="text-white font-bold text-2xl">B</span>
                    </div>
                    <h1 className="text-3xl font-bold text-gray-100 mb-2">Welcome Back</h1>
                    <p className="text-gray-400">Sign in to continue to ezBlog</p>
                </div>

                <div className="bg-gray-custom border border-gray-800 rounded-xl p-6 shadow-lg">
                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Email Address</label>
                            <input
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="input-field"
                                placeholder="Enter your email"
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Password</label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="input-field"
                                placeholder="Enter your password"
                                required
                            />
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="btn-primary w-full flex items-center justify-center space-x-2"
                        >
                            {loading ? (
                                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                            ) : (
                                <>
                                    <LogIn size={18} />
                                    <span>Sign In</span>
                                </>
                            )}
                        </button>
                    </form>

                    <div className="flex items-center my-5">
                        <div className="flex-1 border-t border-gray-800"></div>
                        <span className="px-3 text-sm text-gray-500">or</span>
                        <div className="flex-1 border-t border-gray-800"></div>
                    </div>

                    <GoogleButton label="Continue with Google" />
                </div>

                <p className="text-center text-sm text-gray-400 mt-6">
                    Don't have an account?{' '}
                    <Link to="/register" className="text-primary-400 hover:text-primary-300">Sign up</Link>
                </p>
            </div>
        </div>
    );
};

export default LoginPage;