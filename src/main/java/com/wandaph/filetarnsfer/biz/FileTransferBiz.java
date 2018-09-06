package com.wandaph.filetarnsfer.biz;

import com.wandaph.filetarnsfer.model.request.FileTransferRequest;
import com.wandaph.filetarnsfer.model.response.FileTransferResponse;

import java.io.InputStream;

public interface FileTransferBiz {

    public FileTransferResponse contractUploadBase64(FileTransferRequest request);
    public FileTransferResponse contractUploadInputSteam(FileTransferRequest request, InputStream inputStream);

}
