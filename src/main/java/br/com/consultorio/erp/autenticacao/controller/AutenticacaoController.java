package br.com.consultorio.erp.autenticacao.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.consultorio.erp.autenticacao.dto.CadastroRequest;
import br.com.consultorio.erp.autenticacao.dto.LoginRequest;
import br.com.consultorio.erp.autenticacao.dto.LoginResponse;
import br.com.consultorio.erp.autenticacao.service.AutenticacaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(
            AutenticacaoService autenticacaoService) {

        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/cadastro-inicial")
    public ResponseEntity<LoginResponse> cadastrarAdministrador(
            @Valid @RequestBody CadastroRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                    autenticacaoService
                        .cadastrarAdministrador(request)
                );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                autenticacaoService.login(request)
        );
    }
}