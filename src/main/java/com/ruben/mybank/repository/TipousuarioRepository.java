package com.ruben.mybank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ruben.mybank.entity.TipousuarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TipousuarioRepository extends JpaRepository<TipousuarioEntity, Long> {

    public Page<TipousuarioEntity> findByNombreIgnoreCaseContaining(String strFilter, Pageable oPageable);

}
