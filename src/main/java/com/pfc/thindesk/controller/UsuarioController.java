package com.pfc.thindesk.controller;
import com.pfc.thindesk.entity.Usuario;
import com.pfc.thindesk.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
@RestController @RequestMapping("/api/usuarios")
public class UsuarioController {
 private final UsuarioRepository repository; private final PasswordEncoder encoder;
 public UsuarioController(UsuarioRepository repository, PasswordEncoder encoder) { this.repository=repository; this.encoder=encoder; }
 public record Cadastro(@NotBlank @Pattern(regexp="[a-zA-Z0-9._-]{3,50}") String login,
  @NotBlank @Size(min=12,max=72) String senha, @NotNull Usuario.Perfil perfil) {}
 @PostMapping @ResponseStatus(HttpStatus.CREATED)
 public Usuario criar(@Valid @RequestBody Cadastro dto) {
  if (dto.senha().getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72)
   throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Senha excede 72 bytes em UTF-8");
  var u=new Usuario(); u.setLogin(dto.login()); u.setSenha(encoder.encode(dto.senha())); u.setPerfil(dto.perfil()); return repository.insert(u);
 }
}
