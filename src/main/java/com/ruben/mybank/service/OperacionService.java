package com.ruben.mybank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ruben.mybank.entity.CuentaEntity;
import com.ruben.mybank.entity.OperacionEntity;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

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

    public void validateTransferencia(OperacionEntity oOperacionEntity) {
        Long idEmisor = oOperacionEntity.getEmisorCuentaEntity().getId();
        List<OperacionEntity> operacionesEmisor = oOperacionRepository.findByEmisorCuentaEntityIdOrReceptorCuentaEntityId(idEmisor, idEmisor);

        double balanceTotal = 0;

        // Balance del emisor
        for (OperacionEntity operacion: operacionesEmisor) {
            System.out.println(operacion);
            Long tipoOperacion = operacion.getTipooperacion().getId();
            double cantidadOperacion = operacion.getCantidad();
            
            if (tipoOperacion == 1L) {
                balanceTotal += cantidadOperacion;
            }

            if (tipoOperacion == 2L || tipoOperacion == 3L) {
                balanceTotal -= cantidadOperacion;
            }

            if (operacion.getReceptorCuentaEntity().getUsuario().getId() == idEmisor) {
                balanceTotal += cantidadOperacion;
            }

            System.out.println(balanceTotal);
        }


        double maxNegativoEmisor = oOperacionEntity.getEmisorCuentaEntity().getTipocuenta().getMaxnegativo();
        double cantidadTransferencia = oOperacionEntity.getCantidad();

        if (cantidadTransferencia > balanceTotal + maxNegativoEmisor) {
            throw new CannotPerformOperationException("");
        }
    }

    public OperacionEntity get(Long id) {
        // oAuthService.OnlyAdmins();
        OperacionEntity o = oOperacionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("id " + id + " not exist"));

            validateTransferencia(o);
        return o;
    }

    public Long count() {
        // oAuthService.OnlyAdmins();
        return oOperacionRepository.count();
    }

    public Page<OperacionEntity> getPage(Pageable oPageable, String strFilter, Long id_tipoOperacion, Long id_cuentaemisor, Long id_cuentareceptor ) {
        oAuthService.OnlyAdmins();
        ValidationHelper.validateRPP(oPageable.getPageSize());
        Page<OperacionEntity> oPage = null;

        // Pasando solo id_receptor
        if (id_tipoOperacion == null && id_cuentaemisor == null && id_cuentareceptor != null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oOperacionRepository.findByReceptorCuentaEntityId(id_cuentareceptor, oPageable);
            }

            oPage = oOperacionRepository.findByReceptorCuentaEntityIdAndCantidadContainingOrFechahoraoperacionContaining(id_cuentareceptor, strFilter, strFilter, oPageable);
        }

        // Pasando id_emisor y id_receptor
        if (id_tipoOperacion == null && id_cuentaemisor != null && id_cuentareceptor != null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oOperacionRepository.findByReceptorCuentaEntityIdAndEmisorCuentaEntityId(id_cuentareceptor, id_cuentaemisor, oPageable);
            }

            oPage = oOperacionRepository.findByReceptorCuentaEntityIdAndEmisorCuentaEntityIdAndCantidadContainingOrFechahoraoperacionContaining(id_cuentareceptor, id_cuentaemisor, strFilter, strFilter, oPageable);
        }

        // Pasando id_emisor, id_receptor y id_tipocuenta
        if (id_tipoOperacion != null && id_cuentaemisor != null && id_cuentareceptor != null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oOperacionRepository.findByReceptorCuentaEntityIdAndEmisorCuentaEntityIdAndTipooperacionId(id_cuentareceptor, id_cuentaemisor, id_tipoOperacion, oPageable);
            }

            oPage = oOperacionRepository.findByReceptorCuentaEntityIdAndEmisorCuentaEntityIdAndTipooperacionIdAndCantidadContainingOrFechahoraoperacionContaining(id_cuentareceptor, id_cuentaemisor, id_tipoOperacion, strFilter, strFilter, oPageable);
        }

        // Pasando solo id_tipooperacion
        if (id_tipoOperacion != null && id_cuentaemisor == null && id_cuentareceptor == null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage= oOperacionRepository.findByTipooperacionId(id_tipoOperacion, oPageable);
        }
        oPage = oOperacionRepository.findByTipooperacionIdAndCantidadContainingOrFechahoraoperacionContaining(id_tipoOperacion,strFilter,strFilter,oPageable);
    
    }

        // Pasando id_tipooperacion y id_cuentaemisor
        if (id_tipoOperacion != null && id_cuentaemisor != null && id_cuentareceptor == null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage=oOperacionRepository.findByTipooperacionIdAndEmisorCuentaEntityId(id_tipoOperacion,id_cuentaemisor,oPageable);
            }
            oPage=oOperacionRepository.findByTipooperacionIdAndEmisorCuentaEntityIdAndCantidadContainingOrFechahoraoperacionContaining(id_tipoOperacion,id_cuentaemisor,strFilter,strFilter,oPageable);
        }

        // Pasando solo id_cuentaemisor
        if (id_tipoOperacion == null && id_cuentaemisor != null && id_cuentareceptor == null) {

            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage=oOperacionRepository.findByEmisorCuentaEntityId(id_cuentaemisor,oPageable);
            }
            oPage=oOperacionRepository.findByEmisorCuentaEntityIdAndCantidadContainingOrFechahoraoperacionContaining(id_cuentaemisor,strFilter,strFilter,oPageable);

        }

        // Pasando id_cuentaemisor y id_cuentareceptor
        if (id_tipoOperacion == null && id_cuentaemisor != null && id_cuentareceptor != null) {

            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage=oOperacionRepository.findByEmisorCuentaEntityIdAndReceptorCuentaEntityId(id_cuentaemisor,id_cuentareceptor,oPageable);
            }
            oPage=oOperacionRepository.findByEmisorCuentaEntityIdAndReceptorCuentaEntityIdAndCantidadContainingOrFechahoraoperacionContaining(id_cuentaemisor,id_cuentareceptor,strFilter,strFilter,oPageable);

        }

        // Pasando nada
        if (id_tipoOperacion == null && id_cuentaemisor == null && id_cuentareceptor == null) {

            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage=oOperacionRepository.findAll( oPageable);
            }
            oPage=oOperacionRepository.findByCantidadContainingOrFechahoraoperacionContaining(strFilter,strFilter,oPageable);

        }

        return oPage;
    }

    public Long create(OperacionEntity oNewOperacionEntity) {
        // oAuthService.OnlyAdmins();
        validateTransferencia(oNewOperacionEntity);
        return oOperacionRepository.save(oNewOperacionEntity).getId();
    }

    @Transactional
    public Long update(OperacionEntity oOperacionEntity) {
        validate(oOperacionEntity.getId());
        validateTransferencia(oOperacionEntity);
        return update4Admins(oOperacionEntity).getId();
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

    // public OperacionEntity ingresar() {

    //     return null;
    // }

    // public OperacionEntity extraer() {

    //     return null;
    // }

    // public OperacionEntity transferir() {
        
    //     return null;
    // }
}

