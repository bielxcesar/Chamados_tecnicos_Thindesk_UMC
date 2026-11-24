package com.pfc.thindesk.dto;
import jakarta.validation.constraints.*;
public record ResolverChamadoDTO(@NotBlank @Size(max=5000) String solucao) {}
