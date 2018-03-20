package com.innotree.smartkms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javassist.CtField.Initializer;

@SpringBootApplication
@EnableJpaAuditing
public class SmartKmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(new Class[] {SmartKmsApplication.class, Initializer.class}, args);
	}
}
