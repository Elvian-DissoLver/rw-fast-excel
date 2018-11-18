package com.fastExcel;

import com.google.api.client.util.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.fastExcel.MinioProperties.*;


@Configuration
public class BeanConfig {


    @Value("${minio.endpoint}") String minioEndpoint;
    @Value("${minio.accessKey}") String minioAccessKey;
    @Value("${minio.secretKey}") String minioSecretKey;
    @Value("${minio.bucket.name}") String minioBucketName;

    @Value("${asset.rest.host}") String assetRestHost;

    @Bean
    public Map<String, String> minioConfig() {
        Map<String, String> minioConfig = new HashMap<>();
        minioConfig.put(ENDPOINT.toString(), minioEndpoint);
        minioConfig.put(ACCESS_KEY.toString(), minioAccessKey);
        minioConfig.put(SECRET_KEY.toString(), minioSecretKey);
        minioConfig.put(BUCKET_NAME.toString(), minioBucketName);
        return minioConfig;
    }

    @Bean
    rFEWorker rFEW() {
        return new rFEWorker(minioConfig());
    }

}
