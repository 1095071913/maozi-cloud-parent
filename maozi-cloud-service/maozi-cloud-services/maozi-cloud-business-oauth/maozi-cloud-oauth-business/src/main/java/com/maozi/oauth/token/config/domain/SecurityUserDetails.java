package com.maozi.oauth.token.config.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @author pengjinlong
 * @since 2026/8/13 21:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityUserDetails extends User implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;

    private Map<String, Object> attributes;

    public SecurityUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        super(username, password, authorities);
        this.attributes = attributes;
    }

    @JsonCreator
    public SecurityUserDetails(@JsonProperty("username") String username, @JsonProperty("password") String password,
                               @JsonProperty("enabled") boolean enabled, @JsonProperty("accountNonExpired") boolean accountNonExpired,
                               @JsonProperty("credentialsNonExpired") boolean credentialsNonExpired,
                               @JsonProperty("accountNonLocked") boolean accountNonLocked,
                               @JsonProperty("authorities") Collection<SimpleGrantedAuthority> authorities,
                               @JsonProperty("attributes") Map<String, Object> attributes) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.attributes = attributes;
    }

}
