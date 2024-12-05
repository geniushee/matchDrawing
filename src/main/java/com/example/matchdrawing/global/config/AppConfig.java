package com.example.matchdrawing.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Getter
    private static String koreanDictKey;

    //한국표준대국어사전 API키
    @Value(value = "${secret.KDictKey}")
    public void setKoreanDictKey(String key){
        this.koreanDictKey = key;
    }
}
