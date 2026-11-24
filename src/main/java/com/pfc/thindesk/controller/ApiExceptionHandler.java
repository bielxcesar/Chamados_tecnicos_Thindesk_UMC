package com.pfc.thindesk.controller;
import java.util.Map;
import java.util.LinkedHashMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.*;
import org.springframework.dao.*;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
 public ResponseEntity<?> forbidden(Exception ex) { return ResponseEntity.status(403).body(Map.of("mensagem","Sem permissão para esta operação")); }
 @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
 public ResponseEntity<?> status(org.springframework.web.server.ResponseStatusException ex) { return ResponseEntity.status(ex.getStatusCode()).body(Map.of("mensagem",ex.getReason()==null ? "Operação inválida" : ex.getReason())); }
 @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
 public ResponseEntity<?> json(Exception ex) { return ResponseEntity.badRequest().body(Map.of("mensagem","JSON inválido ou valores incompatíveis")); }
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<?> validation(MethodArgumentNotValidException ex) {
  Map<String,String> campos=new LinkedHashMap<>();
  ex.getBindingResult().getFieldErrors().forEach(e -> campos.put(e.getField(),e.getDefaultMessage()));
  return ResponseEntity.badRequest().body(Map.of("mensagem","Dados inválidos","campos",campos));
 }
 @ExceptionHandler({DuplicateKeyException.class,OptimisticLockingFailureException.class})
 public ResponseEntity<?> conflict(Exception ex) { return ResponseEntity.status(409).body(Map.of("mensagem","Registro duplicado ou alterado por outra requisição")); }
}
