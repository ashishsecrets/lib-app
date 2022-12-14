package com.ucsf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ucsf.auth.model.Role;
import com.ucsf.auth.model.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(RoleName roleName);
}