package br.com.motiva.repository;

import br.com.motiva.model.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para a entidade Equipe (tabela MTV_EQUIPE_MANUTENCAO).
 * Substitui o antigo EquipeManutencaoDAO de 375 linhas da Sprint 3.
 */
@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {

    /**
     * Derived query: busca equipes por turno (MANHA, TARDE, NOITE).
     * SQL gerado: SELECT * FROM MTV_EQUIPE_MANUTENCAO WHERE UPPER(DS_TURNO) = UPPER(?)
     */
    List<Equipe> findByTurnoIgnoreCase(String turno);

    /**
     * Derived query: busca equipes pelo status ('S' ou 'N').
     * SQL gerado: SELECT * FROM MTV_EQUIPE_MANUTENCAO WHERE UPPER(ST_ATIVA) = UPPER(?)
     */
    List<Equipe> findByAtivaIgnoreCase(String ativa);

    /**
     * Derived query: busca equipe por nome exato (ignora maiusculas/minusculas).
     */
    Optional<Equipe> findByNomeIgnoreCase(String nome);

    /**
     * Verifica duplicidade de nome.
     */
    boolean existsByNomeIgnoreCase(String nome);
}
