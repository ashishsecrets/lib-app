package com.ucsf.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.ucsf.config.JwtConfig;
import com.ucsf.model.UserMetadata;
import com.ucsf.payload.request.SignUpRequest;
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

	private static String ROLE_PREFIX = "ROLE_";

	@Value("${twilio.twoFa}")
	private Boolean twoFa;

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

		System.out.println(user.toString());
		if (user.getUserStatus() != null && user.getUserStatus() == UserStatus.ACTIVE) {
			isEnable = true;
		} else {
			isEnable = false;
		}
		if (user.getIsVerified()) {
			System.out.println("Check if user verified: "+user.getIsVerified());
			twoFa = false;
		}
		if (user.getIsVerified()) {
			for (Role role : user != null && user.getRoles() != null ? user.getRoles() : new ArrayList<Role>()) {
				grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getName().toString()));
			}
		} else {
			grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + RoleName.PRE_VERIFICATION_USER.toString()));
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), isEnable,
				isUserNotExpired, isCredentialNotExpired, isAccountNotLocked, grantedAuthorities);
	}

	public User save(SignUpRequest user) {
		User newUser = new User();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setEmail(user.getEmail());
		newUser.setPhoneNumber(user.getPhone());
		newUser.setPhoneCode(user.getPhoneCode());
		newUser.setIsVerified(false);
		Role existed = roleRepository.findByName(RoleName.PATIENT);
		if (existed == null) {
			Role role = new Role();
			role.setName(RoleName.PATIENT);
			roleRepository.save(role);
		}
		// Set initial role
		Role userRole = roleRepository.findByName(RoleName.PATIENT);
		// .orElseThrow(() -> new AppException("User Role not set."));
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
		Role existed = roleRepository.findByName(RoleName.ADMIN);
		if (existed == null) {
			Role role = new Role();
			role.setName(RoleName.ADMIN);
			roleRepository.save(role);
		}
	}

}