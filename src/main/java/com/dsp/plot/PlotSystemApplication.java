package com.dsp.plot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableFeignClients(basePackages = "com.dsp.plot.entity.feignclient")
public class PlotSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlotSystemApplication.class, args);
    }
}