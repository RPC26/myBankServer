package com.ruben.mybank.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import com.ruben.mybank.entity.TipocuentaEntity;
import com.ruben.mybank.exception.ResourceNotFoundException;
import com.ruben.mybank.helper.ValidationHelper;
import com.ruben.mybank.repository.TipocuentaRepository;

@Service
public class TipocuentaService {
    
    @Autowired
    TipocuentaRepository oTipocuentaRepository;

    @Autowired
    AuthService oAuthService;

    public void validate(Long id) {
        if (!oTipocuentaRepository.existsById(id)) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }


    //porcentajeBeneficio está contemplado como un double pero no es correcto
    public List<TipocuentaEntity> generateTipoCuenta() {
        List<TipocuentaEntity> tipoCuentaLista = new ArrayList<>();
        tipoCuentaLista.add(new TipocuentaEntity(1L, "negocios", 5.0));
        tipoCuentaLista.add(new TipocuentaEntity(2L, "corriente", 3.0));
        return tipoCuentaLista;
    }

    public TipocuentaEntity get(Long id) {
        validate(id);
        return oTipocuentaRepository.getReferenceById(id);
    }

    public List<TipocuentaEntity> all() {
        return oTipocuentaRepository.findAll();
    }

    public Long count() {
        return oTipocuentaRepository.count();
    }

    public Page<TipocuentaEntity> getPage(Pageable oPageable, String strFilter) {
        ValidationHelper.validateRPP(oPageable.getPageSize());
        Page<TipocuentaEntity> oPage = null;
        if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
            oPage = oTipocuentaRepository.findAll(oPageable);
        } else {
            oPage = oTipocuentaRepository.findByNombreIgnoreCaseContaining(strFilter, oPageable);
        }
        return oPage;
    }

    public Long update(TipocuentaEntity oTipocuentaEntity) {
        // oAuthService.OnlyAdmins();
        validate(oTipocuentaEntity.getId());
        return oTipocuentaRepository.save(oTipocuentaEntity).getId();
    }

    public Long generate() {
        // oAuthService.OnlyAdmins();
        List<TipocuentaEntity> tipoCuentaLista = generateTipoCuenta();

        for (int i = tipoCuentaLista.size() - 1; i >= 0; i--) {
            oTipocuentaRepository.save(tipoCuentaLista.get(i));
        }
        return oTipocuentaRepository.count();
    }

}
