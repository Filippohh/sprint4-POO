package br.com.motiva.model;

/**
 * Interface originaria da Sprint 2 que define o contrato para
 * trechos monitorados por sensores IoT.
 */
public interface MonitoravelViaIoT {

    /**
     * Simula a transmissao de dados de telemetria coletados pelo sensor instalado no trecho.
     * @return nivel de vegetacao capturado pelo sensor (em centimetros)
     */
    double transmitirDadosSensor();
}
