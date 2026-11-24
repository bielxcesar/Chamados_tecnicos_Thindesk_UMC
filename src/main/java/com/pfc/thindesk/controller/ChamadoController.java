package com.pfc.thindesk.controller;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.dto.*;
import com.pfc.thindesk.service.ChamadoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;
@RestController @RequestMapping("/api/chamados")
public class ChamadoController {
 private final ChamadoService service;
 public ChamadoController(ChamadoService service) { this.service=service; }
 @PostMapping @ResponseStatus(HttpStatus.CREATED)
 public Chamado criar(@Valid @RequestBody AbrirChamadoDTO dto) { return service.criarChamado(dto); }
 @GetMapping public List<Chamado> listar() { return service.listarChamados(); }
 @GetMapping("/{id}") public Chamado buscar(@PathVariable String id) { return service.buscarPorId(id); }
 @GetMapping("/usuario/{id}") public List<Chamado> porUsuario(@PathVariable String id) { return service.listarPorUsuario(id); }
 @PutMapping("/{id}/resolver") public Chamado resolver(@PathVariable String id, @Valid @RequestBody ResolverChamadoDTO dto) {
  return service.resolverChamado(id,dto.solucao());
 }
}
