package com.pfc.thindesk.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
@ControllerAdvice
public class PageAttributes {
 @ModelAttribute("admin") public boolean admin(Authentication auth) {
  return auth!=null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
 }
}
