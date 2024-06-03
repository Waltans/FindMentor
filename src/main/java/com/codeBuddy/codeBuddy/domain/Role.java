package com.codeBuddy.codeBuddy.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority
{
    MENTOR,
    STUDENT;

    @Override
    public String getAuthority() {
        return this.toString();
    }
}