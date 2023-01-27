package com.ruben.mybank.api;

import javax.xml.bind.ValidationException;

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

import com.ruben.mybank.entity.OperacionEntity;
import com.ruben.mybank.service.OperacionService;

@RestController
@RequestMapping("/operacion")
public class OperacionController {

    @Autowired
    OperacionService oOperacionService;

    @GetMapping("/{id}")
    public ResponseEntity<OperacionEntity> get(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<OperacionEntity>(oOperacionService.get(id), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return new ResponseEntity<Long>(oOperacionService.count(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<OperacionEntity>> getPage(
            @PageableDefault(page = 0, size = 10, direction = Sort.Direction.DESC) Pageable oPageable,
            @RequestParam(name = "filter", required = false) String strFilter,
            @RequestParam(name = "tipoOperacion", required = false) Long id_tipoOperacion,
            @RequestParam(name = "cuentaEmisor", required = false) Long id_cuentaemisor ,
            @RequestParam(name = "cuentaReceptor", required = false) Long id_cuentareceptor ) {
        return new ResponseEntity<Page<OperacionEntity>>(
                oOperacionService.getPage(oPageable, strFilter, id_tipoOperacion, id_cuentaemisor,id_cuentareceptor ), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody OperacionEntity oNewOperacionEntity) {
        return new ResponseEntity<Long>(oOperacionService.create(oNewOperacionEntity), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Long> update(@RequestBody OperacionEntity oOperacionEntity) {
        return new ResponseEntity<Long>(oOperacionService.update(oOperacionEntity), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<Long>(oOperacionService.delete(id), HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<OperacionEntity> generate() {
        return new ResponseEntity<OperacionEntity>(oOperacionService.generate(), HttpStatus.OK);
    }

    @PostMapping("/generate/{amount}")
    public ResponseEntity<Long> generateSome(@PathVariable(value = "amount") Integer amount) {
        return new ResponseEntity<>(oOperacionService.generateSome(amount), HttpStatus.OK);
    }

    @PostMapping("/ingresar")
    public ResponseEntity<OperacionEntity> ingresar(@RequestBody OperacionEntity operacionEntity) throws ValidationException {
        return new ResponseEntity<OperacionEntity>(oOperacionService.ingresar(operacionEntity), HttpStatus.OK);
    }

    @PostMapping("/extraer")
    public ResponseEntity<OperacionEntity> extraer(@RequestBody OperacionEntity operacionEntity) throws ValidationException {
        return new ResponseEntity<OperacionEntity>(oOperacionService.extraer(operacionEntity), HttpStatus.OK);
    }

    @PostMapping("/transferir")
    public ResponseEntity<OperacionEntity> transferir(@RequestBody OperacionEntity operacionEntity) throws ValidationException {
        return new ResponseEntity<OperacionEntity>(oOperacionService.transferir(operacionEntity), HttpStatus.OK);
    }



}
