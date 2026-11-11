package br.com.consultorio.erp.agendamento.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.consultorio.erp.agendamento.dto.AgendamentoRequest;
import br.com.consultorio.erp.agendamento.dto.AgendamentoResponse;
import br.com.consultorio.erp.agendamento.service.AgendamentoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(
            AgendamentoService agendamentoService) {

        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponse> cadastrar(
            @Valid @RequestBody AgendamentoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(agendamentoService.cadastrar(request));
    }

    @GetMapping
    public ResponseEntity<List<AgendamentoResponse>> listarTodos() {
        return ResponseEntity.ok(
                agendamentoService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                agendamentoService.buscarPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AgendamentoRequest request) {

        return ResponseEntity.ok(
                agendamentoService.atualizar(id, request)
        );
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<AgendamentoResponse> confirmar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                agendamentoService.confirmar(id)
        );
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<AgendamentoResponse> concluir(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                agendamentoService.concluir(id)
        );
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AgendamentoResponse> cancelar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                agendamentoService.cancelar(id)
        );
    }
}