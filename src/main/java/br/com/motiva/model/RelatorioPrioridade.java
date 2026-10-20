package br.com.motiva.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade JPA que mapeia a tabela MTV_RELATORIO_PRIORIDADE.
 *
 * Registra o historico de relatorios de prioridade de corte/manutencao
 * gerados pelo motor de regras da aplicacao.
 */
@Entity
@Table(name = "MTV_RELATORIO_PRIORIDADE")
public class RelatorioPrioridade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_mtv_relatorio")
    @SequenceGenerator(name = "seq_mtv_relatorio", sequenceName = "SEQ_MTV_RELATORIO", allocationSize = 1)
    @Column(name = "ID_RELATORIO")
    private Long id;

    @Column(name = "DT_GERACAO", nullable = false)
    private LocalDateTime dataGeracao;

    @Column(name = "QT_URGENTE", nullable = false)
    private Integer qtUrgente = 0;

    @Column(name = "QT_CRITICO", nullable = false)
    private Integer qtCritico = 0;

    @Column(name = "QT_ATENCAO", nullable = false)
    private Integer qtAtencao = 0;

    @Column(name = "QT_NORMAL", nullable = false)
    private Integer qtNormal = 0;

    @Column(name = "DS_RESUMO", length = 500)
    private String resumo;

    public RelatorioPrioridade() {
        this.dataGeracao = LocalDateTime.now();
    }

    public RelatorioPrioridade(Integer qtUrgente, Integer qtCritico, Integer qtAtencao, Integer qtNormal, String resumo) {
        this.dataGeracao = LocalDateTime.now();
        this.qtUrgente = qtUrgente;
        this.qtCritico = qtCritico;
        this.qtAtencao = qtAtencao;
        this.qtNormal = qtNormal;
        this.resumo = resumo;
    }

    public RelatorioPrioridade(Long id, LocalDateTime dataGeracao, Integer qtUrgente, Integer qtCritico,
                               Integer qtAtencao, Integer qtNormal, String resumo) {
        this.id = id;
        this.dataGeracao = dataGeracao != null ? dataGeracao : LocalDateTime.now();
        this.qtUrgente = qtUrgente;
        this.qtCritico = qtCritico;
        this.qtAtencao = qtAtencao;
        this.qtNormal = qtNormal;
        this.resumo = resumo;
    }

    public int totalTrechos() {
        return (qtUrgente != null ? qtUrgente : 0) +
               (qtCritico != null ? qtCritico : 0) +
               (qtAtencao != null ? qtAtencao : 0) +
               (qtNormal != null ? qtNormal : 0);
    }

    public int totalAcaoImediata() {
        return (qtUrgente != null ? qtUrgente : 0) +
               (qtCritico != null ? qtCritico : 0);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Integer getQtUrgente() {
        return qtUrgente;
    }

    public void setQtUrgente(Integer qtUrgente) {
        this.qtUrgente = qtUrgente;
    }

    public Integer getQtCritico() {
        return qtCritico;
    }

    public void setQtCritico(Integer qtCritico) {
        this.qtCritico = qtCritico;
    }

    public Integer getQtAtencao() {
        return qtAtencao;
    }

    public void setQtAtencao(Integer qtAtencao) {
        this.qtAtencao = qtAtencao;
    }

    public Integer getQtNormal() {
        return qtNormal;
    }

    public void setQtNormal(Integer qtNormal) {
        this.qtNormal = qtNormal;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }
}
