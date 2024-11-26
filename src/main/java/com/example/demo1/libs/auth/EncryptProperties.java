package com.example.demo1.libs.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("security")
public class EncryptProperties {

    private String secretKey;
    private String salt;
    private String localUrl;
    private String serverUrl;

    @Override
    public String toString() {
        return "EncryptProperties{" +
                "secretKey='" + secretKey + '\'' +
                ", salt='" + salt + '\'' +
                ", localUrl='" + localUrl + '\'' +
                ", serverUrl='" + serverUrl + '\'' +
                '}';
    }
}

