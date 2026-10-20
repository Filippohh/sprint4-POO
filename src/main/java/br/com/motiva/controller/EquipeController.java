package br.com.motiva.controller;

import br.com.motiva.dto.EquipeRequestDTO;
import br.com.motiva.dto.EquipeResponseDTO;
import br.com.motiva.service.EquipeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller REST para o gerenciamento de Equipes de Manutencao.
 * Rotas base: /api/equipes
 */
@RestController
@RequestMapping("/api/equipes")
public class EquipeController {

    private final EquipeService equipeService;

    public EquipeController(EquipeService equipeService) {
        this.equipeService = equipeService;
    }

    /**
     * GET /api/equipes - Listar todas as equipes
     */
    @GetMapping
    public ResponseEntity<List<EquipeResponseDTO>> listarTodas() {
        return ResponseEntity.ok(equipeService.listarTodas());
    }

    /**
     * GET /api/equipes/{id} - Buscar equipe por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EquipeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(equipeService.buscarPorId(id));
    }

    /**
     * POST /api/equipes - Criar nova equipe (201 Created)
     */
    @PostMapping
    public ResponseEntity<EquipeResponseDTO> criar(@Valid @RequestBody EquipeRequestDTO dto) {
        EquipeResponseDTO criada = equipeService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.id())
                .toUri();
        return ResponseEntity.created(location).body(criada);
    }

    /**
     * PUT /api/equipes/{id} - Atualizar equipe existente (200 OK)
     */
    @PutMapping("/{id}")
    public ResponseEntity<EquipeResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EquipeRequestDTO dto) {
        return ResponseEntity.ok(equipeService.atualizar(id, dto));
    }

    /**
     * DELETE /api/equipes/{id} - Remover equipe (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        equipeService.remover(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/equipes/turno/{turno} - Filtrar equipes por turno (MANHA, TARDE ou NOITE)
     */
    @GetMapping("/turno/{turno}")
    public ResponseEntity<List<EquipeResponseDTO>> buscarPorTurno(@PathVariable String turno) {
        return ResponseEntity.ok(equipeService.buscarPorTurno(turno));
    }

    /**
     * GET /api/equipes/ativas - Listar apenas equipes ativas
     */
    @GetMapping("/ativas")
    public ResponseEntity<List<EquipeResponseDTO>> buscarAtivas() {
        return ResponseEntity.ok(equipeService.buscarAtivas());
    }
}
