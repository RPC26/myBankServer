package com.ruben.mybank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.ruben.mybank.bean.OperacionesDashboard;

import com.ruben.mybank.entity.CuentaEntity;
import com.ruben.mybank.entity.OperacionEntity;
import com.ruben.mybank.entity.TipocuentaEntity;
import com.ruben.mybank.entity.TipooperacionEntity;
import com.ruben.mybank.entity.OperacionEntity;
import com.ruben.mybank.entity.UsuarioEntity;
import com.ruben.mybank.exception.CannotPerformOperationException;
import com.ruben.mybank.exception.ResourceNotFoundException;
import com.ruben.mybank.exception.ResourceNotModifiedException;
import com.ruben.mybank.helper.RandomHelper;
import com.ruben.mybank.helper.TipoOperacionHelper;
import com.ruben.mybank.helper.ValidationHelper;
import com.ruben.mybank.repository.CuentaRepository;
import com.ruben.mybank.repository.OperacionRepository;
import com.ruben.mybank.repository.TipooperacionRepository;
import com.ruben.mybank.repository.UsuarioRepository;
import com.ruben.mybank.helper.TipoOperacionHelper;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.xml.bind.ValidationException;

@Service
public class OperacionService {

    @Autowired
    TipooperacionRepository oTipooperacionRepository;

    @Autowired
    OperacionRepository oOperacionRepository;

    @Autowired
    CuentaRepository oCuentaRepository;

    @Autowired
    AuthService oAuthService;

    public void validate(Long id) {
        if (!oOperacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }

    // pendiente de revisar
    public void validateExistsTransferencia(OperacionEntity oOperacionEntity) {
        Long idCuentaEmisor = oOperacionEntity.getEmisorCuentaEntity().getId();
        Long idCuentaReceptor = oOperacionEntity.getReceptorCuentaEntity().getId();

        if (!oOperacionRepository.existsByEmisorCuentaEntityId(idCuentaEmisor)) {
            throw new ResourceNotFoundException(
                    "id CuentaEmisor " + idCuentaEmisor + " not exist" + "or id CuentaReceptor" + idCuentaReceptor);
        }
    }

    public int operacionesHoy() {
        LocalDateTime hoy = LocalDateTime.now();
        String hoyString = hoy.toString();
        String[] formatted = hoyString.split("[T]");

        List<OperacionEntity> operacionesHoy = oOperacionRepository.allOperacionesHoy(formatted[0]);

        return operacionesHoy.size();
    }

    // public OperacionesDashboard getSaldoAllCuentasByMes() {
    //     UsuarioEntity loggedUser = oAuthService.check();
    //     OperacionesDashboard dataset = new OperacionesDashboard();
    //     dataset.setLabel("Saldo por mes");

    //     List<CuentaEntity> cuentasUsuario = oCuentaRepository.findByUsuarioId(loggedUser.getId());

    //     double balanceTotal = 0;
    //     for (CuentaEntity cuenta : cuentasUsuario) {

    //         LocalDate añoActual = LocalDate.now();
    //         String fechaString = añoActual.toString();
    //         String[] añoString = fechaString.split("[-]");

    //         for (int i = 0; i < 12; i++) {
    //             String mes = "";

    //             if (i < 10) {
    //                 mes = añoString[0] + "-0" + i;
    //             } else {
    //                 mes = añoString[0] + "-" + i;
    //             }

    //             List<OperacionEntity> operacionesCuenta = oOperacionRepository.allOperacionesCuentaFecha(mes,
    //                     cuenta.getId());
    //         }

    //     }

    // }

    public List<OperacionesDashboard> getAllOperaciones(Long id_cuenta) {
        List<OperacionesDashboard> datasetsAll = new ArrayList<>();

        String[] tipoOperaciones = { "Ingreso", "Retirada", "Transferencia" };

        for (int i = 1; i <= 3; i++) {
            OperacionesDashboard dataset = new OperacionesDashboard();
            dataset.setLabel(tipoOperaciones[i - 1]);

            List<Integer> data = new ArrayList<>();

            LocalDate añoActual = LocalDate.now();
            String fechaString = añoActual.toString();
            String[] añoString = fechaString.split("[-]");

            for (int j = 1; j <= 12; j++) {

                String mes = "";

                if (j < 10) {
                    mes = añoString[0] + "-0" + j;
                } else {
                    mes = añoString[0] + "-" + j;
                }

                Integer countOperacionesMes = null;

                if (id_cuenta != 0L) {
                    countOperacionesMes = oOperacionRepository.operacionPorTipoMesCuenta(Long.valueOf(i), mes,
                            id_cuenta);
                } else {
                    countOperacionesMes = oOperacionRepository.operacionPorTipoMes(Long.valueOf(i), mes);
                }

                data.add(countOperacionesMes);
            }

            dataset.setData(data);
            datasetsAll.add(dataset);
        }

        return datasetsAll;
    }

    public void validateTransferencia(OperacionEntity oOperacionEntity) {
        boolean activa = oOperacionEntity.getEmisorCuentaEntity().isActiva();

        if (!activa) {
            throw new CannotPerformOperationException("Cuenta deshabilitada");
        }

        Long idEmisor = oOperacionEntity.getEmisorCuentaEntity().getId();
        Long tipoOperacionTransferencia = oOperacionEntity.getTipooperacion().getId();
        List<OperacionEntity> operacionesEmisor = oOperacionRepository.allOperacionesCuenta(idEmisor, idEmisor);

        double balanceTotal = 0;

        // Balance del emisor
        for (OperacionEntity operacion : operacionesEmisor) {

            Long tipoOperacion = operacion.getTipooperacion().getId();
            double cantidadOperacion = operacion.getCantidad();

            if (tipoOperacion == 1L) {
                balanceTotal += cantidadOperacion;
            }

            if (tipoOperacion == 2L
                    || (tipoOperacion == 3L && operacion.getReceptorCuentaEntity().getId() != idEmisor)) {
                balanceTotal -= cantidadOperacion;
            }

            if (operacion.getReceptorCuentaEntity() != null
                    && operacion.getReceptorCuentaEntity().getId() == idEmisor) {
                balanceTotal += cantidadOperacion;
            }
        }

        double maxNegativoEmisor = oOperacionEntity.getEmisorCuentaEntity().getTipocuenta().getMaxnegativo();
        double cantidadTransferencia = oOperacionEntity.getCantidad();

        if (cantidadTransferencia > balanceTotal + maxNegativoEmisor
                && (tipoOperacionTransferencia == 3 || tipoOperacionTransferencia == 2)) {
            throw new CannotPerformOperationException("");
        }
    }

    public OperacionEntity get(Long id) {
        // oAuthService.OnlyAdmins();
        OperacionEntity o = oOperacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("id " + id + " not exist"));

        return o;
    }

