package com.ucsf.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import com.ucsf.auth.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByEmail(String email);
	Page<User> findAll(Pageable pageable);
    Boolean existsByEmail(String email);
    User findByIdOrderByIdDesc(Long userId);
}