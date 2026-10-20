package br.com.motiva.service;

import br.com.motiva.dto.TrechoRequestDTO;
import br.com.motiva.dto.TrechoResponseDTO;
import br.com.motiva.exception.BusinessException;
import br.com.motiva.exception.ResourceNotFoundException;
import br.com.motiva.model.Trecho;
import br.com.motiva.repository.TrechoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servico de regras de negocio para Trechos de Rodovia.
 * Contem as validacoes de dominio da Sprint 1 e o motor de prioridade da Sprint 2.
 */
@Service
public class TrechoService {

    // Limiares de prioridade (em centimetros de vegetacao)
    public static final double LIMITE_URGENTE = 30.0;
    public static final double LIMITE_CRITICO = 20.0;
    public static final double LIMITE_ATENCAO = 10.0;

    public static final String URGENTE = "URGENTE";
    public static final String CRITICO = "CRITICO";
    public static final String ATENCAO = "ATENCAO";
    public static final String NORMAL  = "NORMAL";

    private final TrechoRepository trechoRepository;

    public TrechoService(TrechoRepository trechoRepository) {
        this.trechoRepository = trechoRepository;
    }

    /**
     * Lista todos os trechos cadastrados.
     */
    @Transactional(readOnly = true)
    public List<TrechoResponseDTO> listarTodos() {
        return trechoRepository.findAll().stream()
                .map(this::converterParaResponseDTO)
                .toList();
    }

    /**
     * Busca um trecho por ID ou lanca 404.
     */
    @Transactional(readOnly = true)
    public TrechoResponseDTO buscarPorId(Long id) {
        Trecho trecho = buscarEntidadePorId(id);
        return converterParaResponseDTO(trecho);
    }

    @Transactional(readOnly = true)
    public Trecho buscarEntidadePorId(Long id) {
        return trechoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trecho", id));
    }

    /**
     * Cria um novo trecho aplicando as regras de negocio da Sprint 1.
     */
    @Transactional
    public TrechoResponseDTO criar(TrechoRequestDTO dto) {
        validarRegrasDeNegocio(dto, null);

        Trecho trecho = new Trecho(
                dto.rodovia().trim().toUpperCase(),
                dto.kmInicial(),
                dto.kmFinal(),
                dto.nivelVegetacao(),
                dto.tipo().trim().toUpperCase()
        );

        Trecho salvo = trechoRepository.save(trecho);
        return converterParaResponseDTO(salvo);
    }

    /**
     * Atualiza um trecho existente.
     */
    @Transactional
    public TrechoResponseDTO atualizar(Long id, TrechoRequestDTO dto) {
        Trecho trecho = buscarEntidadePorId(id);
        validarRegrasDeNegocio(dto, id);

        trecho.setRodovia(dto.rodovia().trim().toUpperCase());
        trecho.setKmInicial(dto.kmInicial());
        trecho.setKmFinal(dto.kmFinal());
        trecho.setNivelVegetacao(dto.nivelVegetacao());
        trecho.setTipo(dto.tipo().trim().toUpperCase());
        trecho.setUltimaLeitura(LocalDate.now());

        Trecho atualizado = trechoRepository.save(trecho);
        return converterParaResponseDTO(atualizado);
    }

    /**
     * Remove um trecho pelo ID.
     */
    @Transactional
    public void remover(Long id) {
        Trecho trecho = buscarEntidadePorId(id);
        trechoRepository.delete(trecho);
    }

    /**
     * Consulta trechos por tipo (UMIDO ou SECO) via derived query.
     */
    @Transactional(readOnly = true)
    public List<TrechoResponseDTO> buscarPorTipo(String tipo) {
        return trechoRepository.findByTipoIgnoreCase(tipo).stream()
                .map(this::converterParaResponseDTO)
                .toList();
    }

    /**
     * Consulta trechos com vegetacao igual ou superior a um valor minimo via derived query.
     */
    @Transactional(readOnly = true)
    public List<TrechoResponseDTO> buscarPorVegetacaoMinima(Double minimo) {
        if (minimo == null || minimo < 0) {
            throw new BusinessException("O nivel minimo de vegetacao nao pode ser negativo.");
        }
        return trechoRepository.findByNivelVegetacaoGreaterThanEqual(minimo).stream()
                .map(this::converterParaResponseDTO)
                .toList();
    }

    // ===============================================================
    // Regras de Negocio e Validacoes (Sprint 1 e 2)
    // ===============================================================
    private void validarRegrasDeNegocio(TrechoRequestDTO dto, Long idAtual) {
        // Regra da Sprint 1: KM final deve ser maior que KM inicial
        if (dto.kmFinal() <= dto.kmInicial()) {
            throw new BusinessException("O KM final deve ser estritamente maior que o KM inicial.");
        }

        // Regra da Sprint 1: nivel de vegetacao nao pode ser negativo
        if (dto.nivelVegetacao() < 0) {
            throw new BusinessException("O nivel da vegetacao nao pode ser negativo.");
        }

        // Tipo deve ser UMIDO ou SECO
        String tipoUpper = dto.tipo() != null ? dto.tipo().trim().toUpperCase() : "";
        if (!"UMIDO".equals(tipoUpper) && !"SECO".equals(tipoUpper)) {
            throw new BusinessException("Tipo do trecho invalido. Deve ser UMIDO ou SECO.");
        }

        // Validar unicidade (NM_RODOVIA, NR_KM_INICIAL, NR_KM_FINAL) na criacao
        if (idAtual == null && trechoRepository.existsByRodoviaIgnoreCaseAndKmInicialAndKmFinal(
                dto.rodovia().trim(), dto.kmInicial(), dto.kmFinal())) {
            throw new BusinessException(String.format("Ja existe um trecho cadastrado na rodovia %s entre os KM %d e %d.",
                    dto.rodovia(), dto.kmInicial(), dto.kmFinal()));
        }
    }

    /**
     * Motor de classificacao de prioridade da Sprint 2.
     */
    public static String classificar(double nivel) {
        if (nivel >= LIMITE_URGENTE) return URGENTE;
        if (nivel >= LIMITE_CRITICO) return CRITICO;
        if (nivel >= LIMITE_ATENCAO) return ATENCAO;
        return NORMAL;
    }

    /**
     * Recomendacao de acao operacional baseada na classificacao da vegetacao.
     */
    public static String acaoRecomendada(String prioridade) {
        return switch (prioridade) {
            case URGENTE -> "Rocada mecanizada imediata";
            case CRITICO -> "Rocada mecanizada programada";
            case ATENCAO -> "Pulverizacao quimica";
            default      -> "Apenas monitorar";
        };
    }

    private TrechoResponseDTO converterParaResponseDTO(Trecho trecho) {
        double nivel = trecho.temSensorIoT() ? trecho.transmitirDadosSensor() : trecho.getNivelVegetacao();
        String prioridade = classificar(nivel);
        String acao = acaoRecomendada(prioridade);
        return TrechoResponseDTO.aPartirDe(trecho, prioridade, acao);
    }
}
