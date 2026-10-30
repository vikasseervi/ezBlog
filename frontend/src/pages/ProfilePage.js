import React, { useEffect, useState } from 'react';
import { Edit2, MapPin, Globe, Calendar, Mail, Save, X } from 'lucide-react';
import toast from 'react-hot-toast';
import Navbar from '../components/Navbar';
import { useAuth } from '../context/AuthContext';
import { api } from '../utils/api';
import { formatDate } from '../utils/formatDate';   // adjust to your file name

const BIO_MAX = 500;

const toForm = (user) => ({
    name: user?.name ?? '',
    bio: user?.bio ?? '',
    location: user?.location ?? '',
    website: user?.website ?? '',
});

// Never render a link unless it is plain http(s)
const isSafeUrl = (url) => /^https?:\/\//i.test(url || '');

const ProfilePage = () => {
    const { user, updateUser } = useAuth();
    const [isEditing, setIsEditing] = useState(false);
    const [saving, setSaving] = useState(false);
    const [form, setForm] = useState(toForm(user));

    // Keep the form in sync with the server data whenever we're not editing
    useEffect(() => {
        if (!isEditing) setForm(toForm(user));
    }, [user, isEditing]);

    const handleChange = (e) => {
        setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleSave = async (e) => {
        e.preventDefault();
        setSaving(true);
        try {
            const updated = await api.updateProfile({
                name: form.name.trim(),
                bio: form.bio.trim(),
                location: form.location.trim(),
                website: form.website.trim(),
            });
            updateUser(updated);
            toast.success('Profile updated');
            setIsEditing(false);
        } catch (error) {
            toast.error(error.message);   // validation messages come back readable
        } finally {
            setSaving(false);
        }
    };

    const handleCancel = () => {
        setForm(toForm(user));
        setIsEditing(false);
    };

    if (!user) return null;

    return (
        <div className="min-h-screen">
            <Navbar />

            <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="card">
                    {/* Header */}
                    <div className="flex items-start justify-between mb-6">
                        <div className="flex items-center space-x-4">
                            <div className="w-20 h-20 bg-gradient-to-br from-primary-400 to-primary-600 rounded-full flex items-center justify-center">
                                <span className="text-white font-semibold text-3xl">
                                    {user.name?.charAt(0).toUpperCase()}
                                </span>
                            </div>
                            <div>
                                <h1 className="text-3xl font-bold text-gray-100">{user.name}</h1>
                                <p className="flex items-center space-x-2 text-gray-400 mt-1">
                                    <Mail size={16} />
                                    <span>{user.email}</span>
                                </p>
                            </div>
                        </div>

                        {!isEditing && (
                            <button onClick={() => setIsEditing(true)} className="btn-ghost flex items-center space-x-2">
                                <Edit2 size={16} />
                                <span>Edit profile</span>
                            </button>
                        )}
                    </div>

                    {!isEditing ? (
                        /* ---------- View mode ---------- */
                        <div className="space-y-4">
                            <div>
                                <h2 className="text-sm font-medium text-gray-400 mb-1">About</h2>
                                <p className="text-gray-200 whitespace-pre-wrap">
                                    {user.bio || <span className="text-gray-500">No bio yet.</span>}
                                </p>
                            </div>

                            <div className="flex flex-wrap gap-x-6 gap-y-2 text-sm text-gray-400">
                                {user.location && (
                                    <span className="flex items-center space-x-2">
                                        <MapPin size={16} />
                                        <span>{user.location}</span>
                                    </span>
                                )}
                                {isSafeUrl(user.website) && (
                                    <a
                                        href={user.website}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        className="flex items-center space-x-2 text-primary-400 hover:text-primary-300"
                                    >
                                        <Globe size={16} />
                                        <span>{user.website}</span>
                                    </a>
                                )}
                                <span className="flex items-center space-x-2">
                                    <Calendar size={16} />
                                    <span>Member since {formatDate(user.createdAt)}</span>
                                </span>
                            </div>
                        </div>
                    ) : (
                        /* ---------- Edit mode ---------- */
                        <form onSubmit={handleSave} className="space-y-5">
                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">Name</label>
                                <input
                                    name="name"
                                    value={form.name}
                                    onChange={handleChange}
                                    className="input-field"
                                    minLength={2}
                                    maxLength={100}
                                    required
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">Email</label>
                                <input value={user.email} className="input-field opacity-60" disabled />
                                <p className="text-xs text-gray-500 mt-1">Your email is your login and can't be changed here.</p>
                            </div>

                            <div>
                                <label className="block text-sm font-medium text-gray-300 mb-2">Bio</label>
                                <textarea
                                    name="bio"
                                    value={form.bio}
                                    onChange={handleChange}
                                    className="input-field"
                                    rows={4}
                                    maxLength={BIO_MAX}
                                    placeholder="Tell readers a little about yourself"
                                />
                                <p className="text-xs text-gray-500 mt-1 text-right">{form.bio.length}/{BIO_MAX}</p>
                            </div>

                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
                                <div>
                                    <label className="block text-sm font-medium text-gray-300 mb-2">Location</label>
                                    <input
                                        name="location"
                                        value={form.location}
                                        onChange={handleChange}
                                        className="input-field"
                                        maxLength={100}
                                        placeholder="City, Country"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-300 mb-2">Website</label>
                                    <input
                                        name="website"
                                        type="url"
                                        value={form.website}
                                        onChange={handleChange}
                                        className="input-field"
                                        maxLength={200}
                                        placeholder="https://example.com"
                                    />
                                </div>
                            </div>

                            <div className="flex justify-end space-x-3 pt-2">
                                <button type="button" onClick={handleCancel} className="btn-ghost flex items-center space-x-2" disabled={saving}>
                                    <X size={16} />
                                    <span>Cancel</span>
                                </button>
                                <button type="submit" className="btn-primary flex items-center space-x-2" disabled={saving}>
                                    <Save size={16} />
                                    <span>{saving ? 'Saving…' : 'Save changes'}</span>
                                </button>
                            </div>
                        </form>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;