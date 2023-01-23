package com.ruben.mybank.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ruben.mybank.entity.TipooperacionEntity;

public interface TipooperacionRepository extends JpaRepository<TipooperacionEntity, Long> {

    Page<TipooperacionEntity> findByNombreIgnoreCaseContaining(String strFilter, Pageable oPageable);

   
    
}
