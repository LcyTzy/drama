package com.zhenyu.pojo.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class PhoneLoginDTO implements Serializable {
    private String phone;
    private String code;
}
