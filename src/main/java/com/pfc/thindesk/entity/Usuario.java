package com.pfc.thindesk.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Data @Document("usuarios")
public class Usuario {
 @Id private String id;
 @Indexed(unique=true) private String login;
 @JsonIgnore private String senha;
 private Perfil perfil;
 public enum Perfil { USUARIO, TECNICO, ADMIN }
}
