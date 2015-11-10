package com.firrael.spring.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
	USER, ADMIN;
	
    private List<String> getRoles() {
        List<String> roles = new ArrayList<String>();
        switch(this) {
        case ADMIN:
            roles.add("ROLE_USER");
            roles.add("ROLE_ADMIN");
            break;
        case USER:
            roles.add("ROLE_USER");
            break;
        }
        
        return roles;
    }
    
   public Collection<? extends GrantedAuthority> getAuthorities(){
        List<GrantedAuthority> authList = getGrantedAuthorities(getRoles());
        return authList;
    }

    private static List<GrantedAuthority> getGrantedAuthorities (List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

}
