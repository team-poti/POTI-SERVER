package org.sopt.poti.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  private static final String SECURITY_SCHEME_NAME = "authorization";

  @Bean
  public OpenAPI openAPI() {

    // 운영 서버 (HTTPS 필수)
    Server prodServer = new Server();
    prodServer.setUrl("https://poti.kr");
    prodServer.setDescription("운영 서버 (Production)");

    // 로컬 서버 (개발용)
    Server localServer = new Server();
    localServer.setUrl("http://localhost:8080");
    localServer.setDescription("로컬 서버 (Local)");

    return new OpenAPI()
        .components(new Components()
            .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
        .info(new Info()
            .title("POTI Server API")
            .description("POTI 서비스 API 명세서")
            .version("1.0.0"))
        .servers(List.of(prodServer, localServer));
  }
}
