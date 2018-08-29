package com.wandaph.filetarnsfer.interceptor.adapter;

import com.wandaph.filetarnsfer.interceptor.SignInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author lipeng
 * @Title: SignInterceptorAdapter
 * @ProjectName wandaph-filetransfer
 * @Description: TODO
 * @date 2018/8/249:42
 */
@SpringBootConfiguration
public class SignInterceptorAdapter extends WebMvcConfigurerAdapter{

    @Autowired
    private SignInterceptor signInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(signInterceptor).addPathPatterns("/**");
    }
}
