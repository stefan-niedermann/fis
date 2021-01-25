package it.niedermann.fis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FisApplication {

    public static void main(String[] args) {
        SpringApplication.run(FisApplication.class, args);
    }
}
