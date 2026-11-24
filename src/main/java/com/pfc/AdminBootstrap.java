package com.pfc;
import com.pfc.thindesk.entity.Usuario;
import com.pfc.thindesk.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
@Component
public class AdminBootstrap implements CommandLineRunner {
 private final UsuarioRepository repository; private final PasswordEncoder encoder;
 @Value("${app.admin.login:admin}") private String login;
 @Value("${app.admin.password:}") private String password;
 public AdminBootstrap(UsuarioRepository repository, PasswordEncoder encoder) { this.repository=repository; this.encoder=encoder; }
 public void run(String... args) {
  if (!password.isBlank() && repository.findByLogin(login).isEmpty()) {
   if (password.length()<12 || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72) throw new IllegalStateException("ADMIN_PASSWORD deve ter pelo menos 12 caracteres e no máximo 72 bytes UTF-8");
   var u=new Usuario(); u.setLogin(login); u.setSenha(encoder.encode(password)); u.setPerfil(Usuario.Perfil.ADMIN); repository.insert(u);
  }
 }
}
