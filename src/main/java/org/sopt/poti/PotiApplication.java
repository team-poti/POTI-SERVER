package org.sopt.poti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PotiApplication {

  public static void main(String[] args) {
    SpringApplication.run(PotiApplication.class, args);
  }

}
