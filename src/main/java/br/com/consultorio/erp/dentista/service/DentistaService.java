package br.com.consultorio.erp.dentista.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.consultorio.erp.dentista.dto.DentistaRequest;
import br.com.consultorio.erp.dentista.dto.DentistaResponse;
import br.com.consultorio.erp.dentista.entity.Dentista;
import br.com.consultorio.erp.dentista.repository.DentistaRepository;

@Service
public class DentistaService {

    private final DentistaRepository dentistaRepository;

    public DentistaService(DentistaRepository dentistaRepository) {
        this.dentistaRepository = dentistaRepository;
    }

    @Transactional
    public DentistaResponse cadastrar(DentistaRequest request) {
        if (dentistaRepository.existsByCpf(request.cpf())) {
            throw new IllegalArgumentException(
                    "Já existe um dentista com esse CPF"
            );
        }

        if (dentistaRepository.existsByCro(request.cro())) {
            throw new IllegalArgumentException(
                    "Já existe um dentista com esse CRO"
            );
        }

        Dentista dentista = new Dentista();
        dentista.setNomeCompleto(request.nomeCompleto());
        dentista.setCpf(request.cpf());
        dentista.setCro(request.cro());
        dentista.setEspecialidade(request.especialidade());
        dentista.setTelefone(request.telefone());
        dentista.setEmail(request.email());
        dentista.setAtivo(true);

        Dentista dentistaSalvo = dentistaRepository.save(dentista);

        return converterParaResponse(dentistaSalvo);
    }

    public List<DentistaResponse> listarTodos() {
        return dentistaRepository.findByAtivoTrue()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public DentistaResponse buscarPorId(Long id) {
        Dentista dentista = buscarEntidadePorId(id);

        return converterParaResponse(dentista);
    }

    @Transactional
    public DentistaResponse atualizar(
            Long id,
            DentistaRequest request) {

        Dentista dentista = buscarEntidadePorId(id);

        if (dentistaRepository.existsByCpfAndIdNot(
                request.cpf(), id)) {

            throw new IllegalArgumentException(
                    "Já existe outro dentista com esse CPF"
            );
        }

        if (dentistaRepository.existsByCroAndIdNot(
                request.cro(), id)) {

            throw new IllegalArgumentException(
                    "Já existe outro dentista com esse CRO"
            );
        }

        dentista.setNomeCompleto(request.nomeCompleto());
        dentista.setCpf(request.cpf());
        dentista.setCro(request.cro());
        dentista.setEspecialidade(request.especialidade());
        dentista.setTelefone(request.telefone());
        dentista.setEmail(request.email());

        Dentista dentistaAtualizado =
                dentistaRepository.save(dentista);

        return converterParaResponse(dentistaAtualizado);
    }

    @Transactional
    public void desativar(Long id) {
        Dentista dentista = buscarEntidadePorId(id);

        dentista.setAtivo(false);
        dentistaRepository.save(dentista);
    }

    @Transactional
    public DentistaResponse reativar(Long id) {
        Dentista dentista = buscarEntidadePorId(id);

        dentista.setAtivo(true);

        Dentista dentistaReativado =
                dentistaRepository.save(dentista);

        return converterParaResponse(dentistaReativado);
    }

    private Dentista buscarEntidadePorId(Long id) {
        return dentistaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dentista não encontrado"
                ));
    }

    private DentistaResponse converterParaResponse(
            Dentista dentista) {

        return new DentistaResponse(
                dentista.getId(),
                dentista.getNomeCompleto(),
                dentista.getCpf(),
                dentista.getCro(),
                dentista.getEspecialidade(),
                dentista.getTelefone(),
                dentista.getEmail(),
                dentista.isAtivo()
        );
    }
}