package com.librarium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibrariumServiceApplication {

	public static void main(String[] args) {
//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
		SpringApplication.run(LibrariumServiceApplication.class, args);
	}

}
