package com.mladen.cikara.oauth2.authorization.server.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

@Entity
@Table(name = "oauth2_user")
public class User {

  public static class Builder {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private final Set<Authority> authorities = new HashSet<>();

    public Builder authorities(Authority... authorities) {
      this.authorities.addAll(Arrays.asList(authorities));
      return this;
    }

    public User build() {
      return new User(this);
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    /**
     *
     * @param password
     *          Clear text password
     * @return
     */
    public Builder password(String password) {
      this.password = password;
      return this;
    }
  }

  private static final Logger log = LoggerFactory.getLogger(User.class);

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
  @JoinTable(name = "user_authority", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "authority", nullable = false)
  @Enumerated(EnumType.STRING)
  private final Set<Authority> authorities = new HashSet<>();

  // this constructor should only be used by JPA
  User() {
    super();
  }

  public User(Builder builder) {
    id = builder.id;
    email = builder.email;
    firstName = builder.firstName;
    lastName = builder.lastName;

    setClearTextPassword(builder.password);
  }

  /**
   * Adds all authorities in specified collection to this authorities collection
   *
   * @param authorities
   *          collection containing authorities to be added to this authorities
   *          collection
   */
  public void addAllAuthority(Authority... authorities) {
    this.authorities.addAll(Arrays.asList(authorities));
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

  // Generated by eclipse if class is changed remove and recreate with eclipse
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final User other = (User) obj;
    if (email == null) {
      if (other.email != null) {
        return false;
      }
    } else if (!email.equals(other.email)) {
      return false;
    }
    return true;
  }

  public Set<Authority> getAuthorities() {
    return authorities;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public Long getId() {
    return id;
  }

  public String getLastName() {
    return lastName;
  }

  public String getPassword() {
    return password;
  }

  // Generated by eclipse if class is changed remove and recreate with eclipse
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    return result;
  }

  /**
   * This method accepts password in clear text and hashes it before storing it in
   * password filed.
   *
   * @param password
   *          Clear text password
   */
  private void setClearTextPassword(String password) {
    log.debug("Encoding clear password");

    this.password = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password);
  }

  @SuppressWarnings("unused") // this method is used by JPA
  private void setEmail(String email) {
    this.email = email;
  }

  @SuppressWarnings("unused") // this method is used by JPA
  private void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @SuppressWarnings("unused") // this method is used by JPA
  private void setId(Long id) {
    this.id = id;
  }

  @SuppressWarnings("unused") // this method is used by JPA
  private void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @SuppressWarnings("unused") // this method is used by JPA
  /**
   * This method accepts hashed passwords. To set password with clear text use
   * method {@link #setClearTextPassword}
   *
   * @param password
   */
  private void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "User [id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName="
        + lastName + ", authorities=" + authorities + "]";
  }
}
