package ca.bc.gov.nrs.cmdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

// This application shell is based on the reference OpenShift application available at
//  https://blog.openshift.com/using-spring-boot-on-openshift/


@SpringBootApplication

public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        // run the application.
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

}