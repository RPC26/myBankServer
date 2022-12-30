package com.ruben.mybank.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.ruben.mybank.entity.TipousuarioEntity;
import com.ruben.mybank.service.TipousuarioService;

@RestController
@RequestMapping("/tipousuario")
public class TipoUsuarioController {

    @Autowired
    TipousuarioService oTipousuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<TipousuarioEntity> get(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<TipousuarioEntity>(oTipousuarioService.get(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TipousuarioEntity>> all() {
        return new ResponseEntity<List<TipousuarioEntity>>(oTipousuarioService.all(), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<Long>(oTipousuarioService.count(), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<Page<TipousuarioEntity>> getPage(
            @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
            @RequestParam(name = "filter", required = false) String strFilter) {
        return new ResponseEntity<Page<TipousuarioEntity>>(oTipousuarioService.getPage(oPageable, strFilter),
                HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<Long> update(@RequestBody TipousuarioEntity oTipoUsuarioEntity) {
        return new ResponseEntity<Long>(oTipousuarioService.update(oTipoUsuarioEntity), HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<Long> generate() {
        return new ResponseEntity<Long>(oTipousuarioService.generate(), HttpStatus.OK);
    }

}
