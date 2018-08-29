package com.wandaph.filetarnsfer.config;

import com.hsjry.lang.fs.service.DefaultHsjryFileServiceImpl;
import com.hsjry.lang.fs.service.HsjryFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;


@SpringBootConfiguration
public class HsjryFileServiceConfig {

    public static final Logger LOGGER = LoggerFactory.getLogger(HsjryFileServiceConfig.class);

    @Value("${fileService.fileUrl}")
    private String fileUrl;

    @Value("${fileService.rootDir}")
    private String rootDir;

    @Value("${fileService.fileType}")
    private String fileType;

    @Value("${fileService.fileSize}")
    private Long fileSize;

    @Bean
    public HsjryFileService getHsjryFileService() throws Exception{

        if (StringUtils.isEmpty(fileUrl)){
            throw new Exception("fileUrl is null !!!");
        }
        if (StringUtils.isEmpty(rootDir)){
            throw new Exception("rootDir is null !!!");
        }
        if (StringUtils.isEmpty(fileType)){
            throw new Exception("fileType is null !!!");
        }
        if (StringUtils.isEmpty(fileSize)){
            throw new Exception("fileSize is null !!!");
        }

        DefaultHsjryFileServiceImpl hsjryFileService = new DefaultHsjryFileServiceImpl();
        hsjryFileService.setFileSize(fileSize);
        hsjryFileService.setFileType(fileType);
        hsjryFileService.setRootDir(rootDir);
        hsjryFileService.setUrl(fileUrl);
        return hsjryFileService;
    }
}
