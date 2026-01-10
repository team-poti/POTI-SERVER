package org.sopt.poti.global.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.sopt.poti.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserPrincipal implements UserDetails, OAuth2User {

  private final Long userId;
  private final String email;
  private final Collection<? extends GrantedAuthority> authorities;
  private Map<String, Object> attributes; // OAuth2User를 위한 필드

  // UserDetails를 구현하는 생성자 (JWT 인증용)
  public static UserPrincipal create(User user) {
    List<GrantedAuthority> authorities = Collections.singletonList(
        new SimpleGrantedAuthority(user.getRole().getKey())); // Role enum의 key 사용
    return UserPrincipal.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .authorities(authorities)
        .build();
  }

  // OAuth2User를 구현하는 생성자 (소셜 로그인용, 필요시 확장 가능)
  public static UserPrincipal create(User user, Map<String, Object> attributes) {
    UserPrincipal userPrincipal = UserPrincipal.create(user);
    userPrincipal.setAttributes(attributes);
    return userPrincipal;
  }

  private void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getName() {
    // OAuth2User에서 사용되는 이름
    return String.valueOf(userId);
  }

  // UserDetails 필수 구현 메서드 (JWT 인증 시 사용)
  @Override
  public String getPassword() {
    return null; // JWT 방식에서는 패스워드를 사용하지 않음
  }

  @Override
  public String getUsername() {
    return String.valueOf(userId); // 사용자 식별자로 userId 사용
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
