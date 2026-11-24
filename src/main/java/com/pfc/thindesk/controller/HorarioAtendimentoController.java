package com.pfc.thindesk.controller;
import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.service.HorarioAtendimentoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/ajustes-horarios")
public class HorarioAtendimentoController {
 private final HorarioAtendimentoService service;
 public HorarioAtendimentoController(HorarioAtendimentoService service) { this.service=service; }
 @GetMapping public List<HorarioAtendimento> listar() { return service.listarTodos(); }
 @PostMapping public HorarioAtendimento salvar(@Valid @RequestBody HorarioAtendimento dto) { return service.salvar(dto); }
 @DeleteMapping("/{id}") public void deletar(@PathVariable String id) { service.deletar(id); }
}
