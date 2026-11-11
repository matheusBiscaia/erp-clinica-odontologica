package br.com.consultorio.erp.paciente.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.com.consultorio.erp.paciente.dto.PacienteRequest;
import br.com.consultorio.erp.paciente.dto.PacienteResponse;
import br.com.consultorio.erp.paciente.entity.Paciente;
import br.com.consultorio.erp.paciente.repository.PacienteRepository;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public List<PacienteResponse> listarTodos() {
        return pacienteRepository.findByAtivoTrue()
            .stream()
            .map(this::converterParaResponse)
            .toList();
}
    @Transactional
    public PacienteResponse cadastrar(PacienteRequest request) {
        if (pacienteRepository.existsByCpf(request.cpf())) {
            throw new IllegalArgumentException("Já existe um paciente com esse CPF");
        }

        Paciente paciente = new Paciente();
        paciente.setNomeCompleto(request.nomeCompleto());
        paciente.setCpf(request.cpf());
        paciente.setDataNascimento(request.dataNascimento());
        paciente.setTelefone(request.telefone());
        paciente.setEmail(request.email());
        paciente.setAtivo(true);

        Paciente pacienteSalvo = pacienteRepository.save(paciente);

        return converterParaResponse(pacienteSalvo);
    }

    private PacienteResponse converterParaResponse(Paciente paciente) {
        return new PacienteResponse(
            paciente.getId(),
            paciente.getNomeCompleto(),
            paciente.getCpf(),
            paciente.getDataNascimento(),
            paciente.getTelefone(),
            paciente.getEmail(),
            paciente.getAtivo()
        );
    }
    public PacienteResponse buscarPorId(Long id) {
    Paciente paciente = pacienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Paciente não encontrado"
            ));

    return converterParaResponse(paciente);
}
@Transactional
public PacienteResponse atualizar(Long id, PacienteRequest request) {
    Paciente paciente = pacienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Paciente não encontrado"
            ));

    if (pacienteRepository.existsByCpfAndIdNot(request.cpf(), id)) {
        throw new IllegalArgumentException(
                "Já existe outro paciente com esse CPF"
        );
    }

    paciente.setNomeCompleto(request.nomeCompleto());
    paciente.setCpf(request.cpf());
    paciente.setDataNascimento(request.dataNascimento());
    paciente.setTelefone(request.telefone());
    paciente.setEmail(request.email());

    Paciente pacienteAtualizado = pacienteRepository.save(paciente);

    return converterParaResponse(pacienteAtualizado);
}
@Transactional
public void desativar(Long id) {
    Paciente paciente = pacienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Paciente não encontrado"
            ));

    paciente.setAtivo(false);
    pacienteRepository.save(paciente);
}
@Transactional
public PacienteResponse reativar(Long id) {
    Paciente paciente = pacienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Paciente não encontrado"
            ));

    paciente.setAtivo(true);

    Paciente pacienteReativado = pacienteRepository.save(paciente);

    return converterParaResponse(pacienteReativado);
}
}