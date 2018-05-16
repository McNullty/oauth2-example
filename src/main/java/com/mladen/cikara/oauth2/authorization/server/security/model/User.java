package com.mladen.cikara.oauth2.authorization.server.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = { "password" })
@Entity
@Table(name = "oauth2_user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @NotNull
  @Email
  @Size(min = 5, max = 100)
  @Column(length = 100, unique = true, nullable = false)
  private String email;

  @JsonIgnore
  @NotNull
  @Size(min = 68, max = 68)
  @Column(name = "password_hash")
  private String password;

  @Size(max = 50)
  @Column(name = "first_name")
  private String firstName;

  @Size(max = 50)
  @Column(name = "last_name")
  private String lastName;

  @JsonIgnore
  @ElementCollection(targetClass = Authority.class, fetch = FetchType.EAGER)
  @JoinTable(
      name = "user_authority",
      joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "authority", nullable = false)
  @Enumerated(EnumType.STRING)
  private final Set<Authority> authorities = new HashSet<>();

  /**
   * Adds all authorities in specified collection to this authorities collection
   *
   * @param authorities
   *          collection containing authorities to be added to this authorities
   *          collection
   */
  public void addAllAuthority(Collection<Authority> authorities) {
    this.authorities.addAll(authorities);
  }

  /**
   * Adds the specified authority to authorities collection if it is not already
   * present
   *
   * @param authority
   *          authority to be added to authorities collection
   */
  public void addAuthority(Authority authority) {
    authorities.add(authority);
  }

  /**
   * This method accepts password in clear text and hashes it before storing it in
   * password filed.
   *
   * @param password
   *          Clear text password
   */
  public void setClearTextPassword(String password) {
    this.password = PasswordEncoderFactories
        .createDelegatingPasswordEncoder()
        .encode(password);
  }

  /**
   * This method accepts hashed passwords. To set password with clear text use
   * method {@link #setClearTextPassword}
   *
   * @param password
   */
  public void setPassword(String password) {
    this.password = password;
  }
}
