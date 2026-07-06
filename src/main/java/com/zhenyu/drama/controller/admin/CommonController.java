package com.zhenyu.drama.controller.admin;

import com.zhenyu.common.constant.MessageConstant;
import com.zhenyu.common.utils.AliOssUtil;
import com.zhenyu.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.bind.DatatypeConverter;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传（小文件）
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);

        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀   dfdfdf.png
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
            String objectName = UUID.randomUUID().toString() + extension;

            //文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

    /**
     * 获取 OSS 直传签名（前端直传大文件用）
     */
    @GetMapping("/oss-signature")
    @ApiOperation("获取OSS直传签名")
    public Result<Map<String, String>> getOssSignature() {
        try {
            String endpoint = aliOssUtil.getEndpoint();
            String bucketName = aliOssUtil.getBucketName();
            String host = "https://" + bucketName + "." + endpoint;
            String dir = "video/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";

            long expireTime = 3600;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);

            String policyJson = "{\"expiration\":\"" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(expiration) + "\",\"conditions\":[[\"content-length-range\",0,5368709120]]}";

            // policy 需要先 base64 编码
            String policyBase64 = Base64.getEncoder().encodeToString(policyJson.getBytes(StandardCharsets.UTF_8));

            // 签名是对 base64 后的 policy 进行 HMAC-SHA1
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(aliOssUtil.getAccessKeySecret().getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] signData = mac.doFinal(policyBase64.getBytes(StandardCharsets.UTF_8));
            String signature = DatatypeConverter.printBase64Binary(signData);

            Map<String, String> result = new HashMap<>();
            result.put("accessKeyId", aliOssUtil.getAccessKeyId());
            result.put("policy", policyBase64);
            result.put("signature", signature);
            result.put("host", host);
            result.put("dir", dir);
            result.put("expire", String.valueOf(expireEndTime / 1000));

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取OSS签名失败", e);
            return Result.error("获取签名失败");
        }
    }
}
