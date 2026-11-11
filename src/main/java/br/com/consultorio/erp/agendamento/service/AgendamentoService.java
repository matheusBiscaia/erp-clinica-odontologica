package br.com.consultorio.erp.agendamento.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.consultorio.erp.agendamento.dto.AgendamentoRequest;
import br.com.consultorio.erp.agendamento.dto.AgendamentoResponse;
import br.com.consultorio.erp.agendamento.entity.Agendamento;
import br.com.consultorio.erp.agendamento.entity.StatusAgendamento;
import br.com.consultorio.erp.agendamento.repository.AgendamentoRepository;
import br.com.consultorio.erp.dentista.entity.Dentista;
import br.com.consultorio.erp.dentista.repository.DentistaRepository;
import br.com.consultorio.erp.paciente.entity.Paciente;
import br.com.consultorio.erp.paciente.repository.PacienteRepository;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final PacienteRepository pacienteRepository;
    private final DentistaRepository dentistaRepository;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            PacienteRepository pacienteRepository,
            DentistaRepository dentistaRepository) {

        this.agendamentoRepository = agendamentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.dentistaRepository = dentistaRepository;
    }

    @Transactional
    public AgendamentoResponse cadastrar(
            AgendamentoRequest request) {

        Paciente paciente = buscarPaciente(request.pacienteId());
        Dentista dentista = buscarDentista(request.dentistaId());

        validarAtivos(paciente, dentista);

        boolean horarioOcupado =
                agendamentoRepository
                        .existsByDentistaIdAndDataHoraAndStatusNot(
                                dentista.getId(),
                                request.dataHora(),
                                StatusAgendamento.CANCELADA
                        );

        if (horarioOcupado) {
            throw new IllegalArgumentException(
                    "O dentista já possui um agendamento nesse horário"
            );
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(paciente);
        agendamento.setDentista(dentista);
        agendamento.setDataHora(request.dataHora());
        agendamento.setProcedimento(request.procedimento());
        agendamento.setObservacoes(request.observacoes());
        agendamento.setStatus(StatusAgendamento.AGENDADA);

        Agendamento agendamentoSalvo =
                agendamentoRepository.save(agendamento);

        return converterParaResponse(agendamentoSalvo);
    }

    public List<AgendamentoResponse> listarTodos() {
        return agendamentoRepository
                .findAllByOrderByDataHoraAsc()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public AgendamentoResponse buscarPorId(Long id) {
        return converterParaResponse(buscarEntidadePorId(id));
    }

    @Transactional
    public AgendamentoResponse atualizar(
            Long id,
            AgendamentoRequest request) {

        Agendamento agendamento = buscarEntidadePorId(id);
        Paciente paciente = buscarPaciente(request.pacienteId());
        Dentista dentista = buscarDentista(request.dentistaId());

        validarAtivos(paciente, dentista);

        boolean horarioOcupado =
                agendamentoRepository
                        .existsByDentistaIdAndDataHoraAndStatusNotAndIdNot(
                                dentista.getId(),
                                request.dataHora(),
                                StatusAgendamento.CANCELADA,
                                id
                        );

        if (horarioOcupado) {
            throw new IllegalArgumentException(
                    "O dentista já possui um agendamento nesse horário"
            );
        }

        agendamento.setPaciente(paciente);
        agendamento.setDentista(dentista);
        agendamento.setDataHora(request.dataHora());
        agendamento.setProcedimento(request.procedimento());
        agendamento.setObservacoes(request.observacoes());

        Agendamento agendamentoAtualizado =
                agendamentoRepository.save(agendamento);

        return converterParaResponse(agendamentoAtualizado);
    }

    @Transactional
    public AgendamentoResponse cancelar(Long id) {
        Agendamento agendamento = buscarEntidadePorId(id);

        agendamento.setStatus(StatusAgendamento.CANCELADA);

        Agendamento agendamentoCancelado =
                agendamentoRepository.save(agendamento);

        return converterParaResponse(agendamentoCancelado);
    }

    @Transactional
    public AgendamentoResponse confirmar(Long id) {
        Agendamento agendamento = buscarEntidadePorId(id);

        if (agendamento.getStatus() == StatusAgendamento.CANCELADA) {
            throw new IllegalArgumentException(
                    "Um agendamento cancelado não pode ser confirmado"
            );
        }

        agendamento.setStatus(StatusAgendamento.CONFIRMADA);

        return converterParaResponse(
                agendamentoRepository.save(agendamento)
        );
    }

    @Transactional
    public AgendamentoResponse concluir(Long id) {
        Agendamento agendamento = buscarEntidadePorId(id);

        if (agendamento.getStatus() == StatusAgendamento.CANCELADA) {
            throw new IllegalArgumentException(
                    "Um agendamento cancelado não pode ser concluído"
            );
        }

        agendamento.setStatus(StatusAgendamento.CONCLUIDA);

        return converterParaResponse(
                agendamentoRepository.save(agendamento)
        );
    }

    private Agendamento buscarEntidadePorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Agendamento não encontrado"
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

    private void validarAtivos(
            Paciente paciente,
            Dentista dentista) {

        if (!paciente.getAtivo()) {
            throw new IllegalArgumentException(
                    "Não é possível agendar para um paciente inativo"
            );
        }

        if (!dentista.isAtivo()) {
            throw new IllegalArgumentException(
                    "Não é possível agendar com um dentista inativo"
            );
        }
    }

    private AgendamentoResponse converterParaResponse(
            Agendamento agendamento) {

        return new AgendamentoResponse(
                agendamento.getId(),
                agendamento.getPaciente().getId(),
                agendamento.getPaciente().getNomeCompleto(),
                agendamento.getDentista().getId(),
                agendamento.getDentista().getNomeCompleto(),
                agendamento.getDataHora(),
                agendamento.getProcedimento(),
                agendamento.getObservacoes(),
                agendamento.getStatus()
        );
    }
}