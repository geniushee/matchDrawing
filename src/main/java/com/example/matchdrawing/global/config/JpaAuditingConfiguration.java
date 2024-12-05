package com.example.matchdrawing.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Controller 테스트시 JPA로 인한 에러 발생. main에 두지 않고 별도 config클래스 작성.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfiguration {
}
