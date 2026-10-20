package br.com.motiva.repository;

import br.com.motiva.model.Intervencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para a entidade Intervencao (tabela MTV_INTERVENCAO_OPERACIONAL).
 */
@Repository
public interface IntervencaoRepository extends JpaRepository<Intervencao, Long> {

    /**
     * Derived query: busca intervencoes por tipo (ROCADA_MECANIZADA ou PULVERIZACAO).
     * SQL gerado: SELECT * FROM MTV_INTERVENCAO_OPERACIONAL WHERE UPPER(TP_INTERVENCAO) = UPPER(?)
     */
    List<Intervencao> findByTipoIgnoreCase(String tipo);

    /**
     * Derived query: busca historico de intervencoes de um trecho especifico.
     * SQL gerado: SELECT * FROM MTV_INTERVENCAO_OPERACIONAL WHERE ID_TRECHO = ?
     */
    List<Intervencao> findByTrechoIdOrderByDataExecucaoDesc(Long trechoId);

    /**
     * Derived query: busca intervencoes executadas por uma equipe especifica.
     */
    List<Intervencao> findByEquipeIdOrderByDataExecucaoDesc(Long equipeId);
}
