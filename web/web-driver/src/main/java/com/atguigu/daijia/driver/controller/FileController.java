package com.atguigu.daijia.driver.controller;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.driver.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "上传管理接口")
@RestController
@RequestMapping("file")
public class FileController {

//    @Autowired
//    private CosService cosService;

    @Autowired
    private FileService fileService;

    //司机到起点后拍照车辆认证上传，返回值特殊处理，这里注释掉，上传到minio里
//    @Operation(summary = "上传")
//    @CheckLogin
//    @PostMapping("/upload")
//    public Result<String> upload(@RequestPart("file") MultipartFile file,
//                                      @RequestParam(name = "path", defaultValue = "auth") String path) {
//        CosUploadVo cosUploadVo = cosService.upload(file, path);
//        String showUrl = cosUploadVo.getShowUrl();
//        return Result.ok(showUrl);
//    }

    //司机到起点后拍照车辆认证上传，监控录音文件
    @Operation(summary = "Minio文件上传")
    @PostMapping("/upload")
    public Result<String> upload(@RequestPart("file") MultipartFile file) {
        return Result.ok(fileService.upload(file));
    }

}
