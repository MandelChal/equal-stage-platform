package com.equal_stage_platform.dev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class EqualStagePlatformApplication {

	@GetMapping("/hello")
	public String hello() {
		return "hello world";
	}
	public static void main(String[] args) {
		SpringApplication.run(EqualStagePlatformApplication.class, args);
	}

}
