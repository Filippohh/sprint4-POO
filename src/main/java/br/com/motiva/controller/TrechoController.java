package br.com.motiva.controller;

import br.com.motiva.dto.TrechoRequestDTO;
import br.com.motiva.dto.TrechoResponseDTO;
import br.com.motiva.service.TrechoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller REST para o gerenciamento de Trechos de Rodovia.
 * Rotas base: /api/trechos
 */
@RestController
@RequestMapping("/api/trechos")
public class TrechoController {

    private final TrechoService trechoService;

    public TrechoController(TrechoService trechoService) {
        this.trechoService = trechoService;
    }

    /**
     * GET /api/trechos - Listar todos os trechos
     */
    @GetMapping
    public ResponseEntity<List<TrechoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(trechoService.listarTodos());
    }

    /**
     * GET /api/trechos/{id} - Buscar trecho por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TrechoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(trechoService.buscarPorId(id));
    }

    /**
     * POST /api/trechos - Criar novo trecho (201 Created)
     */
    @PostMapping
    public ResponseEntity<TrechoResponseDTO> criar(@Valid @RequestBody TrechoRequestDTO dto) {
        TrechoResponseDTO criado = trechoService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    /**
     * PUT /api/trechos/{id} - Atualizar trecho existente (200 OK)
     */
    @PutMapping("/{id}")
    public ResponseEntity<TrechoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody TrechoRequestDTO dto) {
        return ResponseEntity.ok(trechoService.atualizar(id, dto));
    }

    /**
     * DELETE /api/trechos/{id} - Remover trecho (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        trechoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/trechos/tipo/{tipo} - Filtrar trechos por tipo (UMIDO ou SECO) via derived query
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<TrechoResponseDTO>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(trechoService.buscarPorTipo(tipo));
    }

    /**
     * GET /api/trechos/vegetacao-minima?minimo=... - Filtrar trechos com vegetacao >= minimo
     */
    @GetMapping("/vegetacao-minima")
    public ResponseEntity<List<TrechoResponseDTO>> buscarPorVegetacaoMinima(@RequestParam Double minimo) {
        return ResponseEntity.ok(trechoService.buscarPorVegetacaoMinima(minimo));
    }
}
