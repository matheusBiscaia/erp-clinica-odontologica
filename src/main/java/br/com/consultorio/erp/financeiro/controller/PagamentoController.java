package br.com.consultorio.erp.financeiro.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.consultorio.erp.financeiro.dto.PagamentoBaixaRequest;
import br.com.consultorio.erp.financeiro.dto.PagamentoRequest;
import br.com.consultorio.erp.financeiro.dto.PagamentoResponse;
import br.com.consultorio.erp.financeiro.entity.StatusPagamento;
import br.com.consultorio.erp.financeiro.service.PagamentoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    public ResponseEntity<PagamentoResponse> cadastrar(
            @Valid @RequestBody PagamentoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pagamentoService.cadastrar(request));
    }

    @GetMapping
    public ResponseEntity<List<PagamentoResponse>> listarTodos() {
        return ResponseEntity.ok(pagamentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                pagamentoService.buscarPorId(id)
        );
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<PagamentoResponse>> listarPorPaciente(
            @PathVariable Long pacienteId) {

        return ResponseEntity.ok(
                pagamentoService.listarPorPaciente(pacienteId)
        );
    }

    @GetMapping("/status")
    public ResponseEntity<List<PagamentoResponse>> listarPorStatus(
            @RequestParam StatusPagamento status) {

        return ResponseEntity.ok(
                pagamentoService.listarPorStatus(status)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PagamentoRequest request) {

        return ResponseEntity.ok(
                pagamentoService.atualizar(id, request)
        );
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<PagamentoResponse> pagar(
            @PathVariable Long id,
            @Valid @RequestBody PagamentoBaixaRequest request) {

        return ResponseEntity.ok(
                pagamentoService.pagar(id, request)
        );
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PagamentoResponse> cancelar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                pagamentoService.cancelar(id)
        );
    }
}