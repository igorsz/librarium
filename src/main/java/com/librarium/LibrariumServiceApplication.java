package com.librarium;

import com.librarium.configuration.Configuration;
import com.librarium.search.Elasticsearch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibrariumServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibrariumServiceApplication.class, args);
	}
}
