-- Profile page: extra personal details (both optional)
ALTER TABLE users ADD COLUMN location VARCHAR(100);
ALTER TABLE users ADD COLUMN website  VARCHAR(200);

-- Pagination: the sort order is (created_at DESC, id DESC), so the indexes must end with id as well
DROP INDEX idx_posts_published_created;
DROP INDEX idx_posts_category_created;

CREATE INDEX idx_posts_published_created ON posts (created_at DESC, id DESC) WHERE published = TRUE;
CREATE INDEX idx_posts_category_created  ON posts (category, created_at DESC, id DESC) WHERE published = TRUE;