package org.sopt.poti.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  private static final String SECURITY_SCHEME_NAME = "authorization";
  private static final String LOCAL_URL = "http://localhost:8080";

  @Value("${swagger.server-url}")
  private String serverUrl;

  @Value("${swagger.server-description}")
  private String serverDescription;

  @Bean
  public OpenAPI openAPI() {
    Server currentServer = new Server();
    currentServer.setUrl(serverUrl);
    currentServer.setDescription(serverDescription);

    List<Server> servers = new ArrayList<>();
    servers.add(currentServer);

    if (!LOCAL_URL.equals(serverUrl)) {
      Server localServer = new Server();
      localServer.setUrl(LOCAL_URL);
      localServer.setDescription("로컬 서버 (Local)");
      servers.add(localServer);
    }

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
        .servers(servers);
  }
}
