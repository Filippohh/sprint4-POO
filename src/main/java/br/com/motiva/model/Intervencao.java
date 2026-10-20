package br.com.motiva.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidade JPA que mapeia a tabela MTV_INTERVENCAO_OPERACIONAL.
 *
 * Registra o historico de intervencoes operacionais (Roçada Mecanizada ou Pulverização)
 * realizadas em cada trecho de rodovia.
 */
@Entity
@Table(name = "MTV_INTERVENCAO_OPERACIONAL")
public class Intervencao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_mtv_intervencao")
    @SequenceGenerator(name = "seq_mtv_intervencao", sequenceName = "SEQ_MTV_INTERVENCAO", allocationSize = 1)
    @Column(name = "ID_INTERVENCAO")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ID_TRECHO", nullable = false)
    private Trecho trecho;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_EQUIPE")
    private Equipe equipe;

    @Column(name = "TP_INTERVENCAO", nullable = false, length = 20)
    private String tipo;

    @Column(name = "DT_EXECUCAO", nullable = false)
    private LocalDate dataExecucao;

    @Column(name = "VL_NIVEL_ANTES", nullable = false)
    private Double nivelAntes;

    @Column(name = "VL_NIVEL_DEPOIS", nullable = false)
    private Double nivelDepois;

    @Column(name = "VL_CUSTO", nullable = false)
    private Double custo;

    public Intervencao() {
        this.dataExecucao = LocalDate.now();
    }

    public Intervencao(Trecho trecho, Equipe equipe, String tipo, LocalDate dataExecucao,
                       Double nivelAntes, Double nivelDepois, Double custo) {
        this.trecho = trecho;
        this.equipe = equipe;
        this.tipo = tipo != null ? tipo.toUpperCase() : null;
        this.dataExecucao = dataExecucao != null ? dataExecucao : LocalDate.now();
        this.nivelAntes = nivelAntes;
        this.nivelDepois = nivelDepois;
        this.custo = custo;
    }

    public Intervencao(Long id, Trecho trecho, Equipe equipe, String tipo, LocalDate dataExecucao,
                       Double nivelAntes, Double nivelDepois, Double custo) {
        this.id = id;
        this.trecho = trecho;
        this.equipe = equipe;
        this.tipo = tipo != null ? tipo.toUpperCase() : null;
        this.dataExecucao = dataExecucao != null ? dataExecucao : LocalDate.now();
        this.nivelAntes = nivelAntes;
        this.nivelDepois = nivelDepois;
        this.custo = custo;
    }

    // Regras de Negocio
    public double reducaoObtida() {
        if (nivelAntes != null && nivelDepois != null) {
            return nivelAntes - nivelDepois;
        }
        return 0.0;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Trecho getTrecho() {
        return trecho;
    }

    public void setTrecho(Trecho trecho) {
        this.trecho = trecho;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo != null ? tipo.toUpperCase() : null;
    }

    public LocalDate getDataExecucao() {
        return dataExecucao;
    }

    public void setDataExecucao(LocalDate dataExecucao) {
        this.dataExecucao = dataExecucao;
    }

    public Double getNivelAntes() {
        return nivelAntes;
    }

    public void setNivelAntes(Double nivelAntes) {
        this.nivelAntes = nivelAntes;
    }

    public Double getNivelDepois() {
        return nivelDepois;
    }

    public void setNivelDepois(Double nivelDepois) {
        this.nivelDepois = nivelDepois;
    }

    public Double getCusto() {
        return custo;
    }

    public void setCusto(Double custo) {
        this.custo = custo;
    }
}
