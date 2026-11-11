package br.com.consultorio.erp.prontuario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.consultorio.erp.prontuario.entity.Prontuario;

public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    List<Prontuario> findAllByOrderByDataAtendimentoDesc();

    List<Prontuario> findByPacienteIdOrderByDataAtendimentoDesc(Long pacienteId);
}