    public Long count() {
        // oAuthService.OnlyAdmins();
        return oOperacionRepository.count();
    }

    public Page<OperacionEntity> getPage(Pageable oPageable, String strFilter, Long id_tipoOperacion,
            Long id_cuentaemisor, Long id_cuentareceptor) {

        ValidationHelper.validateRPP(oPageable.getPageSize());
        Page<OperacionEntity> oPage = null;

        // Pasando solo id_receptor
        if (id_tipoOperacion == null && id_cuentaemisor == null && id_cuentareceptor != null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oOperacionRepository.findByReceptorCuentaEntityId(id_cuentareceptor, oPageable);
            }

            oPage = oOperacionRepository.findByReceptorCuentaEntityIdAndFechahoraoperacionContaining(id_cuentareceptor,
                    strFilter, oPageable);
        }

        // Pasando id_emisor y id_receptor
        if (id_tipoOperacion == null && id_cuentaemisor != null && id_cuentareceptor != null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oOperacionRepository.findByReceptorCuentaEntityIdAndEmisorCuentaEntityId(id_cuentareceptor,
                        id_cuentaemisor, oPageable);
            }

            oPage = oOperacionRepository
                    .findByReceptorCuentaEntityIdAndEmisorCuentaEntityIdAndFechahoraoperacionContaining(
                            id_cuentareceptor, id_cuentaemisor, strFilter, oPageable);
        }

        // Pasando id_emisor, id_receptor y id_tipocuenta
        if (id_tipoOperacion != null && id_cuentaemisor != null && id_cuentareceptor != null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oOperacionRepository.findByReceptorCuentaEntityIdAndEmisorCuentaEntityIdAndTipooperacionId(
                        id_cuentareceptor, id_cuentaemisor, id_tipoOperacion, oPageable);
            }

            oPage = oOperacionRepository
                    .findByReceptorCuentaEntityIdAndEmisorCuentaEntityIdAndTipooperacionIdAndFechahoraoperacionContaining(
                            id_cuentareceptor, id_cuentaemisor, id_tipoOperacion, strFilter, oPageable);
        }

        // Pasando solo id_tipooperacion
        if (id_tipoOperacion != null && id_cuentaemisor == null && id_cuentareceptor == null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oOperacionRepository.findByTipooperacionId(id_tipoOperacion, oPageable);
            }
            oPage = oOperacionRepository.findByTipooperacionIdAndFechahoraoperacionContaining(id_tipoOperacion,
                    strFilter, oPageable);

        }

        // Pasando id_tipooperacion y id_cuentaemisor
        if (id_tipoOperacion != null && id_cuentaemisor != null && id_cuentareceptor == null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oOperacionRepository.findByTipooperacionIdAndEmisorCuentaEntityId(id_tipoOperacion,
                        id_cuentaemisor, oPageable);
            }
            oPage = oOperacionRepository.findByTipooperacionIdAndEmisorCuentaEntityIdAndFechahoraoperacionContaining(
                    id_tipoOperacion, id_cuentaemisor, strFilter, oPageable);
        }

        // Pasando solo id_cuentaemisor
        if (id_tipoOperacion == null && id_cuentaemisor != null && id_cuentareceptor == null) {

            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oOperacionRepository.findByEmisorCuentaEntityId(id_cuentaemisor, oPageable);
            }
            oPage = oOperacionRepository.findByEmisorCuentaEntityIdAndFechahoraoperacionContaining(id_cuentaemisor,
                    strFilter, oPageable);

        }

        // Pasando id_cuentaemisor y id_cuentareceptor
        if (id_tipoOperacion == null && id_cuentaemisor != null && id_cuentareceptor != null) {

            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oOperacionRepository.findByEmisorCuentaEntityIdAndReceptorCuentaEntityId(id_cuentaemisor,
                        id_cuentareceptor, oPageable);
            }
            oPage = oOperacionRepository
                    .findByEmisorCuentaEntityIdAndReceptorCuentaEntityIdAndFechahoraoperacionContaining(id_cuentaemisor,
                            id_cuentareceptor, strFilter, oPageable);

        }

        // Pasando nada
        if (id_tipoOperacion == null && id_cuentaemisor == null && id_cuentareceptor == null) {

            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oOperacionRepository.findAll(oPageable);
            }
            oPage = oOperacionRepository.findByFechahoraoperacionContaining(strFilter, oPageable);

        }

        return oPage;
    }

    public Long create(OperacionEntity oNewOperacionEntity) {
        // oAuthService.OnlyAdmins();

        CuentaEntity emisor = oCuentaRepository.findById(oNewOperacionEntity.getEmisorCuentaEntity().getId()).get();
        TipooperacionEntity tipooperacion = oTipooperacionRepository
                .findById(oNewOperacionEntity.getTipooperacion().getId()).get();

        if (oNewOperacionEntity.getReceptorCuentaEntity() != null) {
            CuentaEntity receptor = oCuentaRepository.findById(oNewOperacionEntity.getReceptorCuentaEntity().getId())
                    .get();
            oNewOperacionEntity.setReceptorCuentaEntity(receptor);
        }

        oNewOperacionEntity.setEmisorCuentaEntity(emisor);
        oNewOperacionEntity.setTipooperacion(tipooperacion);

        if (oNewOperacionEntity.getFechahoraoperacion() == null) {
            oNewOperacionEntity.setFechahoraoperacion(LocalDateTime.now());
        }

        validateTransferencia(oNewOperacionEntity);
        return oOperacionRepository.save(oNewOperacionEntity).getId();
    }

    @Transactional
    public Long update(OperacionEntity oOperacionEntity) {
        oAuthService.OnlyAdmins();
        validate(oOperacionEntity.getId());

        CuentaEntity emisor = oCuentaRepository.findById(oOperacionEntity.getEmisorCuentaEntity().getId()).get();
        TipooperacionEntity tipooperacion = oTipooperacionRepository
                .findById(oOperacionEntity.getTipooperacion().getId()).get();

        if (oOperacionEntity.getReceptorCuentaEntity() != null) {
            CuentaEntity receptor = oCuentaRepository.findById(oOperacionEntity.getReceptorCuentaEntity().getId())
                    .get();
            oOperacionEntity.setReceptorCuentaEntity(receptor);
        }

        oOperacionEntity.setEmisorCuentaEntity(emisor);
        oOperacionEntity.setTipooperacion(tipooperacion);
        validateTransferencia(oOperacionEntity);
        return oOperacionRepository.save(oOperacionEntity).getId();
    }

    @Transactional
    private OperacionEntity update4Admins(OperacionEntity oUpdatedOperacionEntity) {
        oAuthService.OnlyAdmins();

        OperacionEntity oOperacionEntity = oOperacionRepository.findById(oUpdatedOperacionEntity.getId()).get();
        // keeping login password token & validado
        oOperacionEntity.setCantidad(oUpdatedOperacionEntity.getCantidad());
        oOperacionEntity.setEmisorCuentaEntity(oUpdatedOperacionEntity.getEmisorCuentaEntity());
        oOperacionEntity.setReceptorCuentaEntity(oUpdatedOperacionEntity.getReceptorCuentaEntity());
        oOperacionEntity.setTipooperacion(oUpdatedOperacionEntity.getTipooperacion());
        oOperacionEntity.setFechahoraoperacion(oUpdatedOperacionEntity.getFechahoraoperacion());
        return oOperacionRepository.save(oOperacionEntity);
    }

    public Long delete(Long id) {
        oAuthService.OnlyAdmins();
        if (oOperacionRepository.existsById(id)) {
            oOperacionRepository.deleteById(id);
            return id;
        } else {
            throw new ResourceNotModifiedException("id " + id + " not exist");
        }
    }

    public OperacionEntity generate() {
        oAuthService.OnlyAdmins();
        return generateRandomOperacion();
    }

    private OperacionEntity generateRandomOperacion() {
        OperacionEntity oOperacionEntity = new OperacionEntity();

        List<CuentaEntity> cuentas = oCuentaRepository.findAll();
        CuentaEntity emisorRandom = cuentas.get(RandomHelper.getRandomInt(0, cuentas.size() - 1));

        oOperacionEntity.setEmisorCuentaEntity(emisorRandom);
        oOperacionEntity.setReceptorCuentaEntity(null);
        oOperacionEntity.setFechahoraoperacion(RandomHelper.getRadomDateTime());
        oOperacionEntity.setTipooperacion(oTipooperacionRepository.findById(TipoOperacionHelper.INGRESO).get());
        oOperacionEntity.setCantidad(RandomHelper.getRadomDouble(1000, 99999));

        return oOperacionEntity;
    }

    public Long generateSome(Integer amount) {
        oAuthService.OnlyAdmins();
        List<OperacionEntity> OperacionList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            OperacionEntity oOperacionEntity = generateRandomOperacion();
            OperacionList.add(oOperacionEntity);
        }
        oOperacionRepository.saveAll(OperacionList);
        return oOperacionRepository.count();
    }

    public OperacionEntity ingresar(OperacionEntity oOperacionEntity) throws ValidationException {
        double ingreso = oOperacionEntity.getCantidad();
        if (ingreso <= 0) {
            throw new ValidationException("Cantidad incorrecta");
        }
        TipooperacionEntity tipo = oTipooperacionRepository.findById(TipoOperacionHelper.INGRESO).get();
        oOperacionEntity.setTipooperacion(tipo);

        CuentaEntity emisor = oCuentaRepository.findById(oOperacionEntity.getEmisorCuentaEntity().getId()).get();

        oOperacionEntity.setEmisorCuentaEntity(emisor);
        oOperacionEntity.setReceptorCuentaEntity(null);

        return oOperacionRepository.save(oOperacionEntity);
    }

    public OperacionEntity extraer(OperacionEntity oOperacionEntity) throws ValidationException {
        double retirada = oOperacionEntity.getCantidad();
        if (retirada <= 0) {
            throw new ValidationException("Cantidad incorrecta");
        }
        CuentaEntity emisor = oCuentaRepository.findById(oOperacionEntity.getEmisorCuentaEntity().getId()).get();
        oOperacionEntity.setEmisorCuentaEntity(emisor);

        TipooperacionEntity tipo = oTipooperacionRepository.findById(TipoOperacionHelper.RETIRADA).get();
        oOperacionEntity.setTipooperacion(tipo);

        oOperacionEntity.setReceptorCuentaEntity(null);

        validateTransferencia(oOperacionEntity);

        return oOperacionRepository.save(oOperacionEntity);
    }

    public OperacionEntity transferir(OperacionEntity oOperacionEntity) throws ValidationException {
        double retirada = oOperacionEntity.getCantidad();
        if (retirada <= 0) {
            throw new ValidationException("Cantidad incorrecta");
        }
        CuentaEntity emisor = oCuentaRepository.findById(oOperacionEntity.getEmisorCuentaEntity().getId()).get();
        oOperacionEntity.setEmisorCuentaEntity(emisor);

        CuentaEntity receptor = oCuentaRepository.findById(oOperacionEntity.getReceptorCuentaEntity().getId()).get();
        oOperacionEntity.setReceptorCuentaEntity(receptor);

        TipooperacionEntity tipo = oTipooperacionRepository.findById(TipoOperacionHelper.TRANSFERENCIA).get();
        oOperacionEntity.setTipooperacion(tipo);

        validateTransferencia(oOperacionEntity);
        return oOperacionRepository.save(oOperacionEntity);
    }
}
