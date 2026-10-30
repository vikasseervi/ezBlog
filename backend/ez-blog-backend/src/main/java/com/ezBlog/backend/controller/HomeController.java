package com.ezBlog.backend.controller;

import com.ezBlog.backend.security.ApiPaths;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.BASE)
public class HomeController {

    @GetMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public String getHome( ) {
        return "Backend server is running successfully!";
    }

}