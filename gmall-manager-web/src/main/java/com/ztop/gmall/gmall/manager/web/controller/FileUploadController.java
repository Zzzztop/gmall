package com.ztop.gmall.gmall.manager.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileUploadController {

    @Value("${fileServer.url}")
    private String fileUrl ;//从配置文件中得到（）中的值 必须被spring容器管理

    @RequestMapping(value = "fileUpload",method = RequestMethod.POST)
    public String FileUpload(@RequestParam("file") MultipartFile file) throws IOException, MyException {
        String imgUrl =fileUrl;
        if(file!=null){
            String configFile = this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(configFile);
            TrackerClient trackerClient=new TrackerClient();
            TrackerServer trackerServer=trackerClient.getConnection();
            StorageClient storageClient=new StorageClient(trackerServer,null);

            String fileName=file.getOriginalFilename();
            String extName = StringUtils.substringAfterLast(fileName, ".");
            String[] upload_file = storageClient.upload_file(file.getBytes(),extName, null);
            imgUrl =fileUrl;
            for (int i = 0; i < upload_file.length; i++) {
                String path = upload_file[i];
                imgUrl +="/"+path;
            }
        }
            return imgUrl;
    }
}
