package br.com.motiva.service;

import br.com.motiva.dto.RelatorioItemDTO;
import br.com.motiva.dto.RelatorioResponseDTO;
import br.com.motiva.exception.BusinessException;
import br.com.motiva.exception.ResourceNotFoundException;
import br.com.motiva.model.RelatorioPrioridade;
import br.com.motiva.model.Trecho;
import br.com.motiva.repository.RelatorioPrioridadeRepository;
import br.com.motiva.repository.TrechoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servico que implementa o motor de classificacao e geracao de relatorios de prioridade.
 * Converte o antigo GeradorRelatorio (Sprint 2/3) em um servico REST injetavel.
 */
@Service
public class RelatorioService {

    private final TrechoRepository trechoRepository;
    private final RelatorioPrioridadeRepository relatorioPrioridadeRepository;

    public RelatorioService(TrechoRepository trechoRepository,
                            RelatorioPrioridadeRepository relatorioPrioridadeRepository) {
        this.trechoRepository = trechoRepository;
        this.relatorioPrioridadeRepository = relatorioPrioridadeRepository;
    }

    /**
     * Gera o relatorio a partir dos trechos atuais no banco, classifica cada um deles,
     * persiste o resultado no historico e devolve o DTO com o detalhamento.
     */
    @Transactional
    public RelatorioResponseDTO gerarRelatorioAtual() {
        List<Trecho> trechos = trechoRepository.findAll();

        int qtUrgente = 0;
        int qtCritico = 0;
        int qtAtencao = 0;
        int qtNormal = 0;

        List<RelatorioItemDTO> itens = new ArrayList<>();

        for (Trecho trecho : trechos) {
            double nivel = trecho.temSensorIoT() ? trecho.transmitirDadosSensor() : trecho.getNivelVegetacao();
            String prioridade = TrechoService.classificar(nivel);
            String acao = TrechoService.acaoRecomendada(prioridade);

            switch (prioridade) {
                case TrechoService.URGENTE -> qtUrgente++;
                case TrechoService.CRITICO -> qtCritico++;
                case TrechoService.ATENCAO -> qtAtencao++;
                default                    -> qtNormal++;
            }

            itens.add(RelatorioItemDTO.criar(trecho, prioridade, acao));
        }

        String resumo = montarResumo(trechos.size(), qtUrgente, qtCritico, qtAtencao, qtNormal);

        RelatorioPrioridade relatorio = new RelatorioPrioridade(
                qtUrgente,
                qtCritico,
                qtAtencao,
                qtNormal,
                resumo
        );

        RelatorioPrioridade salvo = relatorioPrioridadeRepository.save(relatorio);
        return RelatorioResponseDTO.aPartirDe(salvo, itens);
    }

    /**
     * Lista todo o historico de relatorios ordenado por data decrescente.
     */
    @Transactional(readOnly = true)
    public List<RelatorioResponseDTO> listarHistorico() {
        return relatorioPrioridadeRepository.findAllByOrderByDataGeracaoDesc().stream()
                .map(rel -> RelatorioResponseDTO.aPartirDe(rel, null))
                .toList();
    }

    /**
     * Busca um relatorio especifico por ID.
     */
    @Transactional(readOnly = true)
    public RelatorioResponseDTO buscarPorId(Long id) {
        RelatorioPrioridade relatorio = relatorioPrioridadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relatorio de Prioridade", id));
        return RelatorioResponseDTO.aPartirDe(relatorio, null);
    }

    /**
     * Consulta o historico de relatorios gerados dentro de um intervalo de datas.
     */
    @Transactional(readOnly = true)
    public List<RelatorioResponseDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new BusinessException("As datas de inicio e fim sao obrigatorias para a consulta por periodo.");
        }
        if (fim.isBefore(inicio)) {
            throw new BusinessException("A data final nao pode ser anterior a data inicial.");
        }

        return relatorioPrioridadeRepository.findByDataGeracaoBetweenOrderByDataGeracaoDesc(inicio, fim).stream()
                .map(rel -> RelatorioResponseDTO.aPartirDe(rel, null))
                .toList();
    }

    private String montarResumo(int total, int qtUrgente, int qtCritico, int qtAtencao, int qtNormal) {
        int acaoImediata = qtUrgente + qtCritico;
        return String.format(
                "%d trecho(s) analisado(s): %d urgente(s), %d critico(s), %d em atencao e %d normal(is). "
                        + "%d trecho(s) exigem intervencao imediata.",
                total, qtUrgente, qtCritico, qtAtencao, qtNormal, acaoImediata
        );
    }
}
