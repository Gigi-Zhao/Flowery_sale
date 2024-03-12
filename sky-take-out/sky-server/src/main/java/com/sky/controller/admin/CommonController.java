package com.sky.controller.admin;

import com.aliyuncs.exceptions.ClientException;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    // 把AliOSSUtils工具类注入进来
    @Autowired
    private AliOssUtil aliOSSUtils;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) throws IOException, ClientException {

        log.info("文件上传:{}", file);

        try {
            //调用阿里云OSS工具类进行文件上传，返回值为图片的阿里云地址
            String url = aliOSSUtils.upload(file);
            log.info("文件上传完成,文件访问的url:{}", url);

            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败:{}", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
