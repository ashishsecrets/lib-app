package com.ucsf.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ucsf.auth.model.Role;
import com.ucsf.auth.model.RoleName;
import com.ucsf.auth.model.User;
import com.ucsf.auth.model.User.UserStatus;
import com.ucsf.exception.AppException;
import com.ucsf.model.UserMetadata;
import com.ucsf.payload.UserDto;
import com.ucsf.repository.RoleRepository;
import com.ucsf.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Autowired
	RoleRepository roleRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		boolean isEnable = true;
		boolean isUserNotExpired = true;
		boolean isCredentialNotExpired = true;
		boolean isAccountNotLocked = true;
		User user = userRepository.findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}

		if (user.getUserStatus() != null && user.getUserStatus() == UserStatus.ACTIVE) {
			isEnable = true;
		} else {
			isEnable = false;
		}

		for (Role role : user != null && user.getRoles() != null ? user.getRoles() : new ArrayList<Role>()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(role.getName().toString()));
		}

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), isEnable,
				isUserNotExpired, isCredentialNotExpired, isAccountNotLocked, grantedAuthorities);
	}

	public User save(UserDto user) {
		User newUser = new User();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setEmail(user.getEmail());
		
		Optional<Role> existed = roleRepository.findByName(RoleName.ROLE_PATIENT);
		if (existed == null) {
			Role role = new Role();
			role.setName(RoleName.ROLE_PATIENT);
			roleRepository.save(role);
		}
		// Set initial role
		Role userRole = roleRepository.findByName(RoleName.ROLE_PATIENT)
				.orElseThrow(() -> new AppException("User Role not set."));
		newUser.setRoles(Collections.singleton(userRole));

		newUser.setUserStatus(UserStatus.ACTIVE);
		UserMetadata metadata = new UserMetadata();
		if (user.getUserMetadata() != null) {
			metadata.setAge(user.getUserMetadata().getAge());
			metadata.setRace(user.getUserMetadata().getRace());
			metadata.setZipCode(user.getUserMetadata().getZipCode());
			metadata.setPhone(user.getUserMetadata().getPhone());
			// metadata.setAcceptanceDate(new Date());
			metadata.setConsentAccepted(true);
			newUser.setMetadata(metadata);
		}
		return userRepository.save(newUser);
	}

	@PostConstruct
	public void saveRole() {
		Optional<Role> existed = roleRepository.findByName(RoleName.ROLE_ADMIN);
		if (existed == null) {
			Role role = new Role();
			role.setName(RoleName.ROLE_ADMIN);
			roleRepository.save(role);
		}
	}

}