package com.librarium;

import com.librarium.configuration.AppConfiguration;
import com.librarium.configuration.Configuration;
import com.librarium.search.Elasticsearch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class LibrariumServiceApplication {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
		SpringApplication.run(LibrariumServiceApplication.class, args);
	}

}
