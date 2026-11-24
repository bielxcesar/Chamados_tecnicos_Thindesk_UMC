package com.pfc.thindesk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.service.ClienteService;
import com.pfc.thindesk.service.ChamadoService;
import com.pfc.thindesk.service.HorarioAtendimentoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private ChamadoService chamadoService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private HorarioAtendimentoService horarioAtendimentoService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView("layout");
        String fragment = "home :: content";
        log.info("Carregando fragmento: {}", fragment); // Log para depuração
        modelAndView.addObject("content", fragment);
        return modelAndView;
    }

    @GetMapping("/chamados")
    public String chamados(Model model) {
        List<Chamado> chamados = chamadoService.listarChamados();
        model.addAttribute("chamados", chamados);
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("login", auth.getName());
        model.addAttribute("podeResolver", auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO") || a.getAuthority().equals("ROLE_ADMIN"))); // Passa a lista para o modelo
        String fragment = "chamados :: content";
        log.info("Carregando fragmento: {}", fragment); // Log para depuração
        model.addAttribute("content", fragment);
        return "chamados";
    }

    @GetMapping("/clientes")
    public String clientes(Model model) {
        List<Cliente> clientes = clienteService.listarClientes();
        model.addAttribute("clientes", clientes);
        String fragment = "clientes :: content";
        log.info("Carregando fragmento: {}", fragment); // Log para depuração
        model.addAttribute("content", fragment);
        return "clientes";
    }
    
    @GetMapping("/ajustes-horarios")
    public String horarios(Model model) {
        List<HorarioAtendimento> horarios = horarioAtendimentoService.listarTodos();
        model.addAttribute("horarios", horarios);
        model.addAttribute("novoHorario", new HorarioAtendimento());
        String fragment = "ajustes-horarios :: content";
        log.info("Carregando fragmento: {}", fragment); // Log para depuração
        model.addAttribute("content", fragment);
        return "ajustes-horarios";
    }

}
