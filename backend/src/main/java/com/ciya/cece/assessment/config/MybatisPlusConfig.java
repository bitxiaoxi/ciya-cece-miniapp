package com.ciya.cece.assessment.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.ciya.cece.assessment.mapper")
public class MybatisPlusConfig {
}
