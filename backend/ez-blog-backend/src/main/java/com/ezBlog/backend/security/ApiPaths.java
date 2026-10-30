package com.ezBlog.backend.security;

public final class ApiPaths {
    private ApiPaths() {}
    public static final String BASE = "/ezblog";       // flip to "/ezblog" together with the controllers, nowhere else
    public static final String AUTH = BASE + "/auth";
    public static final String REFRESH = AUTH + "/refresh";
}