package org.sopt.poti.global.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("!test")
public class AddressLineMigrationRunner implements ApplicationRunner {

  @PersistenceContext
  private EntityManager em;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    int userRows = em.createNativeQuery(
        "UPDATE user_addresses SET address = address_line WHERE address IS NULL AND address_line IS NOT NULL"
    ).executeUpdate();

    int orderRows = em.createNativeQuery(
        "UPDATE orders SET address = address_line WHERE address IS NULL AND address_line IS NOT NULL"
    ).executeUpdate();

    if (userRows + orderRows > 0) {
      log.info("address_line 백필 완료 — user_addresses: {}건, orders: {}건", userRows, orderRows);
    }
  }
}
