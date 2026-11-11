package br.com.consultorio.erp.prontuario.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.consultorio.erp.dentista.entity.Dentista;
import br.com.consultorio.erp.dentista.repository.DentistaRepository;
import br.com.consultorio.erp.paciente.entity.Paciente;
import br.com.consultorio.erp.paciente.repository.PacienteRepository;
import br.com.consultorio.erp.prontuario.dto.ProntuarioRequest;
import br.com.consultorio.erp.prontuario.dto.ProntuarioResponse;
import br.com.consultorio.erp.prontuario.entity.Prontuario;
import br.com.consultorio.erp.prontuario.repository.ProntuarioRepository;

@Service
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final DentistaRepository dentistaRepository;

    public ProntuarioService(
            ProntuarioRepository prontuarioRepository,
            PacienteRepository pacienteRepository,
            DentistaRepository dentistaRepository) {

        this.prontuarioRepository = prontuarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.dentistaRepository = dentistaRepository;
    }

    @Transactional
    public ProntuarioResponse cadastrar(ProntuarioRequest request) {
        Paciente paciente = buscarPaciente(request.pacienteId());
        Dentista dentista = buscarDentista(request.dentistaId());

        validarAtivos(paciente, dentista);

        Prontuario prontuario = new Prontuario();
        preencherDados(prontuario, request, paciente, dentista);

        Prontuario prontuarioSalvo = prontuarioRepository.save(prontuario);

        return converterParaResponse(prontuarioSalvo);
    }

    @Transactional(readOnly = true)
    public List<ProntuarioResponse> listarTodos() {
        return prontuarioRepository
                .findAllByOrderByDataAtendimentoDesc()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProntuarioResponse buscarPorId(Long id) {
        return converterParaResponse(buscarProntuario(id));
    }

    @Transactional(readOnly = true)
    public List<ProntuarioResponse> listarPorPaciente(Long pacienteId) {
        buscarPaciente(pacienteId);

        return prontuarioRepository
                .findByPacienteIdOrderByDataAtendimentoDesc(pacienteId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional
    public ProntuarioResponse atualizar(Long id, ProntuarioRequest request) {
        Prontuario prontuario = buscarProntuario(id);
        Paciente paciente = buscarPaciente(request.pacienteId());
        Dentista dentista = buscarDentista(request.dentistaId());

        validarAtivos(paciente, dentista);
        preencherDados(prontuario, request, paciente, dentista);

        Prontuario prontuarioAtualizado =
                prontuarioRepository.save(prontuario);

        return converterParaResponse(prontuarioAtualizado);
    }

    private Prontuario buscarProntuario(Long id) {
        return prontuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Prontuário não encontrado"
                ));
    }

    private Paciente buscarPaciente(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Paciente não encontrado"
                ));
    }

    private Dentista buscarDentista(Long id) {
        return dentistaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dentista não encontrado"
                ));
    }

    private void validarAtivos(Paciente paciente, Dentista dentista) {
        if (!paciente.getAtivo()) {
            throw new IllegalArgumentException(
                    "Não é possível criar um prontuário para um paciente inativo"
            );
        }

        if (!dentista.isAtivo()) {
            throw new IllegalArgumentException(
                    "Não é possível criar um prontuário com um dentista inativo"
            );
        }
    }

    private void preencherDados(
            Prontuario prontuario,
            ProntuarioRequest request,
            Paciente paciente,
            Dentista dentista) {

        prontuario.setPaciente(paciente);
        prontuario.setDentista(dentista);
        prontuario.setDataAtendimento(request.dataAtendimento());
        prontuario.setDiagnostico(request.diagnostico());
        prontuario.setTratamento(request.tratamento());
        prontuario.setObservacoes(request.observacoes());
    }

    private ProntuarioResponse converterParaResponse(Prontuario prontuario) {
        return new ProntuarioResponse(
                prontuario.getId(),
                prontuario.getPaciente().getId(),
                prontuario.getPaciente().getNomeCompleto(),
                prontuario.getDentista().getId(),
                prontuario.getDentista().getNomeCompleto(),
                prontuario.getDataAtendimento(),
                prontuario.getDiagnostico(),
                prontuario.getTratamento(),
                prontuario.getObservacoes()
        );
    }
}