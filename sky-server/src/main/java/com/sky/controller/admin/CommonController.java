package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.properties.AliOssProperties;
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
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@Slf4j
@RequestMapping("/admin/common")
@Api(tags ="通用接口")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload (MultipartFile file){
//        获取初始文件名
        String originalFilename = file.getOriginalFilename();
//        获取拓展名
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        构造新的文件名称
        String objectName = UUID.randomUUID().toString()+extension;
        try {
//         文件的请求路径
        String filePath= aliOssUtil.upload(file.getBytes(), objectName);
        return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败：{}",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
