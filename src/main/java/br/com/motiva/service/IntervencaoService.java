package br.com.motiva.service;

import br.com.motiva.dto.IntervencaoRequestDTO;
import br.com.motiva.dto.IntervencaoResponseDTO;
import br.com.motiva.exception.BusinessException;
import br.com.motiva.exception.ResourceNotFoundException;
import br.com.motiva.model.Equipe;
import br.com.motiva.model.Intervencao;
import br.com.motiva.model.Trecho;
import br.com.motiva.repository.EquipeRepository;
import br.com.motiva.repository.IntervencaoRepository;
import br.com.motiva.repository.TrechoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servico de regras de negocio para Intervencoes Operacionais.
 */
@Service
public class IntervencaoService {

    private final IntervencaoRepository intervencaoRepository;
    private final TrechoRepository trechoRepository;
    private final EquipeRepository equipeRepository;

    public IntervencaoService(IntervencaoRepository intervencaoRepository,
                              TrechoRepository trechoRepository,
                              EquipeRepository equipeRepository) {
        this.intervencaoRepository = intervencaoRepository;
        this.trechoRepository = trechoRepository;
        this.equipeRepository = equipeRepository;
    }

    @Transactional(readOnly = true)
    public List<IntervencaoResponseDTO> listarTodas() {
        return intervencaoRepository.findAll().stream()
                .map(IntervencaoResponseDTO::aPartirDe)
                .toList();
    }

    @Transactional(readOnly = true)
    public IntervencaoResponseDTO buscarPorId(Long id) {
        Intervencao intervencao = buscarEntidadePorId(id);
        return IntervencaoResponseDTO.aPartirDe(intervencao);
    }

    @Transactional(readOnly = true)
    public Intervencao buscarEntidadePorId(Long id) {
        return intervencaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervencao", id));
    }

    /**
     * Registra uma nova intervencao e atualiza o nivel de vegetacao do trecho (regra operacional).
     */
    @Transactional
    public IntervencaoResponseDTO criar(IntervencaoRequestDTO dto) {
        validarRegrasDeNegocio(dto);

        Trecho trecho = trechoRepository.findById(dto.idTrecho())
                .orElseThrow(() -> new ResourceNotFoundException("Trecho", dto.idTrecho()));

        Equipe equipe = null;
        if (dto.idEquipe() != null) {
            equipe = equipeRepository.findById(dto.idEquipe())
                    .orElseThrow(() -> new ResourceNotFoundException("Equipe", dto.idEquipe()));
        }

        LocalDate data = dto.dataExecucao() != null ? dto.dataExecucao() : LocalDate.now();

        Intervencao intervencao = new Intervencao(
                trecho,
                equipe,
                dto.tipo().trim().toUpperCase(),
                data,
                dto.nivelAntes(),
                dto.nivelDepois(),
                dto.custo()
        );

        // Regra da Sprint 2: A intervencao altera o estado do trecho para o novo nivel de vegetacao
        trecho.setNivelVegetacao(dto.nivelDepois());
        trecho.setUltimaLeitura(data);
        trechoRepository.save(trecho);

        Intervencao salva = intervencaoRepository.save(intervencao);
        return IntervencaoResponseDTO.aPartirDe(salva);
    }

    @Transactional
    public IntervencaoResponseDTO atualizar(Long id, IntervencaoRequestDTO dto) {
        Intervencao intervencao = buscarEntidadePorId(id);
        validarRegrasDeNegocio(dto);

        Trecho trecho = trechoRepository.findById(dto.idTrecho())
                .orElseThrow(() -> new ResourceNotFoundException("Trecho", dto.idTrecho()));

        Equipe equipe = null;
        if (dto.idEquipe() != null) {
            equipe = equipeRepository.findById(dto.idEquipe())
                    .orElseThrow(() -> new ResourceNotFoundException("Equipe", dto.idEquipe()));
        }

        intervencao.setTrecho(trecho);
        intervencao.setEquipe(equipe);
        intervencao.setTipo(dto.tipo().trim().toUpperCase());
        if (dto.dataExecucao() != null) {
            intervencao.setDataExecucao(dto.dataExecucao());
        }
        intervencao.setNivelAntes(dto.nivelAntes());
        intervencao.setNivelDepois(dto.nivelDepois());
        intervencao.setCusto(dto.custo());

        Intervencao atualizada = intervencaoRepository.save(intervencao);
        return IntervencaoResponseDTO.aPartirDe(atualizada);
    }

    @Transactional
    public void remover(Long id) {
        Intervencao intervencao = buscarEntidadePorId(id);
        intervencaoRepository.delete(intervencao);
    }

    @Transactional(readOnly = true)
    public List<IntervencaoResponseDTO> buscarPorTrecho(Long trechoId) {
        return intervencaoRepository.findByTrechoIdOrderByDataExecucaoDesc(trechoId).stream()
                .map(IntervencaoResponseDTO::aPartirDe)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IntervencaoResponseDTO> buscarPorTipo(String tipo) {
        return intervencaoRepository.findByTipoIgnoreCase(tipo).stream()
                .map(IntervencaoResponseDTO::aPartirDe)
                .toList();
    }

    private void validarRegrasDeNegocio(IntervencaoRequestDTO dto) {
        if (dto.nivelAntes() < 0 || dto.nivelDepois() < 0) {
            throw new BusinessException("Os niveis de vegetacao antes e depois nao podem ser negativos.");
        }
        if (dto.custo() < 0) {
            throw new BusinessException("O custo da intervencao nao pode ser negativo.");
        }

        String tipoUpper = dto.tipo() != null ? dto.tipo().trim().toUpperCase() : "";
        if (!"ROCADA_MECANIZADA".equals(tipoUpper) && !"PULVERIZACAO".equals(tipoUpper)) {
            throw new BusinessException("Tipo de intervencao invalido. Deve ser ROCADA_MECANIZADA ou PULVERIZACAO.");
        }
    }
}
