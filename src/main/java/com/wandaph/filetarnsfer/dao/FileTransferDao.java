package com.wandaph.filetarnsfer.dao;

import com.wandaph.filetarnsfer.model.entity.ContractTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;

public interface FileTransferDao extends JpaRepository<ContractTransfer,Long> ,JpaSpecificationExecutor<ContractTransfer>,Serializable {

}
