package com.mladen.cikara.oauth2.authorization.server.security.repository;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
  Optional<User> findByEmail(String email);

  Optional<User> findByUuid(UUID uuid);
}
