package com.wandaph.filetarnsfer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author lipeng
 * @Title: Config
 * @ProjectName wandaph-filetransfer
 * @Description: TODO
 * @date 2018/8/2715:03
 */

@Configuration
@ImportResource(locations= {"classpath:application-bean/*.xml"})
public class Config {
}
