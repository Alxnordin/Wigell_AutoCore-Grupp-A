package com.wac.autocore.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController {

    //Alexander
    //säkerhetshändelser loggas med en egen logger som skriver till filen security.log
    private static final Logger SECURITY = LoggerFactory.getLogger("SECURITY");

    private final AuthenticationManager authenticationManager;

    public LoginController(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    public boolean login(String username, String password){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //Alexander
            //användaren läggs i MDC (en typ av minneslapp) så att den står på alla loggrader tills utloggning
            MDC.put("user", authentication.getName());
            SECURITY.info("Inloggning lyckades, roller {}", authentication.getAuthorities());
            return true;
        } catch(org.springframework.security.core.AuthenticationException e){
            SecurityContextHolder.clearContext();
            //lösenordet loggas aldrig, bara användarnamnet som angavs
            SECURITY.warn("Inloggning misslyckades för användare {}", username);
            return false;
        }
    }
    public void logout() {
        SECURITY.info("Utloggning");
        MDC.remove("user");
        SecurityContextHolder.clearContext();
    }
}
