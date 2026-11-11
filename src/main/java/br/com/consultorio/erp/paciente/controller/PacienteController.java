package br.com.consultorio.erp.paciente.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import br.com.consultorio.erp.paciente.dto.PacienteRequest;
import br.com.consultorio.erp.paciente.dto.PacienteResponse;
import br.com.consultorio.erp.paciente.service.PacienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<PacienteResponse> cadastrar(
            @Valid @RequestBody PacienteRequest request) {

        PacienteResponse paciente = pacienteService.cadastrar(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(paciente);
    }
    @GetMapping
public ResponseEntity<List<PacienteResponse>> listarTodos() {
    List<PacienteResponse> pacientes = pacienteService.listarTodos();

    return ResponseEntity.ok(pacientes);
}
@GetMapping("/{id}")
public ResponseEntity<PacienteResponse> buscarPorId(
        @PathVariable Long id) {

    PacienteResponse paciente = pacienteService.buscarPorId(id);

    return ResponseEntity.ok(paciente);
}
@PutMapping("/{id}")
public ResponseEntity<PacienteResponse> atualizar(
        @PathVariable Long id,
        @Valid @RequestBody PacienteRequest request) {

    PacienteResponse paciente = pacienteService.atualizar(id, request);

    return ResponseEntity.ok(paciente);
}
@DeleteMapping("/{id}")
public ResponseEntity<Void> desativar(@PathVariable Long id) {
    pacienteService.desativar(id);

    return ResponseEntity.noContent().build();
}
@PatchMapping("/{id}/reativar")
public ResponseEntity<PacienteResponse> reativar(
        @PathVariable Long id) {

    PacienteResponse paciente = pacienteService.reativar(id);

    return ResponseEntity.ok(paciente);
}
}