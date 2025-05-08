package com.thales.common.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis bağlantı özelliklerini tanımlayan yapılandırma sınıfı.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "thales.redis")
public class RedisProperties {

    /**
     * Redis sunucu adresi.
     */
    private String host = "localhost";

    /**
     * Redis port numarası.
     */
    private int port = 6379;

    /**
     * Redis şifresi (varsa).
     */
    private String password;

    /**
     * Redis veritabanı indeksi.
     */
    private int database = 0;

    /**
     * Bağlantı zaman aşımı süresi (milisaniye).
     */
    private long timeout = 2000;

    /**
     * Bağlantı havuzu maksimum boyutu.
     */
    private int maxActive = 8;

    /**
     * Kullanılabilir bağlantı olmadığında maksimum bekleme süresi (milisaniye).
     */
    private long maxWait = -1;

    /**
     * Havuzdaki minimum boşta bağlantı sayısı.
     */
    private int minIdle = 0;

    /**
     * Havuzdaki maksimum boşta bağlantı sayısı.
     */
    private int maxIdle = 8;
} 