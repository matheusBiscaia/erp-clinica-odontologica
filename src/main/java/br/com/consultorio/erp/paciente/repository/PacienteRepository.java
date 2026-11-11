package br.com.consultorio.erp.paciente.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.consultorio.erp.paciente.entity.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, Long id);

    List<Paciente> findByAtivoTrue();
}