package br.com.consultorio.erp.financeiro.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.consultorio.erp.agendamento.entity.Agendamento;
import br.com.consultorio.erp.agendamento.repository.AgendamentoRepository;
import br.com.consultorio.erp.financeiro.dto.PagamentoBaixaRequest;
import br.com.consultorio.erp.financeiro.dto.PagamentoRequest;
import br.com.consultorio.erp.financeiro.dto.PagamentoResponse;
import br.com.consultorio.erp.financeiro.entity.Pagamento;
import br.com.consultorio.erp.financeiro.entity.StatusPagamento;
import br.com.consultorio.erp.financeiro.repository.PagamentoRepository;
import br.com.consultorio.erp.paciente.entity.Paciente;
import br.com.consultorio.erp.paciente.repository.PacienteRepository;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PacienteRepository pacienteRepository;
    private final AgendamentoRepository agendamentoRepository;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            PacienteRepository pacienteRepository,
            AgendamentoRepository agendamentoRepository) {

        this.pagamentoRepository = pagamentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    @Transactional
    public PagamentoResponse cadastrar(PagamentoRequest request) {
        Paciente paciente = buscarPaciente(request.pacienteId());

        Agendamento agendamento =
                buscarAgendamentoOpcional(request.agendamentoId());

        validarAgendamento(paciente, agendamento);

        Pagamento pagamento = new Pagamento();
        pagamento.setPaciente(paciente);
        pagamento.setAgendamento(agendamento);
        pagamento.setDescricao(request.descricao());
        pagamento.setValor(request.valor());
        pagamento.setDataVencimento(request.dataVencimento());
        pagamento.setStatus(StatusPagamento.PENDENTE);

        Pagamento pagamentoSalvo =
                pagamentoRepository.save(pagamento);

        return converterParaResponse(pagamentoSalvo);
    }

    @Transactional(readOnly = true)
    public List<PagamentoResponse> listarTodos() {
        return pagamentoRepository
                .findAllByOrderByDataVencimentoDesc()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PagamentoResponse buscarPorId(Long id) {
        return converterParaResponse(buscarPagamento(id));
    }

    @Transactional(readOnly = true)
    public List<PagamentoResponse> listarPorPaciente(Long pacienteId) {
        buscarPaciente(pacienteId);

        return pagamentoRepository
                .findByPacienteIdOrderByDataVencimentoDesc(pacienteId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PagamentoResponse> listarPorStatus(
            StatusPagamento status) {

        return pagamentoRepository
                .findByStatusOrderByDataVencimentoAsc(status)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional
    public PagamentoResponse atualizar(
            Long id,
            PagamentoRequest request) {

        Pagamento pagamento = buscarPagamento(id);

        if (pagamento.getStatus() != StatusPagamento.PENDENTE) {
            throw new IllegalArgumentException(
                    "Somente pagamentos pendentes podem ser alterados"
            );
        }

        Paciente paciente = buscarPaciente(request.pacienteId());

        Agendamento agendamento =
                buscarAgendamentoOpcional(request.agendamentoId());

        if (agendamento != null
                && pagamento.getAgendamento() != null
                && !agendamento.getId()
                        .equals(pagamento.getAgendamento().getId())) {

            validarAgendamento(paciente, agendamento);
        } else {
            validarRelacionamento(paciente, agendamento);
        }

        pagamento.setPaciente(paciente);
        pagamento.setAgendamento(agendamento);
        pagamento.setDescricao(request.descricao());
        pagamento.setValor(request.valor());
        pagamento.setDataVencimento(request.dataVencimento());

        Pagamento pagamentoAtualizado =
                pagamentoRepository.save(pagamento);

        return converterParaResponse(pagamentoAtualizado);
    }

    @Transactional
    public PagamentoResponse pagar(
            Long id,
            PagamentoBaixaRequest request) {

        Pagamento pagamento = buscarPagamento(id);

        if (pagamento.getStatus() == StatusPagamento.PAGO) {
            throw new IllegalArgumentException(
                    "O pagamento já foi realizado"
            );
        }

        if (pagamento.getStatus() == StatusPagamento.CANCELADO) {
            throw new IllegalArgumentException(
                    "Um pagamento cancelado não pode ser pago"
            );
        }

        pagamento.setStatus(StatusPagamento.PAGO);
        pagamento.setFormaPagamento(request.formaPagamento());
        pagamento.setDataPagamento(LocalDateTime.now());

        return converterParaResponse(
                pagamentoRepository.save(pagamento)
        );
    }

    @Transactional
    public PagamentoResponse cancelar(Long id) {
        Pagamento pagamento = buscarPagamento(id);

        if (pagamento.getStatus() == StatusPagamento.PAGO) {
            throw new IllegalArgumentException(
                    "Um pagamento realizado não pode ser cancelado"
            );
        }

        if (pagamento.getStatus() == StatusPagamento.CANCELADO) {
            throw new IllegalArgumentException(
                    "O pagamento já está cancelado"
            );
        }

        pagamento.setStatus(StatusPagamento.CANCELADO);

        return converterParaResponse(
                pagamentoRepository.save(pagamento)
        );
    }

    private Pagamento buscarPagamento(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pagamento não encontrado"
                ));
    }

    private Paciente buscarPaciente(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Paciente não encontrado"
                ));
    }

    private Agendamento buscarAgendamentoOpcional(Long id) {
        if (id == null) {
            return null;
        }

        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Agendamento não encontrado"
                ));
    }

    private void validarAgendamento(
            Paciente paciente,
            Agendamento agendamento) {

        validarRelacionamento(paciente, agendamento);

        if (agendamento != null
                && pagamentoRepository
                    .existsByAgendamentoIdAndStatusNot(
                        agendamento.getId(),
                        StatusPagamento.CANCELADO
                    )) {

            throw new IllegalArgumentException(
                    "Já existe uma cobrança para esse agendamento"
            );
        }
    }

    private void validarRelacionamento(
            Paciente paciente,
            Agendamento agendamento) {

        if (agendamento != null
                && !agendamento.getPaciente().getId()
                    .equals(paciente.getId())) {

            throw new IllegalArgumentException(
                    "O agendamento não pertence ao paciente informado"
            );
        }
    }

    private PagamentoResponse converterParaResponse(
            Pagamento pagamento) {

        Long agendamentoId = pagamento.getAgendamento() == null
                ? null
                : pagamento.getAgendamento().getId();

        return new PagamentoResponse(
                pagamento.getId(),
                pagamento.getPaciente().getId(),
                pagamento.getPaciente().getNomeCompleto(),
                agendamentoId,
                pagamento.getDescricao(),
                pagamento.getValor(),
                pagamento.getDataVencimento(),
                pagamento.getDataPagamento(),
                pagamento.getStatus(),
                pagamento.getFormaPagamento()
        );
    }
}