import { useCallback, useEffect, useRef, useState } from 'react';
import { api } from '../utils/api';

const PAGE_SIZE = 10;

export default function usePosts(category, search) {
    const [posts, setPosts] = useState([]);
    const [nextPage, setNextPage] = useState(0);
    const [hasMore, setHasMore] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const latestRequest = useRef(0);

    const fetchPage = useCallback(async (pageToLoad, replace) => {
        const requestId = ++latestRequest.current;
        setLoading(true);
        setError(null);
        if (replace) setPosts([]);
        try {
            const data = await api.getPosts({ page: pageToLoad, size: PAGE_SIZE, category, q: search });
            if (requestId !== latestRequest.current) return;   // a newer request replaced this one

            setPosts((previous) => {
                if (replace) return data.posts;
                const seen = new Set(previous.map((p) => p.id));   // skip posts that shifted between pages
                return [...previous, ...data.posts.filter((p) => !seen.has(p.id))];
            });
            setNextPage(pageToLoad + 1);
            setHasMore(data.hasNext);
        } catch (e) {
            if (requestId === latestRequest.current) setError(e.message);
        } finally {
            if (requestId === latestRequest.current) setLoading(false);
        }
    }, [category, search]);

    // First page on mount, and again whenever the category or the search text changes
    useEffect(() => {
        fetchPage(0, true);
    }, [fetchPage]);

    const loadMore = useCallback(() => {
        if (!loading && hasMore) fetchPage(nextPage, false);
    }, [loading, hasMore, nextPage, fetchPage]);

    return { posts, loading, error, hasMore, loadMore };
}