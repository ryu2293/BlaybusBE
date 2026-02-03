package com.blaybus.blaybusbe.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", securityScheme())) // 1. JWT 설정 등록
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt")) // 2. 전역 적용
                .info(apiInfo()) // 3. API 정보
                .servers(serverList()); // 4. 서버 리스트 (로컬 vs 배포)
    }

    // --- 아래는 가독성을 위한 분리 메서드들 ---

    /**
     * JWT 보안 설정 (Bearer Token)
     */
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");
    }

    /**
     * API 문서 기본 정보
     */
    private Info apiInfo() {
        return new Info()
                .title("SeolStudy API Documentation")
                .description("SeolStudy 서비스의 API 명세서입니다.")
                .version("1.0.0");
    }

    /**
     * 환경별 서버 주소 설정
     * 스웨거 상단 'Servers' 드롭다운에 표시됩니다.
     */
    private List<Server> serverList() {
        return List.of(
                new Server().url("http://localhost:8080").description("Local (개발 환경)"),
                new Server().url("https://api.seolstudy.cloud").description("Production (배포 환경)")
        );
    }

}
