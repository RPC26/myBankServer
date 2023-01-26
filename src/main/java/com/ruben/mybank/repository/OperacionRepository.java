package com.ruben.mybank.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ruben.mybank.entity.OperacionEntity;

public interface OperacionRepository extends JpaRepository<OperacionEntity, Long> {

    Page<OperacionEntity> findByReceptorCuentaEntityId(Long id_cuentareceptor, Pageable oPageable);

    Page<OperacionEntity> findByReceptorCuentaEntityIdAndCantidadContainingOrFechahoraoperacionContaining(
            Long id_cuentareceptor, String strFilter, String strFilter2, Pageable oPageable);

    Page<OperacionEntity> findByReceptorCuentaEntityIdAndEmisorCuentaEntityId(Long id_cuentareceptor,
            Long id_cuentaemisor, Pageable oPageable);

    Page<OperacionEntity> findByReceptorCuentaEntityIdAndEmisorCuentaEntityIdAndCantidadContainingOrFechahoraoperacionContaining(
            Long id_cuentareceptor, Long id_cuentaemisor, String strFilter, String strFilter2, Pageable oPageable);

    Page<OperacionEntity> findByReceptorCuentaEntityIdAndEmisorCuentaEntityIdAndTipooperacionId(Long id_cuentareceptor,
            Long id_cuentaemisor, Long id_tipoOperacion, Pageable oPageable);

    Page<OperacionEntity> findByReceptorCuentaEntityIdAndEmisorCuentaEntityIdAndTipooperacionIdAndCantidadContainingOrFechahoraoperacionContaining(
            Long id_cuentareceptor, Long id_cuentaemisor, Long id_tipoOperacion, String strFilter, String strFilter2,
            Pageable oPageable);

    boolean existsByEmisorCuentaEntityId(Long idCuentaEmisor);

    Page<OperacionEntity> findByEmisorCuentaEntityId(Long idEmisor, Pageable oPageable);

    Page<OperacionEntity> findByTipooperacionId(Long id_tipoOperacion, Pageable oPageable);
    

    Page<OperacionEntity> findByTipooperacionIdAndCantidadContainingOrFechahoraoperacionContaining(
            Long id_tipoOperacion, String strFilter, String strFilter2, Pageable oPageable);

    Page<OperacionEntity> findByTipooperacionIdAndEmisorCuentaEntityId(Long id_tipoOperacion, Long id_cuentaemisor,
            Pageable oPageable);

    Page<OperacionEntity> findByTipooperacionIdAndEmisorCuentaEntityIdAndCantidadContainingOrFechahoraoperacionContaining(
            Long id_tipoOperacion, Long id_cuentaemisor, String strFilter, String strFilter2, Pageable oPageable);

Page<OperacionEntity> findByEmisorCuentaEntityIdAndCantidadContainingOrFechahoraoperacionContaining(
                Long id_cuentaemisor, String strFilter, String strFilter2, Pageable oPageable);

Page<OperacionEntity> findByEmisorCuentaEntityIdAndReceptorCuentaEntityId(Long id_cuentaemisor, Long id_cuentareceptor,
        Pageable oPageable);

Page<OperacionEntity> findByEmisorCuentaEntityIdAndReceptorCuentaEntityIdAndCantidadContainingOrFechahoraoperacionContaining(
        Long id_cuentaemisor, Long id_cuentareceptor, String strFilter, String strFilter2, Pageable oPageable);

Page<OperacionEntity> findByCantidadContainingOrFechahoraoperacionContaining(String strFilter, String strFilter2,
        Pageable oPageable);

List<OperacionEntity> findByEmisorCuentaEntityIdOrReceptorCuentaEntityId(Long idEmisor, Long idEmisor2);
    
@Query(value = "SELECT * FROM operacion WHERE id_cuentaemisor = ?1 OR id_cuentareceptor = ?2", nativeQuery = true)
List<OperacionEntity> allOperacionesCuenta(Long idEmisor, Long idEmisor2);
    
}
