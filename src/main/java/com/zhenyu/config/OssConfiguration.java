package com.zhenyu.config;

import com.zhenyu.common.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OssConfiguration {

    @Value("${sky.alioss.endpoint}")
    private String endpoint;

    @Value("${sky.alioss.access-key-id}")
    private String accessKeyId;

    @Value("${sky.alioss.access-key-secret}")
    private String accessKeySecret;

    @Value("${sky.alioss.bucket-name}")
    private String bucketName;

    @Bean
    public AliOssUtil aliOssUtil() {
        log.info("开始创建阿里云文件上传工具类对象：{}", endpoint);
        return new AliOssUtil(endpoint, accessKeyId, accessKeySecret, bucketName);
    }
}
