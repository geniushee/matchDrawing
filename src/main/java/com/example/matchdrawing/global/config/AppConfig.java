package com.example.matchdrawing.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AppConfig {

    private final Environment environment;

    public AppConfig(Environment environment){
        this.environment = environment;
    }

    @Bean
    public boolean isProduct(){
        String[] profiles = environment.getActiveProfiles();
        if(profiles.length >0){
            for(int i = 0; i < profiles.length;i++){
                if(profiles[i].equals("product")){
                    return true;
                }
            }
        }
        return false;
    }

}
