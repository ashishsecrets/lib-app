package com.ucsf.repository;

import org.springframework.data.repository.CrudRepository;
import com.ucsf.auth.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);
	User findByEmail(String email);
	Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}