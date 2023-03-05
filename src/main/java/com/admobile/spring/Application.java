 
package com.admobile.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@EnableJpaRepositories(basePackages = {"com.admobile.spring.entity"})
//@EntityScan(basePackages = {"com.admobile.spring.entity"})
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
