package br.com.motiva.controller;

import br.com.motiva.dto.RelatorioResponseDTO;
import br.com.motiva.service.RelatorioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller REST para Geracao e Consulta de Relatorios de Prioridade de Manutencao.
 * Rotas base: /api/relatorios
 */
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    /**
     * POST /api/relatorios - Gera o relatorio a partir dos trechos atuais e persiste no historico (201 Created)
     */
    @PostMapping
    public ResponseEntity<RelatorioResponseDTO> gerarRelatorio() {
        RelatorioResponseDTO gerado = relatorioService.gerarRelatorioAtual();
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(gerado.id())
                .toUri();
        return ResponseEntity.created(location).body(gerado);
    }

    /**
     * GET /api/relatorios - Lista todo o historico de relatorios (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<RelatorioResponseDTO>> listarHistorico() {
        return ResponseEntity.ok(relatorioService.listarHistorico());
    }

    /**
     * GET /api/relatorios/{id} - Busca detalhes de um relatorio especifico por ID (200 OK)
     */
    @GetMapping("/{id}")
    public ResponseEntity<RelatorioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(relatorioService.buscarPorId(id));
    }

    /**
     * GET /api/relatorios/periodo?inicio=...&fim=... - Consulta historicos por periodo (200 OK)
     * Aceita tanto formato de data completo (YYYY-MM-DDTHH:mm:ss) quanto apenas data (YYYY-MM-DD).
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<RelatorioResponseDTO>> buscarPorPeriodo(
            @RequestParam("inicio") String inicioStr,
            @RequestParam("fim") String fimStr) {

        LocalDateTime inicio = converterDataHora(inicioStr, true);
        LocalDateTime fim = converterDataHora(fimStr, false);

        return ResponseEntity.ok(relatorioService.buscarPorPeriodo(inicio, fim));
    }

    private LocalDateTime converterDataHora(String valor, boolean ehInicio) {
        try {
            if (valor.contains("T")) {
                return LocalDateTime.parse(valor);
            }
            LocalDate data = LocalDate.parse(valor);
            return ehInicio ? data.atStartOfDay() : data.atTime(LocalTime.MAX);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Formato de data invalido: '" + valor + "'. Use YYYY-MM-DD ou YYYY-MM-DDTHH:mm:ss");
        }
    }
}
