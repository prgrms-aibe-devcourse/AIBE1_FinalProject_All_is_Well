package org.example.bookmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BookmarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookmarketApplication.class, args);
    }

}
