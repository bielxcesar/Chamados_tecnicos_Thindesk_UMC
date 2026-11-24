package com.pfc;
import com.pfc.thindesk.controller.ChamadoController;
import com.pfc.thindesk.service.ChamadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
@WebMvcTest(ChamadoController.class) @Import({SecurityConfig.class,com.pfc.thindesk.service.UsuarioService.class})
class SecurityTests {
 @Autowired MockMvc mvc;
 @MockitoBean ChamadoService service;
 @MockitoBean com.pfc.thindesk.repository.UsuarioRepository usuarios;
 @Autowired org.springframework.security.crypto.password.PasswordEncoder encoder;
 @Test void loginAutenticaContaDoMongo() throws Exception {
  var u=new com.pfc.thindesk.entity.Usuario(); u.setLogin("ana"); u.setSenha(encoder.encode("SenhaTesteForte123")); u.setPerfil(com.pfc.thindesk.entity.Usuario.Perfil.USUARIO);
  when(usuarios.findByLogin("ana")).thenReturn(java.util.Optional.of(u));
  mvc.perform(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin().user("ana").password("SenhaTesteForte123"))
   .andExpect(org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated().withUsername("ana"));
 }
 @Test void senhaErradaNaoAutentica() throws Exception {
  var u=new com.pfc.thindesk.entity.Usuario(); u.setLogin("ana"); u.setSenha(encoder.encode("SenhaTesteForte123")); u.setPerfil(com.pfc.thindesk.entity.Usuario.Perfil.USUARIO);
  when(usuarios.findByLogin("ana")).thenReturn(java.util.Optional.of(u));
  mvc.perform(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin().user("ana").password("incorreta"))
   .andExpect(org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated());
 }

 @Test void anonimoNaoAcessaApi() throws Exception { mvc.perform(get("/api/chamados")).andExpect(status().isUnauthorized()); }
 @Test @WithMockUser void exigeCsrf() throws Exception { mvc.perform(post("/api/chamados").contentType("application/json").content("{}")) .andExpect(status().isForbidden()); verifyNoInteractions(service); }
 @Test @WithMockUser void validaCampos() throws Exception { mvc.perform(post("/api/chamados").with(csrf()).contentType("application/json").content("{}")) .andExpect(status().isBadRequest()); verifyNoInteractions(service); }
 @Test @WithMockUser void usuarioNaoGerenciaContas() throws Exception { mvc.perform(post("/api/usuarios").with(csrf()).contentType("application/json").content("{}")) .andExpect(status().isForbidden()); }
}
