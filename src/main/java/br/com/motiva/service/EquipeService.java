package br.com.motiva.service;

import br.com.motiva.dto.EquipeRequestDTO;
import br.com.motiva.dto.EquipeResponseDTO;
import br.com.motiva.exception.BusinessException;
import br.com.motiva.exception.ResourceNotFoundException;
import br.com.motiva.model.Equipe;
import br.com.motiva.repository.EquipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servico de regras de negocio para Equipes de Manutencao.
 */
@Service
public class EquipeService {

    private final EquipeRepository equipeRepository;

    public EquipeService(EquipeRepository equipeRepository) {
        this.equipeRepository = equipeRepository;
    }

    @Transactional(readOnly = true)
    public List<EquipeResponseDTO> listarTodas() {
        return equipeRepository.findAll().stream()
                .map(EquipeResponseDTO::aPartirDe)
                .toList();
    }

    @Transactional(readOnly = true)
    public EquipeResponseDTO buscarPorId(Long id) {
        Equipe equipe = buscarEntidadePorId(id);
        return EquipeResponseDTO.aPartirDe(equipe);
    }

    @Transactional(readOnly = true)
    public Equipe buscarEntidadePorId(Long id) {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe", id));
    }

    @Transactional
    public EquipeResponseDTO criar(EquipeRequestDTO dto) {
        validarRegrasDeNegocio(dto, null);

        String statusAtiva = dto.ativa() != null ? dto.ativa().trim().toUpperCase() : "S";
        Equipe equipe = new Equipe(
                dto.nome().trim(),
                dto.turno().trim().toUpperCase(),
                dto.qtIntegrantes(),
                statusAtiva
        );

        Equipe salva = equipeRepository.save(equipe);
        return EquipeResponseDTO.aPartirDe(salva);
    }

    @Transactional
    public EquipeResponseDTO atualizar(Long id, EquipeRequestDTO dto) {
        Equipe equipe = buscarEntidadePorId(id);
        validarRegrasDeNegocio(dto, id);

        equipe.setNome(dto.nome().trim());
        equipe.setTurno(dto.turno().trim().toUpperCase());
        equipe.setQtIntegrantes(dto.qtIntegrantes());
        if (dto.ativa() != null) {
            equipe.setAtiva(dto.ativa().trim().toUpperCase());
        }

        Equipe atualizada = equipeRepository.save(equipe);
        return EquipeResponseDTO.aPartirDe(atualizada);
    }

    @Transactional
    public void remover(Long id) {
        Equipe equipe = buscarEntidadePorId(id);
        equipeRepository.delete(equipe);
    }

    @Transactional(readOnly = true)
    public List<EquipeResponseDTO> buscarPorTurno(String turno) {
        return equipeRepository.findByTurnoIgnoreCase(turno).stream()
                .map(EquipeResponseDTO::aPartirDe)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EquipeResponseDTO> buscarAtivas() {
        return equipeRepository.findByAtivaIgnoreCase("S").stream()
                .map(EquipeResponseDTO::aPartirDe)
                .toList();
    }

    private void validarRegrasDeNegocio(EquipeRequestDTO dto, Long idAtual) {
        if (dto.qtIntegrantes() <= 0) {
            throw new BusinessException("A equipe precisa ter pelo menos 1 integrante.");
        }

        String turnoUpper = dto.turno() != null ? dto.turno().trim().toUpperCase() : "";
        if (!"MANHA".equals(turnoUpper) && !"TARDE".equals(turnoUpper) && !"NOITE".equals(turnoUpper)) {
            throw new BusinessException("Turno invalido. Os valores permitidos sao MANHA, TARDE ou NOITE.");
        }

        if (dto.ativa() != null && !"S".equalsIgnoreCase(dto.ativa()) && !"N".equalsIgnoreCase(dto.ativa())) {
            throw new BusinessException("Status da equipe invalido. Deve ser 'S' (ativa) ou 'N' (inativa).");
        }

        // Validar unicidade do nome da equipe
        equipeRepository.findByNomeIgnoreCase(dto.nome().trim()).ifPresent(existente -> {
            if (idAtual == null || !existente.getId().equals(idAtual)) {
                throw new BusinessException("Ja existe uma equipe cadastrada com o nome '" + dto.nome() + "'.");
            }
        });
    }
}
