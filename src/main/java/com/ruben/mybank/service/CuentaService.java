package com.ruben.mybank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ruben.mybank.entity.CuentaEntity;
import com.ruben.mybank.exception.CannotPerformOperationException;
import com.ruben.mybank.exception.ResourceNotFoundException;
import com.ruben.mybank.exception.ResourceNotModifiedException;
import com.ruben.mybank.helper.RandomHelper;
import com.ruben.mybank.helper.TipoCuentaHelper;
import com.ruben.mybank.helper.ValidationHelper;
import com.ruben.mybank.repository.CuentaRepository;
import com.ruben.mybank.repository.TipocuentaRepository;
import com.ruben.mybank.repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

@Service

public class CuentaService {

    @Autowired
    TipousuarioService oTipousuarioService;

    @Autowired
    UsuarioService oUsuarioService;

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    TipocuentaRepository oTipocuentaRepository;

    @Autowired
    CuentaRepository oCuentaRepository;

    @Autowired
    AuthService oAuthService;

    private final String DNI_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";
    private final String MYBANK_PASSWORD = "655e786674d9d3e77bc05ed1de37b4b6bc89f788829f9f3c679e7687b410c89b"; // prueba
    private final String[] NAMES = { "Jose", "Mark", "Elen", "Toni", "Hector", "Jose", "Laura", "Vika", "Sergio",
            "Javi", "Marcos", "Pere", "Daniel", "Jose", "Javi", "Sergio", "Aaron", "Rafa", "Lionel", "Borja" };

    private final String[] SURNAMES = { "Penya", "Tatay", "Coronado", "Cabanes", "Mikayelyan", "Gil", "Martinez",
            "Bargues", "Raga", "Santos", "Sierra", "Arias", "Santos", "Kuvshinnikova", "Cosin", "Frejo", "Marti",
            "Valcarcel", "Sesa", "Lence", "Villanueva", "Peyro", "Navarro", "Navarro", "Primo", "Gil", "Mocholi",
            "Ortega", "Dung", "Vi", "Sanchis", "Merida", "Aznar", "Aparici", "Tarazón", "Alcocer", "Salom",
            "Santamaría" };

    public void validate(Long id) {
        if (!oCuentaRepository.existsById(id)) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }

    // pendiente de revisar
    public void validate2(CuentaEntity oCuentaEntity) {
        Long idUsuario = oCuentaEntity.getUsuario().getId();
        Long idTipoCuenta = oCuentaEntity.getTipocuenta().getId();
        if (!oUsuarioRepository.existsById(idUsuario) && !oTipocuentaRepository.existsById(idTipoCuenta)) {
            throw new ResourceNotFoundException(
                    "id tipoUsuario " + idUsuario + " not exist" + "or id tipoCuenta" + idTipoCuenta);
        }
    }

    public CuentaEntity get(Long id) {
        // oAuthService.OnlyAdmins();
        try {
            return oCuentaRepository.findById(id).get();
        } catch (Exception ex) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }

    public Long count() {
        // oAuthService.OnlyAdmins();
        return oCuentaRepository.count();
    }

