package com.example.matchdrawing.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AppConfig {

    private final Environment environment;

    public AppConfig(Environment environment){
        this.environment = environment;
    }

    @Value(value = "${secret.KDictKey}")
    private String koreanDictKey;

    //한국표준대국어사전 API키
    @Bean
    public String koreanDictKey(){
        return koreanDictKey;
    }

    @Value(value = "${custom.site.domain}")
    private String domain;

   @Bean
    public String domain(){return domain;}

    @Value(value = "${custom.site.frontUrl}")
    private String frontUrl;

    @Bean
    public String frontUrl() {return frontUrl;}


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
