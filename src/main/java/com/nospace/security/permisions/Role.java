package com.nospace.security.permisions;

import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.nospace.security.permisions.Permission.*;

public enum Role {
    USER(Sets.newHashSet(FILE_DELETE, FILE_UPLOAD, FILE_REPORT) ),
    ADMIM(Sets.newHashSet(FILE_UPLOAD, FILE_REPORT));

    private Set<Permission> permissions;

    private Role(Set<Permission> permissions){
        this.permissions = permissions;
    }

    public Set<GrantedAuthority> getAuthorities(){
        Set<GrantedAuthority> roleAuthorities = this.permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toSet());

        roleAuthorities.add( new SimpleGrantedAuthority("ROLE_" + this.name()));
        return roleAuthorities;
    }

}
