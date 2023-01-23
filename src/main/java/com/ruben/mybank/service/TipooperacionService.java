package com.ruben.mybank.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import com.ruben.mybank.entity.TipooperacionEntity;
import com.ruben.mybank.exception.ResourceNotFoundException;
import com.ruben.mybank.helper.ValidationHelper;
import com.ruben.mybank.repository.TipooperacionRepository;

@Service
public class TipooperacionService {
    
    @Autowired
    TipooperacionRepository oTipooperacionRepository;

    @Autowired
    AuthService oAuthService;

    public void validate(Long id) {
        if (!oTipooperacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }


    //porcentajeBeneficio está contemplado como un double pero no es correcto
    public List<TipooperacionEntity> generateTipooperacion() {
        List<TipooperacionEntity> tipooperacionLista = new ArrayList<>();
        tipooperacionLista.add(new TipooperacionEntity());
        tipooperacionLista.add(new TipooperacionEntity());
        return tipooperacionLista;
    }

    public TipooperacionEntity get(Long id) {
        validate(id);
        return oTipooperacionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("id "+id+" not found"));
    }

    public List<TipooperacionEntity> all() {
        return oTipooperacionRepository.findAll();
    }

    public Long count() {
        return oTipooperacionRepository.count();
    }

    public Page<TipooperacionEntity> getPage(Pageable oPageable, String strFilter) {
        ValidationHelper.validateRPP(oPageable.getPageSize());
        Page<TipooperacionEntity> oPage = null;
        if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
            oPage = oTipooperacionRepository.findAll(oPageable);
        } else {
            oPage = oTipooperacionRepository.findByNombreIgnoreCaseContaining(strFilter, oPageable);
        }
        return oPage;
    }

    public Long update(TipooperacionEntity oTipooperacionEntity) {
        // oAuthService.OnlyAdmins();
        validate(oTipooperacionEntity.getId());
        return oTipooperacionRepository.save(oTipooperacionEntity).getId();
    }

    public Long generate() {
        // oAuthService.OnlyAdmins();
        oTipooperacionRepository.saveAll(generateTipooperacion());
        return oTipooperacionRepository.count();
    }

}

