package com.mladen.cikara.oauth2.authorization.server.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
public class User implements Serializable {

  public static class Builder {
    private Long id;
    private String email;
    private String password;
    private String encryptedPassword;
    private String firstName;
    private String lastName;
    private final Set<Authority> authorities = new HashSet<>();
    private UUID uuid;

    public Builder authorities(final Authority... authorities) {
      this.authorities.addAll(Arrays.asList(authorities));
      return this;
    }

    public User build() {
      return new User(this);
    }

    public Builder email(final String email) {
      this.email = email;
      return this;
    }

    public Builder encryptedPassword(final String encryptedPassword) {
      this.encryptedPassword = encryptedPassword;
      return this;
    }

    public Builder firstName(final String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder id(final Long id) {
      this.id = id;
      return this;
    }

    public Builder lastName(final String lastName) {
      this.lastName = lastName;
      return this;
    }

    /**
     * Sets clear password. Password will be hashed by build method.
     *
     * @param password
     *          Clear text password
     * @return
     */
    public Builder password(final String password) {
      this.password = password;
      return this;
    }

    public Builder uuid(final UUID uuid) {
      this.uuid = uuid;
      return this;
    }
  }

  private static final long serialVersionUID = -7596199454429470917L;

  private static final Logger log = LoggerFactory.getLogger(User.class);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @NotNull
  @org.hibernate.annotations.Type(type = "pg-uuid") // this annotation is Hibernate specific but I
  // didn't find any better solution
  @Column(name = "uuid")
  private UUID uuid;

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
  private Set<Authority> authorities;

  @SuppressWarnings("unused") // this method is used by JPA
  private User() {
    super();
  }

  /**
   * Creates new User object using builder object.
   *
   * @param builder
   *          Builder object configured with new User
   */
  public User(final Builder builder) {
    this.id = builder.id;
    this.email = builder.email;
    this.firstName = builder.firstName;
    this.lastName = builder.lastName;
    this.authorities = builder.authorities;

    if (builder.uuid == null) {
      this.uuid = UUID.randomUUID();
    } else {
      this.uuid = builder.uuid;
    }

    checkPasswords(builder.password, builder.encryptedPassword);

    if (builder.password != null) {
      setClearTextPassword(builder.password);
    } else {
      this.password = builder.encryptedPassword;
    }
  }

  /**
   * Adds all authorities in specified collection to this authorities collection.
   *
   * @param authorities
   *          collection containing authorities to be added to this authorities collection
   */
  public void addAllAuthority(final Authority... authorities) {
    this.authorities.addAll(Arrays.asList(authorities));
  }

  /**
   * Adds the specified authority to authorities collection if it is not already present.
   *
   * @param authority
   *          authority to be added to authorities collection
   */
  public void addAuthority(final Authority authority) {
    this.authorities.add(authority);
  }

  private void checkPasswords(final String password, final String encryptedPassword) {
    if ((password == null || password.isEmpty())
        && (encryptedPassword == null || encryptedPassword.isEmpty())) {
      throw new PasswordMustBeSetException("Passwords not set");
    }

    if (password != null && !password.isEmpty()
        && encryptedPassword != null && !encryptedPassword.isEmpty()) {
      throw new DuplicatePasswordsSetException("Password and Encrypted password set");
    }
  }

  // Generated by eclipse if class is changed remove and recreate with eclipse
  @Override
  public boolean equals(final Object obj) {
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
    if (this.email == null) {
      if (other.email != null) {
        return false;
      }
    } else if (!this.email.equals(other.email)) {
      return false;
    }
    return true;
  }

  public Set<Authority> getAuthorities() {
    return this.authorities;
  }

  public String getEmail() {
    return this.email;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public Long getId() {
    return this.id;
  }

  public String getLastName() {
    return this.lastName;
  }

  public String getPassword() {
    return this.password;
  }

  public UUID getUuid() {
    return this.uuid;
  }

  // Generated by eclipse if class is changed remove and recreate with eclipse
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.email == null ? 0 : this.email.hashCode());
    return result;
  }

  /**
   * Removes all authorities in specified collection to this authorities collection.
   *
   * @param authorities
   *          collection containing authorities to be added to this authorities collection
   */
  public void removeAllAuthority(final Authority... authorities) {
    this.authorities.removeAll(Arrays.asList(authorities));
  }

  /**
   * Removes the specified authority from authorities collection.
   *
   * @param authority
   *          authority to be added to authorities collection
   */
  public void removeAuthority(final Authority authority) {
    this.authorities.remove(authority);
  }

  /**
   * This method accepts password in clear text and hashes it before storing it in password filed.
   *
   * @param password
   *          Clear text password
   */
  private void setClearTextPassword(final String password) {
    log.debug("Encoding clear password");

    this.password = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(password);
  }

  @SuppressWarnings("unused") // this method is used by JPA
  private void setEmail(final String email) {
    this.email = email;
  }

  @SuppressWarnings("unused") // this method is used by JPA
  private void setFirstName(final String firstName) {
    this.firstName = firstName;
  }

  @SuppressWarnings("unused") // this method is used by JPA
  private void setId(final Long id) {
    this.id = id;
  }

  @SuppressWarnings("unused") // this method is used by JPA
  private void setLastName(final String lastName) {
    this.lastName = lastName;
  }

  @SuppressWarnings("unused") // this method is used by JPA
  /**
   * This method accepts hashed passwords. To set password with clear text use method
   * {@link #setClearTextPassword}
   *
   * @param password
   */
  private void setPassword(final String password) {
    this.password = password;
  }

  @SuppressWarnings("unused") // this method is used by JPA
  private void setUuid(final UUID uuid) {
    this.uuid = uuid;
  }

  @Override
  public String toString() {
    return "User [id=" + this.id + ", uuid=" + this.uuid + ", email=" + this.email + ", firstName="
        + this.firstName
        + ", lastName=" + this.lastName + ", authorities=" + this.authorities + "]";
  }
}
