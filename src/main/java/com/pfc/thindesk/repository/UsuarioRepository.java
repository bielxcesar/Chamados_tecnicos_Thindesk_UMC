package com.pfc.thindesk.repository;
import com.pfc.thindesk.entity.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
public interface UsuarioRepository extends MongoRepository<Usuario,String> {
 Optional<Usuario> findByLogin(String login);
}
