package com.wandaph.filetarnsfer.config;

import com.wandaph.filetarnsfer.model.entity.Filetransfer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author lipeng
 * @Title: ThreeInfoConfig
 * @ProjectName wdph-filetransfer
 * @Description: TODO
 * @date 2018/9/317:22
 */
@Configuration
@ConfigurationProperties("filetransferConfig")
public class FiletransferConfig {

        private List<Filetransfer> filetransfer;

        public List<Filetransfer> getFiletransfer() {
                return filetransfer;
        }

        public void setFiletransfer(List<Filetransfer> filetransfer) {
                this.filetransfer = filetransfer;
        }
}
