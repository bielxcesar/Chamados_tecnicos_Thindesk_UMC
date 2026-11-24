package com.pfc.thindesk.controller;
import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.service.HorarioAtendimentoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Controller @RequestMapping("/ajustes-horarios")
public class HorarioPageController {
 private final HorarioAtendimentoService service;
 public HorarioPageController(HorarioAtendimentoService service) { this.service=service; }
 @PostMapping("/salvar") public String salvar(@Valid @ModelAttribute HorarioAtendimento horario) { service.salvar(horario); return "redirect:/ajustes-horarios"; }
 @PostMapping("/deletar/{id}") public String deletar(@PathVariable String id) { service.deletar(id); return "redirect:/ajustes-horarios"; }
}
