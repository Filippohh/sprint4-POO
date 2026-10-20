package br.com.motiva.repository;

import br.com.motiva.model.RelatorioPrioridade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio Spring Data JPA para a entidade RelatorioPrioridade (tabela MTV_RELATORIO_PRIORIDADE).
 */
@Repository
public interface RelatorioPrioridadeRepository extends JpaRepository<RelatorioPrioridade, Long> {

    /**
     * Derived query: lista todos os relatorios ordenados por data de geracao decrescente.
     */
    List<RelatorioPrioridade> findAllByOrderByDataGeracaoDesc();

    /**
     * Derived query: consulta relatorios gerados dentro de um intervalo de datas.
     * SQL gerado: SELECT * FROM MTV_RELATORIO_PRIORIDADE WHERE DT_GERACAO BETWEEN ? AND ? ORDER BY DT_GERACAO DESC
     */
    List<RelatorioPrioridade> findByDataGeracaoBetweenOrderByDataGeracaoDesc(LocalDateTime inicio, LocalDateTime fim);
}
