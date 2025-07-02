package io.erisdev.secretlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SecretLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecretLinkApplication.class, args);
    }

}
