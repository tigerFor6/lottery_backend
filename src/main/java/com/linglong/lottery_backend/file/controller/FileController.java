package com.linglong.lottery_backend.file.controller;

import com.linglong.lottery_backend.order.model.Result;
import com.linglong.lottery_backend.order.model.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/8
 */
@RestController
@RequestMapping(value = "/api/file")
public class FileController {

    @Value("${spring.file.ipAddress}")
    private String ipAddress;

    @Value("${spring.file.filePath}")
    private String filePath;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new NullPointerException("文件为空");
            }
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            // 文件上传后的路径
            fileName = UUID.randomUUID() + suffixName;
            String path = filePath + fileName;
            File dest = new File(path);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();// 新建文件夹
                Runtime.getRuntime().exec("chmod 777 -R " + filePath);
            }
            file.transferTo(dest);// 文件写入
            Runtime.getRuntime().exec("chmod 777 -R " + filePath);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("fileName",file.getOriginalFilename());
            resultMap.put("fileUuidName",fileName);
            resultMap.put("path",ipAddress + fileName);
            return new Result(StatusCode.OK.getCode(), "上传文件成功", resultMap);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Result(StatusCode.ERROR.getCode(), "上传文件失败", null);
    }
}
