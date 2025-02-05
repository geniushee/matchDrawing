package com.example.matchdrawing.global.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties("custom.site")
public class SiteProperties {
    private final String domain;
    private final String frontUrl;
    private final String backUrl;

    public SiteProperties(String domain, String frontUrl, String backUrl){
        System.out.println(domain + frontUrl+backUrl);
        this.domain = domain;
        this.frontUrl = frontUrl;
        this.backUrl = backUrl;
    }
}
