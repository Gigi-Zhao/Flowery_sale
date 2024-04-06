package com.flow.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component    //交给IOC容器
@ConfigurationProperties(prefix = "flow.alioss")
public class AliOssProperties {

    private String endpoint;
//    private String accessKeyId;
//    private String accessKeySecret;
    private String bucketName;

}
