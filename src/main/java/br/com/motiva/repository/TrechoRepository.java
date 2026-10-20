package br.com.motiva.repository;

import br.com.motiva.model.Trecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para a entidade Trecho (tabela MTV_TRECHO_RODOVIA).
 * O Spring Data gera a implementacao concreta em tempo de execucao.
 */
@Repository
public interface TrechoRepository extends JpaRepository<Trecho, Long> {

    /**
     * Derived query: busca trechos com vegetacao igual ou superior ao limite.
     * SQL gerado: SELECT * FROM MTV_TRECHO_RODOVIA WHERE VL_NIVEL_VEGETACAO >= ?
     */
    List<Trecho> findByNivelVegetacaoGreaterThanEqual(Double minimo);

    /**
     * Derived query: busca trechos por tipo ('UMIDO' ou 'SECO').
     * SQL gerado: SELECT * FROM MTV_TRECHO_RODOVIA WHERE UPPER(TP_TRECHO) = UPPER(?)
     */
    List<Trecho> findByTipoIgnoreCase(String tipo);

    /**
     * Derived query: busca trechos por rodovia.
     */
    List<Trecho> findByRodoviaIgnoreCase(String rodovia);

    /**
     * Derived query: verifica se ja existe trecho na mesma rodovia e km.
     */
    boolean existsByRodoviaIgnoreCaseAndKmInicialAndKmFinal(String rodovia, Integer kmInicial, Integer kmFinal);
}
