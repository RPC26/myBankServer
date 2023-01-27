package com.ruben.mybank.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ruben.mybank.entity.OperacionEntity;

public interface OperacionRepository extends JpaRepository<OperacionEntity, Long> {

        Page<OperacionEntity> findByReceptorCuentaEntityId(Long id_cuentareceptor, Pageable oPageable);

        @Query(value = "SELECT * FROM operacion WHERE id_cuentareceptor = ?1 AND fechahoraoperacion LIKE %?2%", nativeQuery = true)
        Page<OperacionEntity> findByReceptorCuentaEntityIdAndFechahoraoperacionContaining(
                        Long id_cuentareceptor, String strFilter, Pageable oPageable);

        Page<OperacionEntity> findByReceptorCuentaEntityIdAndEmisorCuentaEntityId(Long id_cuentareceptor,
                        Long id_cuentaemisor, Pageable oPageable);

        @Query(value = "SELECT * FROM operacion WHERE id_cuentareceptor = ?1 AND id_cuentaemisor = ?2 AND fechahoraoperacion LIKE %?3%", nativeQuery = true)
        Page<OperacionEntity> findByReceptorCuentaEntityIdAndEmisorCuentaEntityIdAndFechahoraoperacionContaining(
                        Long id_cuentareceptor, Long id_cuentaemisor, String strFilter, Pageable oPageable);

        Page<OperacionEntity> findByReceptorCuentaEntityIdAndEmisorCuentaEntityIdAndTipooperacionId(
                        Long id_cuentareceptor,
                        Long id_cuentaemisor, Long id_tipoOperacion, Pageable oPageable);

        @Query(value = "SELECT * FROM operacion WHERE id_cuentareceptor = ?1 AND id_cuentaemisor = ?2 AND id_tipooperacion = ?3 AND fechahoraoperacion LIKE %?4%", nativeQuery = true)
        Page<OperacionEntity> findByReceptorCuentaEntityIdAndEmisorCuentaEntityIdAndTipooperacionIdAndFechahoraoperacionContaining(
                        Long id_cuentareceptor, Long id_cuentaemisor, Long id_tipoOperacion, String strFilter,
                        Pageable oPageable);

        boolean existsByEmisorCuentaEntityId(Long idCuentaEmisor);

        Page<OperacionEntity> findByEmisorCuentaEntityId(Long idEmisor, Pageable oPageable);

        Page<OperacionEntity> findByTipooperacionId(Long id_tipoOperacion, Pageable oPageable);

        @Query(value = "SELECT * FROM operacion WHERE id_tipooperacion = ?1 AND fechahoraoperacion LIKE %?2%", nativeQuery = true)
        Page<OperacionEntity> findByTipooperacionIdAndFechahoraoperacionContaining(
                        Long id_tipoOperacion, String strFilter, Pageable oPageable);

        Page<OperacionEntity> findByTipooperacionIdAndEmisorCuentaEntityId(Long id_tipoOperacion, Long id_cuentaemisor,
                        Pageable oPageable);

        @Query(value = "SELECT * FROM operacion WHERE id_tipooperacion = ?1 AND id_cuentaemisor = ?2 AND fechahoraoperacion LIKE %?3%", nativeQuery = true)
        Page<OperacionEntity> findByTipooperacionIdAndEmisorCuentaEntityIdAndFechahoraoperacionContaining(
                        Long id_tipoOperacion, Long id_cuentaemisor, String strFilter, Pageable oPageable);

        @Query(value = "SELECT * FROM operacion WHERE id_cuentaemisor = ?1 AND fechahoraoperacion LIKE %?2%", nativeQuery = true)
        Page<OperacionEntity> findByEmisorCuentaEntityIdAndFechahoraoperacionContaining(
                        Long id_cuentaemisor, String strFilter, Pageable oPageable);

        Page<OperacionEntity> findByEmisorCuentaEntityIdAndReceptorCuentaEntityId(Long id_cuentaemisor,
                        Long id_cuentareceptor,
                        Pageable oPageable);

        @Query(value = "SELECT * FROM operacion WHERE id_cuentaemisor = ?1 AND id_cuentareceptor = ?2 AND fechahoraoperacion LIKE %?3%", nativeQuery = true)
        Page<OperacionEntity> findByEmisorCuentaEntityIdAndReceptorCuentaEntityIdAndFechahoraoperacionContaining(
                        Long id_cuentaemisor, Long id_cuentareceptor, String strFilter, Pageable oPageable);

        @Query(value = "SELECT * FROM operacion WHERE fechahoraoperacion LIKE %?1%", nativeQuery = true)
        Page<OperacionEntity> findByFechahoraoperacionContaining(String strFilter,
                        Pageable oPageable);

        List<OperacionEntity> findByEmisorCuentaEntityIdOrReceptorCuentaEntityId(Long idEmisor, Long idEmisor2);

        @Query(value = "SELECT * FROM operacion WHERE id_cuentaemisor = ?1 OR id_cuentareceptor = ?2", nativeQuery = true)
        List<OperacionEntity> allOperacionesCuenta(Long idEmisor, Long idEmisor2);

}
