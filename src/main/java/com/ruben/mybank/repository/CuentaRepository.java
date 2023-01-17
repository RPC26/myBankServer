package com.ruben.mybank.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ruben.mybank.entity.CuentaEntity;

public interface CuentaRepository extends JpaRepository<CuentaEntity, Long> {

    Page<CuentaEntity> findByTipocuentaId(Long id_tipocuenta, Pageable oPageable);
    Page<CuentaEntity> findByNombreIgnoreCaseContaining(String strFilter, Pageable oPageable);
    Page<CuentaEntity> findByTipousuarioIdAndDniIgnoreCaseContainingOrNombreIgnoreCaseContainingOrApellido1IgnoreCaseContainingOrApellido2IgnoreCaseContaining(
            Long id_tipocuenta, String strFilter, String strFilter2, String strFilter3, String strFilter4,
            Pageable oPageable);

    
}
