package com.pfc.thindesk.dto;
import jakarta.validation.constraints.*;
public record AbrirChamadoDTO(@NotBlank @Size(max=150) String titulo,
 @NotBlank @Size(max=5000) String descricao, @NotBlank @Size(max=80) String tipo) {}
