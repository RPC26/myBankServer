package com.ruben.mybank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ruben.mybank.bean.SaldoUsuario;
import com.ruben.mybank.bean.SaldoCuenta;
import com.ruben.mybank.entity.CuentaEntity;
import com.ruben.mybank.entity.OperacionEntity;
import com.ruben.mybank.entity.UsuarioEntity;
import com.ruben.mybank.exception.CannotPerformOperationException;
import com.ruben.mybank.exception.ResourceNotFoundException;
import com.ruben.mybank.exception.ResourceNotModifiedException;
import com.ruben.mybank.helper.RandomHelper;
import com.ruben.mybank.helper.TipoCuentaHelper;
import com.ruben.mybank.helper.ValidationHelper;
import com.ruben.mybank.repository.CuentaRepository;
import com.ruben.mybank.repository.OperacionRepository;
import com.ruben.mybank.repository.TipocuentaRepository;
import com.ruben.mybank.repository.UsuarioRepository;

import java.time.LocalDateTime;
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
    TipocuentaService oTipocuentaService;

    @Autowired
    CuentaRepository oCuentaRepository;

    @Autowired
    OperacionRepository oOperacionRepository;

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
        return oCuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("id " + id + " not exist"));
    }

    public Long count() {
        // oAuthService.OnlyAdmins();
        return oCuentaRepository.count();
    }

    public Page<CuentaEntity> getPage(Pageable oPageable, String strFilter, Long id_tipocuenta, Long id_usuario) {
        // oAuthService.OnlyAdmins();
        ValidationHelper.validateRPP(oPageable.getPageSize());
        Page<CuentaEntity> oPage = null;

        // Pasar id_usuario
        if (id_tipocuenta == null && id_usuario != null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oCuentaRepository.findByUsuarioId(id_usuario, oPageable);
            }

            oPage = oCuentaRepository.findByUsuarioIdAndIbanIgnoreCaseContaining(id_usuario, strFilter, oPageable);
        }

        // Pasar id_tipocuenta
        if (id_tipocuenta != null && id_usuario == null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oCuentaRepository.findByTipocuentaId(id_tipocuenta, oPageable);
            }

            oPage = oCuentaRepository.findByTipocuentaIdAndIbanIgnoreCaseContaining(id_tipocuenta, strFilter,
                    oPageable);
        }

        // Pasando todo
        if (id_tipocuenta != null && id_usuario != null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oCuentaRepository.findByTipocuentaIdAndUsuarioId(id_tipocuenta, id_usuario, oPageable);
            }

            oPage = oCuentaRepository.findByTipocuentaIdAndUsuarioIdAndIbanIgnoreCaseContaining(id_tipocuenta,
                    id_usuario, strFilter, oPageable);
        }

        // Pasando nada
        if (id_tipocuenta == null && id_usuario == null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oCuentaRepository.findAll(oPageable);
            }

            oPage = oCuentaRepository.findByIbanIgnoreCaseContaining(strFilter, oPageable);
        }

        return oPage;
    }

    public Long create(CuentaEntity oNewCuentaEntity) {
        // oAuthService.OnlyAdmins();
        oNewCuentaEntity.setId(0L);
        oNewCuentaEntity.setFechacreacion(LocalDateTime.now());
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
            return id;
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
            return oCuentaRepository.findById(cuentaList.get(iPosicion).getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "id " + cuentaList.get(iPosicion).getId() + " not found"));
        } else {
            throw new CannotPerformOperationException("ho hay usuarios en la base de datos");
        }
    }

    private CuentaEntity generateRandomCuenta() {
        CuentaEntity oCuentaEntity = new CuentaEntity();
        oCuentaEntity.setFechacreacion(RandomHelper.getRadomDateTime());
        oCuentaEntity.setIban(RandomHelper.getRandomIban());
        oCuentaEntity.setUsuario(usuarioRandom());
        if (RandomHelper.getRandomInt(0, 10) > 5) {
            oCuentaEntity.setTipocuenta(oTipocuentaService.get(TipoCuentaHelper.NEGOCIOS));
        } else {
            oCuentaEntity.setTipocuenta(oTipocuentaService.get(TipoCuentaHelper.CORRIENTE));
        }
        return oCuentaEntity;
    }

    private UsuarioEntity usuarioRandom() {
        List<UsuarioEntity> allUsers = oUsuarioRepository.findAll();
        UsuarioEntity random = allUsers.get(RandomHelper.getRandomInt(0, allUsers.size() - 1));
        return random;
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

    public SaldoCuenta saldo(Long id) {

        CuentaEntity cuenta = oCuentaRepository.findById(id).get();
        SaldoCuenta saldoCuenta = new SaldoCuenta();
        saldoCuenta.setIdCuenta(id);

        List<OperacionEntity> operaciones = oOperacionRepository.allOperacionesCuenta(cuenta.getId(),
                cuenta.getId());

        double balanceTotal = 0;

        // Balance de la cuenta
        for (OperacionEntity operacion : operaciones) {

            Long tipoOperacion = operacion.getTipooperacion().getId();
            double cantidadOperacion = operacion.getCantidad();

            if (tipoOperacion == 1L) {
                balanceTotal += cantidadOperacion;
            }

            if (tipoOperacion == 2L
                    || (tipoOperacion == 3L && operacion.getReceptorCuentaEntity().getId() != cuenta.getId())) {
                balanceTotal -= cantidadOperacion;
            }

            if (operacion.getReceptorCuentaEntity() != null
                    && operacion.getReceptorCuentaEntity().getId() == cuenta.getId()) {
                balanceTotal += cantidadOperacion;
            }
        }

        Long idTipocuenta = cuenta.getTipocuenta().getId();
        double balanceBeneficio = 0;

        if (idTipocuenta == TipoCuentaHelper.NEGOCIOS) {
            double porcentaje = (balanceTotal * 0.5);

            balanceBeneficio = balanceTotal + porcentaje;
        }

         if (idTipocuenta == TipoCuentaHelper.CORRIENTE) {
            double porcentaje = (balanceTotal * 0.3);

            balanceBeneficio = balanceTotal + porcentaje;
        }
        
        saldoCuenta.setSaldoBeneficio(balanceBeneficio);
        saldoCuenta.setSaldoReal(balanceTotal);

        return saldoCuenta;
    }
}
