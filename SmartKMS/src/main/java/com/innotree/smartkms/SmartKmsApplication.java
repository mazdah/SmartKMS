package com.innotree.smartkms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javassist.CtField.Initializer;

@SpringBootApplication
public class SmartKmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(new Class[] {SmartKmsApplication.class, Initializer.class}, args);
	}
}
