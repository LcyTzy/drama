package com.zhenyu.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.wechat")
@Data
public class WeChatProperties {
    private String appid;                 // 微信小程序 AppId
    private String appsecret;             // 微信小程序 AppSecret
    private String mchid;                 // 商户号
    private String mchSerialNo;           // 商户 API 证书序列号
    private String privateKeyFilePath;    // 商户私钥文件路径（classpath: 或绝对路径）
    private String apiV3Key;              // API v3 密钥（用于解密回调数据）
    private String weChatPayCertFilePath; // 微信支付平台证书路径
    private String notifyUrl;             // 支付成功回调地址（公网可访问）
    private String refundNotifyUrl;       // 退款成功回调地址（可选）
}
