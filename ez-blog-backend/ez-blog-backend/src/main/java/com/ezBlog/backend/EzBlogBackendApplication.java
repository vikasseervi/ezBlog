package com.ezBlog.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EzBlogBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EzBlogBackendApplication.class, args);
	}

}

