package br.com.consultorio.erp.dentista.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.consultorio.erp.dentista.dto.DentistaRequest;
import br.com.consultorio.erp.dentista.dto.DentistaResponse;
import br.com.consultorio.erp.dentista.service.DentistaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dentistas")
public class DentistaController {

    private final DentistaService dentistaService;

    public DentistaController(DentistaService dentistaService) {
        this.dentistaService = dentistaService;
    }

    @PostMapping
    public ResponseEntity<DentistaResponse> cadastrar(
            @Valid @RequestBody DentistaRequest request) {

        DentistaResponse dentista =
                dentistaService.cadastrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dentista);
    }

    @GetMapping
    public ResponseEntity<List<DentistaResponse>> listarTodos() {
        return ResponseEntity.ok(
                dentistaService.listarTodos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DentistaResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                dentistaService.buscarPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<DentistaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DentistaRequest request) {

        return ResponseEntity.ok(
                dentistaService.atualizar(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(
            @PathVariable Long id) {

        dentistaService.desativar(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<DentistaResponse> reativar(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                dentistaService.reativar(id)
        );
    }
}