package com.ruben.mybank.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.ruben.mybank.bean.UsuarioBean;
import com.ruben.mybank.entity.UsuarioEntity;
import com.ruben.mybank.exception.UnauthorizedException;
import com.ruben.mybank.helper.JwtHelper;
import com.ruben.mybank.helper.TipoUsuarioHelper;
import com.ruben.mybank.repository.UsuarioRepository;

@Service
public class AuthService {
   
    @Autowired
    private HttpServletRequest oRequest;

    @Autowired
    UsuarioRepository oUsuarioRepository;

    public String login(UsuarioBean oUsuarioBean) {
        if (oUsuarioBean.getPassword() != null) {
            UsuarioEntity oUsuarioEntity = oUsuarioRepository.findByLoginAndPassword(oUsuarioBean.getLogin(), oUsuarioBean.getPassword());
            if (oUsuarioEntity != null) {
                return JwtHelper.generateJWT(String.valueOf(oUsuarioEntity.getId()),oUsuarioEntity.getLogin(), oUsuarioEntity.getTipousuario().getNombre());
            } else {
                throw new UnauthorizedException("login or password incorrect");
            }
        } else {
            throw new UnauthorizedException("wrong password");
        }
    }

    public UsuarioEntity check() {
        String strUsuarioName = (String) oRequest.getAttribute("usuario");
        if (strUsuarioName != null) {
            UsuarioEntity oUsuarioEntity = oUsuarioRepository.findByLogin(strUsuarioName);
            return oUsuarioEntity;
        } else {
            throw new UnauthorizedException("No active session");
        }
    }

    public boolean isAdmin() {
        UsuarioEntity oUsuarioSessionEntity = oUsuarioRepository.findByLogin((String)  oRequest.getAttribute("usuario"));
        if (oUsuarioSessionEntity != null) {
            if (oUsuarioSessionEntity.getTipousuario().getId().equals(TipoUsuarioHelper.ADMIN)) {
                return true;
            }
        }
        return false;
    }
    
    public void OnlyAdmins() {
        String nombre = (String)  oRequest.getAttribute("usuario");
        UsuarioEntity oUsuarioSessionEntity = oUsuarioRepository.findByLogin(nombre);
        if (oUsuarioSessionEntity == null) {
            throw new UnauthorizedException("this request is only allowed to admin role");
        } else {
            if (!oUsuarioSessionEntity.getTipousuario().getId().equals(TipoUsuarioHelper.ADMIN)) {
                throw new UnauthorizedException("this request is only allowed to admin role");
            }
        }
    }

}

