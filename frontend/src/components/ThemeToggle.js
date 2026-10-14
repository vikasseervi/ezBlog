import React, { useState, useEffect } from 'react';
import { Sun, Moon } from 'lucide-react';

const ThemeToggle = () => {
    const [isLight, setIsLight] = useState(
        () => document.documentElement.classList.contains('light')
    );

    useEffect(() => {
        document.documentElement.classList.toggle('light', isLight);
        try {
            localStorage.setItem('theme', isLight ? 'light' : 'dark');
        } catch {}
    }, [isLight]);

    return (
        <button
            onClick={() => setIsLight((prev) => !prev)}
            className="text-gray-400 hover:text-gray-100 transition-colors p-2 hover:bg-gray-900 rounded-lg"
            title={isLight ? 'Switch to dark mode' : 'Switch to light mode'}
        >
            {isLight ? <Moon size={22} /> : <Sun size={22} />}
        </button>
    );
};

export default ThemeToggle;