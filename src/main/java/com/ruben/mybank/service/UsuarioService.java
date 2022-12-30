package com.ruben.mybank.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ruben.mybank.entity.UsuarioEntity;
import com.ruben.mybank.exception.CannotPerformOperationException;
import com.ruben.mybank.exception.ResourceNotFoundException;
import com.ruben.mybank.exception.ResourceNotModifiedException;
import com.ruben.mybank.exception.ValidationException;
import com.ruben.mybank.helper.RandomHelper;
import com.ruben.mybank.helper.TipoUsuarioHelper;
import com.ruben.mybank.helper.ValidationHelper;
import com.ruben.mybank.repository.TipousuarioRepository;
import com.ruben.mybank.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    TipousuarioService oTipousuarioService;

    @Autowired
    TipousuarioRepository oTipousuarioRepository;

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    AuthService oAuthService;

    private final String DNI_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";
    private final String MYBANK_PASSWORD = "prueba"; 
    private final String[] NAMES = { "Jose", "Mark", "Elen", "Toni", "Hector", "Jose", "Laura", "Vika", "Sergio",
            "Javi", "Marcos", "Pere", "Daniel", "Jose", "Javi", "Sergio", "Aaron", "Rafa", "Lionel", "Borja" };

    private final String[] SURNAMES = { "Penya", "Tatay", "Coronado", "Cabanes", "Mikayelyan", "Gil", "Martinez",
            "Bargues", "Raga", "Santos", "Sierra", "Arias", "Santos", "Kuvshinnikova", "Cosin", "Frejo", "Marti",
            "Valcarcel", "Sesa", "Lence", "Villanueva", "Peyro", "Navarro", "Navarro", "Primo", "Gil", "Mocholi",
            "Ortega", "Dung", "Vi", "Sanchis", "Merida", "Aznar", "Aparici", "Tarazón", "Alcocer", "Salom",
            "Santamaría" };

    public void validate(Long id) {
        if (!oUsuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }

    public void validate(UsuarioEntity oUsuarioEntity) {
        ValidationHelper.validateDNI(oUsuarioEntity.getDni(), "campo DNI de Usuario");
        ValidationHelper.validateStringLength(oUsuarioEntity.getNombre(), 2, 50,
                "campo nombre de Usuario (el campo debe tener longitud de 2 a 50 caracteres)");
        ValidationHelper.validateStringLength(oUsuarioEntity.getApellido1(), 2, 50,
                "campo primer apellido de Usuario (el campo debe tener longitud de 2 a 50 caracteres)");
        ValidationHelper.validateStringLength(oUsuarioEntity.getApellido2(), 2, 50,
                "campo segundo apellido de Usuario (el campo debe tener longitud de 2 a 50 caracteres)");
        ValidationHelper.validateEmail(oUsuarioEntity.getEmail(), " campo email de Usuario");
        ValidationHelper.validateLogin(oUsuarioEntity.getLogin(), " campo login de Usuario");
        // if (oUsuarioRepository.existsByLogin(oUsuarioEntity.getLogin())) {
        //     throw new ValidationException("el campo Login está repetido");
        // }

        // ESCRIBIR LA SQL
        oTipousuarioService.validate(oUsuarioEntity.getTipousuario().getId());
    }

    public UsuarioEntity get(Long id) {
        oAuthService.OnlyAdminsOrOwnUsersData(id);
        try {
            return oUsuarioRepository.findById(id).get();
        } catch (Exception ex) {
            throw new ResourceNotFoundException("id " + id + " not exist");
        }
    }

    public Long count() {
        oAuthService.OnlyAdmins();
        return oUsuarioRepository.count();
    }

    public Page<UsuarioEntity> getPage(Pageable oPageable, String strFilter, Long lTipoUsuario) {
        oAuthService.OnlyAdmins();
        ValidationHelper.validateRPP(oPageable.getPageSize());
        Page<UsuarioEntity> oPage = null;
        if (lTipoUsuario == null) {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oUsuarioRepository.findAll(oPageable);
            } else {
                oPage = oUsuarioRepository
                        .findByDniIgnoreCaseContainingOrNombreIgnoreCaseContainingOrApellido1IgnoreCaseContainingOrApellido2IgnoreCaseContaining(
                                strFilter, strFilter, strFilter, strFilter, oPageable);
            }
        } else {
            if (strFilter == null || strFilter.isEmpty() || strFilter.trim().isEmpty()) {
                oPage = oUsuarioRepository.findByTipousuarioId(lTipoUsuario, oPageable);
            } else {
                oPage = oUsuarioRepository
                        .findByTipousuarioIdAndDniIgnoreCaseContainingOrNombreIgnoreCaseContainingOrApellido1IgnoreCaseContainingOrApellido2IgnoreCaseContaining(
                                lTipoUsuario, strFilter, strFilter, strFilter, strFilter, oPageable);
            }
        }
        return oPage;
    }

    public Long create(UsuarioEntity oNewUsuarioEntity) {
        oAuthService.OnlyAdmins();
        validate(oNewUsuarioEntity);
        oNewUsuarioEntity.setId(0L);
        oNewUsuarioEntity.setPassword(MYBANK_PASSWORD);
        return oUsuarioRepository.save(oNewUsuarioEntity).getId();
    }

    @Transactional
    public Long update(UsuarioEntity oUsuarioEntity) {
        validate(oUsuarioEntity.getId());
        oAuthService.OnlyAdminsOrOwnUsersData(oUsuarioEntity.getId());
        validate(oUsuarioEntity);
        oTipousuarioService.validate(oUsuarioEntity.getTipousuario().getId());
        if (oAuthService.isAdmin()) {
            return update4Admins(oUsuarioEntity).getId();
        } else {
            return update4Users(oUsuarioEntity).getId();
        }
    }

    @Transactional
    private UsuarioEntity update4Admins(UsuarioEntity oUpdatedUsuarioEntity) {
        UsuarioEntity oUsuarioEntity = oUsuarioRepository.findById(oUpdatedUsuarioEntity.getId()).get();
        // keeping login password token & validado
        oUsuarioEntity.setDni(oUpdatedUsuarioEntity.getDni());
        oUsuarioEntity.setNombre(oUpdatedUsuarioEntity.getNombre());
        oUsuarioEntity.setApellido1(oUpdatedUsuarioEntity.getApellido1());
        oUsuarioEntity.setApellido2(oUpdatedUsuarioEntity.getApellido2());
        oUsuarioEntity.setLogin(oUpdatedUsuarioEntity.getLogin());
        oUsuarioEntity.setPassword(oUpdatedUsuarioEntity.getPassword());
        oUsuarioEntity.setEmail(oUpdatedUsuarioEntity.getEmail());
        oUsuarioEntity.setTipousuario(oTipousuarioService.get(oUpdatedUsuarioEntity.getTipousuario().getId()));
        return oUsuarioRepository.save(oUsuarioEntity);
    }

    @Transactional
    private UsuarioEntity update4Users(UsuarioEntity oUpdatedUsuarioEntity) {
        UsuarioEntity oUsuarioEntity = oUsuarioRepository.findById(oUpdatedUsuarioEntity.getId()).get();
        // keeping login password token & validado descuento activo tipousuario
        oUsuarioEntity.setDni(oUpdatedUsuarioEntity.getDni());
        oUsuarioEntity.setNombre(oUpdatedUsuarioEntity.getNombre());
        oUsuarioEntity.setApellido1(oUpdatedUsuarioEntity.getApellido1());
        oUsuarioEntity.setApellido2(oUpdatedUsuarioEntity.getApellido2());
        oUsuarioEntity.setPassword(oUpdatedUsuarioEntity.getPassword());
        oUsuarioEntity.setEmail(oUpdatedUsuarioEntity.getEmail());
        oUsuarioEntity.setTipousuario(oTipousuarioService.get(2L));
        return oUsuarioRepository.save(oUsuarioEntity);
    }

    public Long delete(Long id) {
        oAuthService.OnlyAdmins();
        if (oUsuarioRepository.existsById(id)) {
            oUsuarioRepository.deleteById(id);
            if (oUsuarioRepository.existsById(id)) {
                throw new ResourceNotModifiedException("can't remove register " + id);
            } else {
                return id;
            }
        } else {
            throw new ResourceNotModifiedException("id " + id + " not exist");
        }
    }

    public UsuarioEntity generate() {
        oAuthService.OnlyAdmins();
        return generateRandomUser();
    }

    public UsuarioEntity getOneRandom() {
        if (count() > 0) {
            List<UsuarioEntity> usuarioList = oUsuarioRepository.findAll();
            int iPosicion = RandomHelper.getRandomInt(0, (int) oUsuarioRepository.count() - 1);
            return oUsuarioRepository.getById(usuarioList.get(iPosicion).getId());
        } else {
            throw new CannotPerformOperationException("ho hay usuarios en la base de datos");
        }
    }

    private UsuarioEntity generateRandomUser() {
        UsuarioEntity oUserEntity = new UsuarioEntity();
        oUserEntity.setDni(generateDNI());
        oUserEntity.setNombre(generateName());
        oUserEntity.setApellido1(generateSurname());
        oUserEntity.setApellido2(generateSurname());
        oUserEntity.setLogin(oUserEntity.getNombre() + "_" + oUserEntity.getApellido1());
        oUserEntity.setPassword(MYBANK_PASSWORD); // wildcart
        oUserEntity.setEmail(generateEmail(oUserEntity.getNombre(), oUserEntity.getApellido1()));
        if (RandomHelper.getRandomInt(0, 10) > 1) {
            oUserEntity.setTipousuario(oTipousuarioRepository.getById(TipoUsuarioHelper.USER));
        } else {
            oUserEntity.setTipousuario(oTipousuarioRepository.getById(TipoUsuarioHelper.ADMIN));
        }
        return oUserEntity;
    }

    public Long generateSome(Integer amount) {
        oAuthService.OnlyAdmins();
        List<UsuarioEntity> userList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            UsuarioEntity oUsuarioEntity = generateRandomUser();
            userList.add(oUsuarioEntity);
        }
        oUsuarioRepository.saveAll(userList);
        return oUsuarioRepository.count();
    }

    private String generateDNI() {
        String dni = "";
        int dniNumber = RandomHelper.getRandomInt(11111111, 99999999 + 1);
        dni += dniNumber + "" + DNI_LETTERS.charAt(dniNumber % 23);
        return dni;
    }

    private String generateName() {
        return NAMES[RandomHelper.getRandomInt(0, NAMES.length - 1)].toLowerCase();
    }

    private String generateSurname() {
        return SURNAMES[RandomHelper.getRandomInt(0, SURNAMES.length - 1)].toLowerCase();
    }

    private String generateEmail(String name, String surname) {
        List<String> list = new ArrayList<>();
        list.add(name);
        list.add(surname);
        return getFromList(list) + "_" + getFromList(list) + "@daw.tk";
    }

    private String getFromList(List<String> list) {
        int randomNumber = RandomHelper.getRandomInt(0, list.size() - 1);
        String value = list.get(randomNumber);
        list.remove(randomNumber);
        return value;
    }

}
