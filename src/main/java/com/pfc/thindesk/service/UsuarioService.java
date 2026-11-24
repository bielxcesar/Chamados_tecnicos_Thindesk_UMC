package com.pfc.thindesk.service;
import com.pfc.thindesk.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.*;
@Service
public class UsuarioService implements UserDetailsService {
 private final UsuarioRepository repository;
 public UsuarioService(UsuarioRepository repository) { this.repository=repository; }
 public UserDetails loadUserByUsername(String login) {
  var u=repository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas"));
  return User.withUsername(u.getLogin()).password(u.getSenha()).roles(u.getPerfil().name()).build();
 }
}
