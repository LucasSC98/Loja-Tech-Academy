package com.store.ljstore.controller;

import com.store.ljstore.model.AuditoriaPrecoProduto;
import com.store.ljstore.repository.AuditoriaPrecoProdutoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {
    @Autowired
    private AuditoriaPrecoProdutoRepositorio auditoriaPrecoProdutoRepositorio;

    // um get simples para retornar todos os registros de auditoria de preço de produto
    @GetMapping("/auditoria-preco-produto")
    public ResponseEntity<List<AuditoriaPrecoProduto>> getAllAuditoriaPrecoProduto() {
        List<AuditoriaPrecoProduto> auditoriaPrecoProduto = auditoriaPrecoProdutoRepositorio.findAll();
        return ResponseEntity.ok(auditoriaPrecoProduto);
    }



}
