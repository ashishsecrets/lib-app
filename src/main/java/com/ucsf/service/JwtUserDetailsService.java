package com.ucsf.service;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ucsf.auth.model.User;
import com.ucsf.auth.model.User.UserStatus;
import com.ucsf.model.UserMetadata;
import com.ucsf.payload.UserDto;
import com.ucsf.repository.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userDao;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	private static String ROLE_PREFIX = "ROLE_";

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		boolean isEnable = true;
		boolean isUserNotExpired = true;
		boolean isCredetialNotExpired = true;
		boolean isAcoountNotLocked = true;
		User user = userDao.findByUsername(username);

		if (user == null) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}

		if (user.getUserStatus() != null && user.getUserStatus() == UserStatus.ACTIVE) {
			isEnable = true;
		} else {
			isEnable = false;
		}

		if (user.getRole() != null) {
			grantedAuthorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + user.getRole()));
		}

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), isEnable,
				isUserNotExpired, isCredetialNotExpired, isAcoountNotLocked, grantedAuthorities);
	}

	public User save(UserDto user) {
		User newUser = new User();
		newUser.setUsername(user.getUsername());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		newUser.setEmail(user.getEmail());
		newUser.setRole("ADMIN");
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
		return userDao.save(newUser);
	}

}