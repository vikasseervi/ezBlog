package com.ezBlog.backend.model;

import java.util.List;

public record PostPageResponse(List<PostResponse> posts, int page, int size, boolean hasNext) {}