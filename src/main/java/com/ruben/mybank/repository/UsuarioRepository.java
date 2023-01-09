package com.ruben.mybank.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import com.ruben.mybank.entity.UsuarioEntity;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    
    public Page<UsuarioEntity> findByNombreIgnoreCaseContaining(String strFilter, Pageable oPageable);

    public UsuarioEntity findByLoginAndPassword(String login, String password);

    boolean existsByLogin(String login);

    public Page<UsuarioEntity> findByDniIgnoreCaseContainingOrNombreIgnoreCaseContainingOrApellido1IgnoreCaseContainingOrApellido2IgnoreCaseContaining(
            String strFilter, String strFilter2, String strFilter3, String strFilter4, Pageable oPageable);

    public Page<UsuarioEntity> findByTipousuarioId(Long lTipoUsuario, Pageable oPageable);

    public Page<UsuarioEntity> findByTipousuarioIdAndDniIgnoreCaseContainingOrNombreIgnoreCaseContainingOrApellido1IgnoreCaseContainingOrApellido2IgnoreCaseContaining(
            Long idTipoUsuario, String strFilter, String strFilter2, String strFilter3, String strFilter4,
            Pageable oPageable);

public UsuarioEntity findByLogin(String strUsuarioName);
}
