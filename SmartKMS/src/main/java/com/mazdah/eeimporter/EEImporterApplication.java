package com.mazdah.eeimporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javassist.CtField.Initializer;

@SpringBootApplication
@EnableJpaAuditing
public class EEImporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(new Class[] {EEImporterApplication.class, Initializer.class}, args);
	}
}
