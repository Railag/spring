package com.firrael.spring.data.storage;

import java.util.logging.Logger;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.firrael.spring.security.Role;

@Service("userDetailsService")
public class UserStorage implements UserDetailsService {

	private static Logger logger = Logger.getLogger(UserStorage.class.getName());

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
//		String uid = getUidForLogin(login);
//		User user = get(uid, new UserFields());
////		
////		
//		boolean enabled = true;
//		boolean accountNonExpired = true;
//		boolean credentialsNonExpired = true;
//		boolean accountNonLocked = true;
//		org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User(
//				user.getEmail(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired,
//				accountNonLocked, user.getRole().getAuthorities());
//		
//		logger.info("logged: " + user.getLogin());
//		
//		return securityUser;
		return new org.springframework.security.core.userdetails.User("email", "pass", true, true, true, true, Role.USER.getAuthorities());
	}
}
