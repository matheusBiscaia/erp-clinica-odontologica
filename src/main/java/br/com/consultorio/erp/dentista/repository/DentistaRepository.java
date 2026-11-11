package br.com.consultorio.erp.dentista.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.consultorio.erp.dentista.entity.Dentista;

public interface DentistaRepository
        extends JpaRepository<Dentista, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByCro(String cro);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByCroAndIdNot(String cro, Long id);

    List<Dentista> findByAtivoTrue();
}