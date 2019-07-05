package com.bs.book.controller;

import com.bs.book.annotation.LoginIgnore;
import com.bs.book.util.ErrorEnum;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RequestMapping("/image")
@Slf4j
@Controller
public class ImageController extends BaseController {
    @Resource
    Gson gson;

    @RequestMapping("/upload")
    @ResponseBody
    public Object upload(HttpServletRequest req, @RequestParam("file") MultipartFile file) {
        try {
            // get file name
            String basePath = new ApplicationHome(this.getClass()).getSource().getParentFile().getPath()+ "/files";
            String fileName = System.currentTimeMillis() + file.getOriginalFilename();
            String destFileName = basePath + File.separator + fileName;
            File destFile = new File(destFileName);
            destFile.getParentFile().mkdirs();
            file.transferTo(destFile);
            log.info("UPLOAD FILE SAVE TO: " + destFileName);
            return gson.toJson(buildSuccessResp(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            log.error("UPLOAD FILE: " + e.getMessage());
            return gson.toJson(buildResponse(ErrorEnum.ERROR_UPLOAD_FAIL));
        }
    }

    @RequestMapping(value = {"/{filename}"}, method = RequestMethod.GET)
    @LoginIgnore
    public void getImage(@PathVariable("filename") String filename,
                           HttpServletRequest request, HttpServletResponse response) {
        String basePath = new ApplicationHome(this.getClass()).getSource().getParentFile().getPath()+ "/files";
        String filePath = basePath + File.separator + filename;
        File file = new File(filePath);
        if (file.exists()) {
            try {
                response.setContentType("application/octet-stream");
                InputStream inputStream = new FileInputStream(file);
                ServletOutputStream outputStream = response.getOutputStream();
                byte[] bs = new byte[1024];
                while ((inputStream.read(bs) > 0)) {
                    outputStream.write(bs);
                }
                outputStream.close();
                inputStream.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
