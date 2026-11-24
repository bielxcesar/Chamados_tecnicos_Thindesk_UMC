package com.pfc.thindesk.service;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.dto.*;
import com.pfc.thindesk.repository.ChamadoRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
@Service
public class ChamadoService {
 private final ChamadoRepository repository;
 public ChamadoService(ChamadoRepository repository) { this.repository=repository; }
 private String login() { return SecurityContextHolder.getContext().getAuthentication().getName(); }
 private boolean equipe() { return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
  .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO") || a.getAuthority().equals("ROLE_ADMIN")); }
 public Chamado criarChamado(AbrirChamadoDTO dto) {
  var c=new Chamado(); c.setTitulo(dto.titulo().strip()); c.setDescricao(dto.descricao().strip());
  c.setTipo(dto.tipo().strip()); c.setUsuario(login()); c.setStatus("ABERTO"); c.setDataAbertura(LocalDateTime.now());
  return repository.insert(c);
 }
 public Chamado buscarPorId(String id) {
  var c=repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Chamado não encontrado"));
  if (!equipe() && !login().equals(c.getUsuario())) throw new AccessDeniedException("Sem permissão para este chamado");
  return c;
 }
 public List<Chamado> listarPorUsuario(String usuario) {
  if (!equipe() && !login().equals(usuario)) throw new AccessDeniedException("Sem permissão para este usuário");
  return repository.findByUsuario(usuario);
 }
 public List<Chamado> listarChamados() { return equipe() ? repository.findAll() : repository.findByUsuario(login()); }
 @PreAuthorize("hasAnyRole('TECNICO','ADMIN')")
 public Chamado resolverChamado(String id, String solucao) {
  var c=buscarPorId(id);
  if ("RESOLVIDO".equals(c.getStatus())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Chamado já resolvido");
  if (c.getTecnico()!=null && !c.getTecnico().equals(login())) throw new AccessDeniedException("Chamado atribuído a outro técnico");
  c.setSolucao(solucao.strip()); c.setTecnico(login()); c.setStatus("RESOLVIDO"); c.setDataResolucao(LocalDateTime.now());
  return repository.save(c);
 }
}
