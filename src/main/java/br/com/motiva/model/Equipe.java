package br.com.motiva.model;

import jakarta.persistence.*;

/**
 * Entidade JPA que mapeia a tabela MTV_EQUIPE_MANUTENCAO.
 *
 * Representa as equipes de campo responsaveis por roçada e pulverização.
 */
@Entity
@Table(name = "MTV_EQUIPE_MANUTENCAO")
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_mtv_equipe")
    @SequenceGenerator(name = "seq_mtv_equipe", sequenceName = "SEQ_MTV_EQUIPE", allocationSize = 1)
    @Column(name = "ID_EQUIPE")
    private Long id;

    @Column(name = "NM_EQUIPE", nullable = false, unique = true, length = 60)
    private String nome;

    @Column(name = "DS_TURNO", nullable = false, length = 10)
    private String turno;

    @Column(name = "QT_INTEGRANTES", nullable = false)
    private Integer qtIntegrantes = 1;

    @Column(name = "ST_ATIVA", nullable = false, length = 1)
    private String ativa = "S";

    public Equipe() {
    }

    public Equipe(String nome, String turno, Integer qtIntegrantes, String ativa) {
        this.nome = nome;
        this.turno = turno != null ? turno.toUpperCase() : null;
        this.qtIntegrantes = qtIntegrantes;
        this.ativa = ativa != null ? ativa.toUpperCase() : "S";
    }

    public Equipe(Long id, String nome, String turno, Integer qtIntegrantes, String ativa) {
        this.id = id;
        this.nome = nome;
        this.turno = turno != null ? turno.toUpperCase() : null;
        this.qtIntegrantes = qtIntegrantes;
        this.ativa = ativa != null ? ativa.toUpperCase() : "S";
    }

    public boolean isAtiva() {
        return "S".equalsIgnoreCase(this.ativa);
    }

    public void setAtivaBoolean(boolean ativa) {
        this.ativa = ativa ? "S" : "N";
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno != null ? turno.toUpperCase() : null;
    }

    public Integer getQtIntegrantes() {
        return qtIntegrantes;
    }

    public void setQtIntegrantes(Integer qtIntegrantes) {
        this.qtIntegrantes = qtIntegrantes;
    }

    public String getAtiva() {
        return ativa;
    }

    public void setAtiva(String ativa) {
        this.ativa = ativa != null ? ativa.toUpperCase() : "S";
    }
}
