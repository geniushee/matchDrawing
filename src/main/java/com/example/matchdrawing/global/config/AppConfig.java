package com.example.matchdrawing.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Getter
    private static String koreanDictKey;

    @Value(value = "${secret.KDictKey}")
    public void setKoreanDictKey(String key){
        this.koreanDictKey = key;
    }
}
