package com.ruben.mybank.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;


import com.ruben.mybank.entity.CuentaEntity;
import com.ruben.mybank.service.CuentaService;

@RestController
@RequestMapping("/cuenta")
public class CuentaController {
    
    
    @Autowired
   CuentaService oCuentaService;

    @GetMapping("/{id}")
    public ResponseEntity<CuentaEntity> get(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<CuentaEntity>(oCuentaService.get(id), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<Long>(oCuentaService.count(), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<Page<CuentaEntity>> getPage(
             @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
            @RequestParam(name = "filter", required = false) String strFilter,
            @RequestParam(name = "tipocuenta", required = false) Long id_tipocuenta,
            @RequestParam(name = "usuario", required = false) Long id_usuario) {
        return new ResponseEntity<Page<CuentaEntity>>(oCuentaService.getPage(oPageable, strFilter, id_tipocuenta, id_usuario), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Long> create(@RequestBody CuentaEntity oNewCuentaEntity) {
        return new ResponseEntity<Long>(oCuentaService.create(oNewCuentaEntity), HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity<Long> update(@RequestBody CuentaEntity oCuentaEntity) {
        return new ResponseEntity<Long>(oCuentaService.update(oCuentaEntity), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<Long>(oCuentaService.delete(id), HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<CuentaEntity> generate() {
        return new ResponseEntity<CuentaEntity>(oCuentaService.generate(), HttpStatus.OK);
    }

    @PostMapping("/generate/{amount}")
    public ResponseEntity<Long> generateSome(@PathVariable(value = "amount") Integer amount) {
        return new ResponseEntity<>(oCuentaService.generateSome(amount), HttpStatus.OK);
    }

}
