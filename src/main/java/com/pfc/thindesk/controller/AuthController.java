package com.pfc.thindesk.controller;
import java.security.Principal;
import java.util.Map;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 @GetMapping("/csrf") public Map<String,String> csrf(CsrfToken token) {
  return Map.of("token",token.getToken(),"headerName",token.getHeaderName(),"parameterName",token.getParameterName());
 }
 @GetMapping("/me") public Map<String,String> me(Principal principal) { return Map.of("login",principal.getName()); }
}
