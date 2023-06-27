package se.liu.ida.tdp024.account.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "se.liu.ida.tdp024.account.rest",
        "se.liu.ida.tdp024.account.util.kafka"
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
