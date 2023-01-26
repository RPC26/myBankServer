package com.ruben.mybank.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ruben.mybank.entity.CuentaEntity;

public interface CuentaRepository extends JpaRepository<CuentaEntity, Long> {

    Page<CuentaEntity> findByTipocuentaId(Long id_tipocuenta, Pageable oPageable);
    Page<CuentaEntity> findByIbanIgnoreCaseContaining(String iban, Pageable oPageable);
    Page<CuentaEntity> findByUsuarioId(Long id_usuario, Pageable oPageable);
    Page<CuentaEntity> findByUsuarioIdAndIbanIgnoreCaseContaining(Long id_usuario, String strFilter, Pageable oPageable);
    Page<CuentaEntity> findByTipocuentaIdAndIbanIgnoreCaseContaining(Long id_tipocuenta, String strFilter, Pageable oPageable);
    List<CuentaEntity> findByUsuarioId(Long id);
    Page<CuentaEntity> findByTipocuentaIdAndUsuarioIdAndIbanIgnoreCaseContaining(Long id_tipocuenta, Long id_usuario,
            String strFilter, Pageable oPageable);
    Page<CuentaEntity> findByTipocuentaIdAndUsuarioId(Long id_tipocuenta, Long id_usuario, Pageable oPageable);

}
