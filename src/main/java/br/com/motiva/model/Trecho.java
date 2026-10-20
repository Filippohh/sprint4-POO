package br.com.motiva.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidade JPA que mapeia a tabela MTV_TRECHO_RODOVIA.
 *
 * Representa os trechos monitorados da malha rodoviaria.
 * Implementa MonitoravelViaIoT para trechos com tecnologia IoT (TP_TRECHO = 'UMIDO').
 */
@Entity
@Table(name = "MTV_TRECHO_RODOVIA")
public class Trecho implements MonitoravelViaIoT {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_mtv_trecho")
    @SequenceGenerator(name = "seq_mtv_trecho", sequenceName = "SEQ_MTV_TRECHO", allocationSize = 1)
    @Column(name = "ID_TRECHO")
    private Long id;

    @Column(name = "NM_RODOVIA", nullable = false, length = 20)
    private String rodovia;

    @Column(name = "NR_KM_INICIAL", nullable = false)
    private Integer kmInicial;

    @Column(name = "NR_KM_FINAL", nullable = false)
    private Integer kmFinal;

    @Column(name = "VL_NIVEL_VEGETACAO", nullable = false)
    private Double nivelVegetacao;

    @Column(name = "TP_TRECHO", nullable = false, length = 10)
    private String tipo;

    @Column(name = "DT_ULTIMA_LEITURA")
    private LocalDate ultimaLeitura;

    public Trecho() {
        this.ultimaLeitura = LocalDate.now();
    }

    public Trecho(String rodovia, Integer kmInicial, Integer kmFinal, Double nivelVegetacao, String tipo) {
        this.rodovia = rodovia;
        this.kmInicial = kmInicial;
        this.kmFinal = kmFinal;
        this.nivelVegetacao = nivelVegetacao;
        this.tipo = tipo != null ? tipo.toUpperCase() : null;
        this.ultimaLeitura = LocalDate.now();
    }

    public Trecho(Long id, String rodovia, Integer kmInicial, Integer kmFinal, Double nivelVegetacao, String tipo, LocalDate ultimaLeitura) {
        this.id = id;
        this.rodovia = rodovia;
        this.kmInicial = kmInicial;
        this.kmFinal = kmFinal;
        this.nivelVegetacao = nivelVegetacao;
        this.tipo = tipo != null ? tipo.toUpperCase() : null;
        this.ultimaLeitura = ultimaLeitura != null ? ultimaLeitura : LocalDate.now();
    }

    // Regras de Negocio e Polimorfismo herdados das Sprints 1 e 2
    public boolean temSensorIoT() {
        return "UMIDO".equalsIgnoreCase(this.tipo);
    }

    @Override
    public double transmitirDadosSensor() {
        return this.nivelVegetacao != null ? this.nivelVegetacao : 0.0;
    }

    public void registrarCrescimento(double taxa) {
        if (taxa > 0) {
            double fator = "UMIDO".equalsIgnoreCase(this.tipo) ? 2.0 : ("SECO".equalsIgnoreCase(this.tipo) ? 0.5 : 1.0);
            this.nivelVegetacao = (this.nivelVegetacao != null ? this.nivelVegetacao : 0.0) + (taxa * fator);
            this.ultimaLeitura = LocalDate.now();
        }
    }

    public String identificacao() {
        return String.format("%s KM %d-%d", rodovia, kmInicial, kmFinal);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRodovia() {
        return rodovia;
    }

    public void setRodovia(String rodovia) {
        this.rodovia = rodovia;
    }

    public Integer getKmInicial() {
        return kmInicial;
    }

    public void setKmInicial(Integer kmInicial) {
        this.kmInicial = kmInicial;
    }

    public Integer getKmFinal() {
        return kmFinal;
    }

    public void setKmFinal(Integer kmFinal) {
        this.kmFinal = kmFinal;
    }

    public Double getNivelVegetacao() {
        return nivelVegetacao;
    }

    public void setNivelVegetacao(Double nivelVegetacao) {
        this.nivelVegetacao = nivelVegetacao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo != null ? tipo.toUpperCase() : null;
    }

    public LocalDate getUltimaLeitura() {
        return ultimaLeitura;
    }

    public void setUltimaLeitura(LocalDate ultimaLeitura) {
        this.ultimaLeitura = ultimaLeitura;
    }
}
