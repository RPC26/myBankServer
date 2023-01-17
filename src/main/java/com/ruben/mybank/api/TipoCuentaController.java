package com.ruben.mybank.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Sort;


import com.ruben.mybank.entity.TipocuentaEntity;
import com.ruben.mybank.service.TipocuentaService;

@RestController
@RequestMapping("/tipocuenta")
public class TipoCuentaController{

    @Autowired
    TipocuentaService oTipocuentaService;

    @GetMapping("/{id}")
    public ResponseEntity<TipocuentaEntity> get(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<TipocuentaEntity>(oTipocuentaService.get(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TipocuentaEntity>> all() {
        return new ResponseEntity<List<TipocuentaEntity>>(oTipocuentaService.all(), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<Long>(oTipocuentaService.count(), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<Page<TipocuentaEntity>> getPage(
             @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
            @RequestParam(name = "filter", required = false) String strFilter) {
        return new ResponseEntity<Page<TipocuentaEntity>>(oTipocuentaService.getPage(oPageable, strFilter), HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<Long> update(@RequestBody TipocuentaEntity oTipocuentaEntity) {
        return new ResponseEntity<Long>(oTipocuentaService.update(oTipocuentaEntity), HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<Long> generate() {
        return new ResponseEntity<Long>(oTipocuentaService.generate(), HttpStatus.OK);
    }
    
}
