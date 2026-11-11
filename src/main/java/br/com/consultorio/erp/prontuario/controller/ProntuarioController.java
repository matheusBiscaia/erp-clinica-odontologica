package br.com.consultorio.erp.prontuario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.consultorio.erp.prontuario.dto.ProntuarioRequest;
import br.com.consultorio.erp.prontuario.dto.ProntuarioResponse;
import br.com.consultorio.erp.prontuario.service.ProntuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/prontuarios")
public class ProntuarioController {

    private final ProntuarioService prontuarioService;

    public ProntuarioController(ProntuarioService prontuarioService) {
        this.prontuarioService = prontuarioService;
    }

    @PostMapping
    public ResponseEntity<ProntuarioResponse> cadastrar(
            @Valid @RequestBody ProntuarioRequest request) {

        ProntuarioResponse prontuario =
                prontuarioService.cadastrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(prontuario);
    }

    @GetMapping
    public ResponseEntity<List<ProntuarioResponse>> listarTodos() {
        return ResponseEntity.ok(prontuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProntuarioResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(prontuarioService.buscarPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ProntuarioResponse>> listarPorPaciente(
            @PathVariable Long pacienteId) {

        return ResponseEntity.ok(
                prontuarioService.listarPorPaciente(pacienteId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProntuarioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProntuarioRequest request) {

        return ResponseEntity.ok(
                prontuarioService.atualizar(id, request)
        );
    }
}