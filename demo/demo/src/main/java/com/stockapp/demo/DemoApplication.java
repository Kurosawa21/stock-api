package com.stockapp.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DemoApplication {


	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);
		/*ApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);
		System.out.println(ctx.getBeanDefinitionCount() + " beans créés");
		System.out.println(ctx.getBean(GreetingService.class));*/
	}

}
