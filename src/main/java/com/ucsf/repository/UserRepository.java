package com.ucsf.repository;

import org.springframework.data.repository.CrudRepository;
import com.ucsf.auth.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
	User findByUsername(String username);
}