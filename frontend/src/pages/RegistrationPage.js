import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { UserPlus, Mail } from 'lucide-react';
import { api } from '../utils/api';
import toast from 'react-hot-toast';
import ThemeToggle from '../components/ThemeToggle';
import GoogleButton from '../components/GoogleButton';

const RegisterPage = () => {
    const [loading, setLoading] = useState(false);
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [message, setMessage] = useState(null);   // set after a successful registration

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            toast.error('Passwords do not match');
            return;
        }
        setLoading(true);
        try {
            const result = await api.register(name.trim(), email.trim(), password);
            setMessage(result.message);
        } catch (error) {
            toast.error(error.message);
        } finally {
            setLoading(false);
        }
    };

    if (message) {
        return (
            <div className="min-h-screen flex items-center justify-center px-4 py-12 bg-black">
                <div className="fixed top-4 right-4"><ThemeToggle /></div>
                <div className="w-full max-w-md text-center">
                    <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-primary-500 to-primary-700 rounded-2xl mb-4">
                        <Mail className="text-white" size={28} />
                    </div>
                    <h1 className="text-3xl font-bold text-gray-100 mb-2">Check your email</h1>
                    <div className="bg-gray-custom border border-gray-800 rounded-xl p-6 shadow-lg mt-6 space-y-3">
                        <p className="text-gray-300">{message}</p>
                        <p className="text-gray-400 text-sm">
                            We sent a link to <span className="text-gray-200">{email}</span>. It expires in 60 minutes.
                            Not there? Check your spam folder, or register again to get a new link.
                        </p>
                        <Link to="/login" className="btn-primary inline-block">Back to login</Link>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen flex items-center justify-center px-4 py-12 bg-black">
            <div className="fixed top-4 right-4"><ThemeToggle /></div>
            <div className="w-full max-w-md">
                <div className="text-center mb-8">
                    <div className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-primary-500 to-primary-700 rounded-2xl mb-4">
                        <span className="text-white font-bold text-2xl">B</span>
                    </div>
                    <h1 className="text-3xl font-bold text-gray-100 mb-2">Create your account</h1>
                    <p className="text-gray-400">Join ezBlog and start writing</p>
                </div>

                <div className="bg-gray-custom border border-gray-800 rounded-xl p-6 shadow-lg">
                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Name</label>
                            <input
                                type="text"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                className="input-field"
                                placeholder="Your name"
                                minLength={2}
                                maxLength={100}
                                required
                            />
                        </div>

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
                                placeholder="At least 8 characters"
                                minLength={8}
                                maxLength={72}
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Confirm Password</label>
                            <input
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                className="input-field"
                                placeholder="Repeat your password"
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
                                    <UserPlus size={18} />
                                    <span>Create Account</span>
                                </>
                            )}
                        </button>
                    </form>

                    <div className="flex items-center my-5">
                        <div className="flex-1 border-t border-gray-800"></div>
                        <span className="px-3 text-sm text-gray-500">or</span>
                        <div className="flex-1 border-t border-gray-800"></div>
                    </div>

                    <GoogleButton label="Sign up with Google" />
                </div>

                <p className="text-center text-sm text-gray-400 mt-6">
                    Already have an account?{' '}
                    <Link to="/login" className="text-primary-400 hover:text-primary-300">Sign in</Link>
                </p>
            </div>
        </div>
    );
};

export default RegisterPage;