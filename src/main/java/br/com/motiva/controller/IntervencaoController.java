package br.com.motiva.controller;

import br.com.motiva.dto.IntervencaoRequestDTO;
import br.com.motiva.dto.IntervencaoResponseDTO;
import br.com.motiva.service.IntervencaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller REST para o gerenciamento de Intervencoes Operacionais.
 * Rotas base: /api/intervencoes
 */
@RestController
@RequestMapping("/api/intervencoes")
public class IntervencaoController {

    private final IntervencaoService intervencaoService;

    public IntervencaoController(IntervencaoService intervencaoService) {
        this.intervencaoService = intervencaoService;
    }

    /**
     * GET /api/intervencoes - Listar todas as intervencoes
     */
    @GetMapping
    public ResponseEntity<List<IntervencaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(intervencaoService.listarTodas());
    }

    /**
     * GET /api/intervencoes/{id} - Buscar intervencao por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<IntervencaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(intervencaoService.buscarPorId(id));
    }

    /**
     * POST /api/intervencoes - Registrar nova intervencao (201 Created)
     */
    @PostMapping
    public ResponseEntity<IntervencaoResponseDTO> criar(@Valid @RequestBody IntervencaoRequestDTO dto) {
        IntervencaoResponseDTO criada = intervencaoService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.id())
                .toUri();
        return ResponseEntity.created(location).body(criada);
    }

    /**
     * PUT /api/intervencoes/{id} - Atualizar dados da intervencao (200 OK)
     */
    @PutMapping("/{id}")
    public ResponseEntity<IntervencaoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody IntervencaoRequestDTO dto) {
        return ResponseEntity.ok(intervencaoService.atualizar(id, dto));
    }

    /**
     * DELETE /api/intervencoes/{id} - Remover intervencao (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        intervencaoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/intervencoes/tipo/{tipo} - Filtrar intervencoes por tipo (ROCADA_MECANIZADA ou PULVERIZACAO)
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<IntervencaoResponseDTO>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(intervencaoService.buscarPorTipo(tipo));
    }

    /**
     * GET /api/intervencoes/trecho/{trechoId} - Historico de intervencoes de um trecho especifico
     */
    @GetMapping("/trecho/{trechoId}")
    public ResponseEntity<List<IntervencaoResponseDTO>> buscarPorTrecho(@PathVariable Long trechoId) {
        return ResponseEntity.ok(intervencaoService.buscarPorTrecho(trechoId));
    }
}
