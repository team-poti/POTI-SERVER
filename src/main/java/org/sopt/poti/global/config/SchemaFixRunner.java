package org.sopt.poti.global.config;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
@Profile("!test")
@RequiredArgsConstructor
public class SchemaFixRunner implements ApplicationRunner {

  private final DataSource dataSource;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    try (var conn = dataSource.getConnection();
        var stmt = conn.createStatement()) {
      stmt.execute(
          "ALTER TABLE users MODIFY COLUMN social_type VARCHAR(20) NOT NULL"
      );
      log.info("social_type 컬럼 VARCHAR(20) 변경 완료");
    } catch (Exception e) {
      log.warn("social_type 컬럼 변경 실패 (이미 변경됐거나 무시 가능): {}", e.getMessage());
    }
  }
}
