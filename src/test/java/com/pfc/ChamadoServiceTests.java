package com.pfc;
import com.pfc.thindesk.dto.AbrirChamadoDTO;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.repository.ChamadoRepository;
import com.pfc.thindesk.service.ChamadoService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringJUnitConfig(ChamadoServiceTests.Config.class)
class ChamadoServiceTests {
 @Configuration @EnableMethodSecurity static class Config {
  @Bean ChamadoRepository repository() { return mock(ChamadoRepository.class); }
  @Bean ChamadoService service(ChamadoRepository r) { return new ChamadoService(r); }
 }
 @Autowired ChamadoService service; @Autowired ChamadoRepository repository;
 @BeforeEach void resetMock() { reset(repository); }
 @Test @WithMockUser(username="ana") void impedeConsultaAlheia() {
  var c=new Chamado(); c.setUsuario("joao"); when(repository.findById("1")).thenReturn(Optional.of(c));
  assertThrows(AccessDeniedException.class,() -> service.buscarPorId("1"));
 }
 @Test @WithMockUser void usuarioNaoResolve() { assertThrows(AccessDeniedException.class,() -> service.resolverChamado("1","Feito")); verifyNoInteractions(repository); }
 @Test @WithMockUser(username="tec",roles="TECNICO") void tecnicoResolveComSuaIdentidade() {
  var c=new Chamado(); c.setStatus("ABERTO"); when(repository.findById("1")).thenReturn(Optional.of(c)); when(repository.save(c)).thenReturn(c);
  var r=service.resolverChamado("1","Correção"); assertEquals("tec",r.getTecnico()); assertEquals("RESOLVIDO",r.getStatus()); assertNotNull(r.getDataResolucao());
 }
 @Test @WithMockUser(roles="TECNICO") void naoReabreResolvido() {
  var c=new Chamado(); c.setStatus("RESOLVIDO"); when(repository.findById("1")).thenReturn(Optional.of(c));
  assertThrows(ResponseStatusException.class,() -> service.resolverChamado("1","Outra")); verify(repository,never()).save(any());
 }
 @Test @WithMockUser(username="tec2",roles="TECNICO") void respeitaTecnicoResponsavel() {
  var c=new Chamado(); c.setStatus("ABERTO"); c.setTecnico("tec1"); when(repository.findById("1")).thenReturn(Optional.of(c));
  assertThrows(AccessDeniedException.class,() -> service.resolverChamado("1","Outra")); verify(repository,never()).save(any());
 }
 @Test @WithMockUser(username="ana") void aberturaUsaUsuarioLogado() {
  when(repository.insert(any(Chamado.class))).thenAnswer(i -> i.getArgument(0));
  var c=service.criarChamado(new AbrirChamadoDTO("Tela", "Sem imagem", "Hardware"));
  assertEquals("ana",c.getUsuario()); assertEquals("ABERTO",c.getStatus()); assertNull(c.getTecnico());
 }
}