    public Page<CuentaEntity> getPage(Pageable oPageable, String strFilter, Long id_tipocuenta) {
        // oAuthService.OnlyAdmins();
        ValidationHelper.validateRPP(oPageable.getPageSize());
        Page<CuentaEntity> oPage = null;
        if (id_tipocuenta == null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oCuentaRepository.findAll(oPageable);
            } else {
                // oPage = oCuentaRepository
                // .findByDniIgnoreCaseContainingOrNombreIgnoreCaseContainingOrApellido1IgnoreCaseContainingOrApellido2IgnoreCaseContaining(
                // strFilter, strFilter, strFilter, strFilter, oPageable);
            }
        } else {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oCuentaRepository.findByTipocuentaId(id_tipocuenta, oPageable);
            } else {
                // oPage = oCuentaRepository
                // .findByTipousuarioIdAndDniIgnoreCaseContainingOrNombreIgnoreCaseContainingOrApellido1IgnoreCaseContainingOrApellido2IgnoreCaseContaining(
                // id_tipocuenta, strFilter, strFilter, strFilter, strFilter, oPageable);
            }
        }
        return oPage;
    }

    public Long create(CuentaEntity oNewCuentaEntity) {
        // oAuthService.OnlyAdmins();
        validate2(oNewCuentaEntity);
        oNewCuentaEntity.setId(0L);
        return oCuentaRepository.save(oNewCuentaEntity).getId();
    }

    @Transactional
    public Long update(CuentaEntity oCuentaEntity) {
        validate(oCuentaEntity.getId());
        // oAuthService.OnlyAdmins();
        validate2(oCuentaEntity);
        if (oAuthService.isAdmin()) {
            return update4Admins(oCuentaEntity).getId();
        } else {
            return update4Users(oCuentaEntity).getId();
        }
    }

    @Transactional
    private CuentaEntity update4Admins(CuentaEntity oUpdatedCuentaEntity) {
        CuentaEntity oCuentaEntity = oCuentaRepository.findById(oUpdatedCuentaEntity.getId()).get();
        // keeping login password token & validado
        oCuentaEntity.setUsuario(oUpdatedCuentaEntity.getUsuario());
        oCuentaEntity.setTipocuenta(oUpdatedCuentaEntity.getTipocuenta());
        oCuentaEntity.setIban(oUpdatedCuentaEntity.getIban());
        return oCuentaRepository.save(oCuentaEntity);
    }

    @Transactional
    private CuentaEntity update4Users(CuentaEntity oUpdatedCuentaEntity) {
        CuentaEntity oCuentaEntity = oCuentaRepository.findById(oUpdatedCuentaEntity.getId()).get();
        // keeping login password token & validado descuento activo tipousuario
        oCuentaEntity.setUsuario(oUpdatedCuentaEntity.getUsuario());
        oCuentaEntity.setTipocuenta(oUpdatedCuentaEntity.getTipocuenta());
        oCuentaEntity.setIban(oUpdatedCuentaEntity.getIban());
        return oCuentaRepository.save(oCuentaEntity);
    }

    public Long delete(Long id) {
        // oAuthService.OnlyAdmins();
        if (oCuentaRepository.existsById(id)) {
            oCuentaRepository.deleteById(id);
            if (oCuentaRepository.existsById(id)) {
                throw new ResourceNotModifiedException("can't remove register " + id);
            } else {
                return id;
            }
        } else {
            throw new ResourceNotModifiedException("id " + id + " not exist");
        }
    }

    public CuentaEntity generate() {
        // oAuthService.OnlyAdmins();
        return generateRandomCuenta();
    }

    public CuentaEntity getOneRandom() {
        if (count() > 0) {
            List<CuentaEntity> cuentaList = oCuentaRepository.findAll();
            int iPosicion = RandomHelper.getRandomInt(0, (int) oCuentaRepository.count() - 1);
            return oCuentaRepository.getReferenceById(cuentaList.get(iPosicion).getId());
        } else {
            throw new CannotPerformOperationException("ho hay usuarios en la base de datos");
        }
    }

    private CuentaEntity generateRandomCuenta() {
        CuentaEntity oCuentaEntity = new CuentaEntity();
        oCuentaEntity.setFechaCreacion(RandomHelper.getRadomDateTime());
        oCuentaEntity.setIban(RandomHelper.getRandomIban());
        oCuentaEntity.setUsuario(oUsuarioRepository.findById(idUsuarioRandom()).get());
        if (RandomHelper.getRandomInt(0, 10) > 1) {
            oCuentaEntity.setTipocuenta(oTipocuentaRepository.getReferenceById(TipoCuentaHelper.NEGOCIOS));
        } else {
            oCuentaEntity.setTipocuenta(oTipocuentaRepository.getReferenceById(TipoCuentaHelper.CORRIENTE));
        }
        return oCuentaEntity;
    }

    public Long idUsuarioRandom() {
        Long total = oUsuarioService.count();
        return RandomHelper.getRandomLongCuenta(1L, total);
    }

    public Long generateSome(Integer amount) {
        // oAuthService.OnlyAdmins();
        List<CuentaEntity> cuentaList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            CuentaEntity oCuentaEntity = generateRandomCuenta();
            cuentaList.add(oCuentaEntity);
        }
        oCuentaRepository.saveAll(cuentaList);
        return oCuentaRepository.count();
    }

    private String getFromList(List<String> list) {
        int randomNumber = RandomHelper.getRandomInt(0, list.size() - 1);
        String value = list.get(randomNumber);
        list.remove(randomNumber);
        return value;
    }

}
