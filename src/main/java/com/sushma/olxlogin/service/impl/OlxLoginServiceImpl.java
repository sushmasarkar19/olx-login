package com.sushma.olxlogin.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sushma.olxlogin.Repository.OlxLoginRepository;
import com.sushma.olxlogin.dto.AuthRequestDto;
import com.sushma.olxlogin.dto.AuthResponseDto;
import com.sushma.olxlogin.dto.UserDto;
import com.sushma.olxlogin.entity.UserEntity;
import com.sushma.olxlogin.exception.handler.InvalidTokenException;
import com.sushma.olxlogin.exception.handler.UsernameNotFoundException;
import com.sushma.olxlogin.service.OlxLoginService;
import com.sushma.olxlogin.utility.JwtUtil;
import com.sushma.olxlogin.utility.TokenBlacklist;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OlxLoginServiceImpl implements OlxLoginService {

	@Autowired
	public OlxLoginRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtTokenUtil;
	@Autowired
	private TokenBlacklist tokenBlacklist;

	@Autowired
	private AuthenticationManager authenticationManager;

	private Map<String, String> tokenStore = new HashMap();

	@Override
	public AuthResponseDto authenticateUser(AuthRequestDto userDto) {
		try {

			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(userDto.getUserName(), userDto.getPassword()));

			// authentication.getName() is the username confirmed by Spring Security
			return new AuthResponseDto(jwtTokenUtil.generateToken(authentication.getName()));
		} catch (Exception e) {
			throw new InvalidTokenException("Failed to authenticate user.");
		}
	}

	/**
	 * Invalidates the token by adding it to the in-memory blacklist. Returns true
	 * on success.
	 */
	@Override
	public Boolean logoutUser(String authHeader) {
log.info("Inside logoutUser");
		String token = jwtTokenUtil.extractBearerToken(authHeader);
		log.info("token: "+token);
		if (token != null && jwtTokenUtil.isTokenValid(token) && !tokenBlacklist.contains(token)) {
			log.info("Token is valid");
			tokenBlacklist.add(token);
			return true;
		}else if (tokenBlacklist.contains(token)) {
			log.info("Already logged out");
		}
		return false;

//		UUID uuid = UUID.fromString(token);
//		authenticatedUserRepository.deleteById(uuid);
		// return true;
	}

	@Override
	@Transactional
	public UserDto createUser(UserDto userDto) {
		try {
			UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);

			userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
			userEntity.setRoles("ROLE_USER");
			userEntity.setActive("true");

			UserEntity savedUser = userRepository.save(userEntity);
			return modelMapper.map(savedUser, UserDto.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to create user", e); // will handle it later
		}
	}

	@Override
	public Boolean validateToken(String authHeader) {

		String token = jwtTokenUtil.extractBearerToken(authHeader);
		if (token == null || tokenBlacklist.contains(token)) {
			return false;
		}
		return jwtTokenUtil.isTokenValid(token);
	}

//		AuthenticatedUserEntity authenticatedUserEntity = authenticatedUserRepository.findById(UUID.fromString(token))
//				.orElseThrow(() -> new RuntimeException("Invalid token."));
//

//		if (authenticatedUserEntity.getUsername() != null)
//			return true;
//		else
//			return false;

	public String getUserFromToken(String token) {
		String user = tokenStore.get(token);
		if (user == null) {
			throw new RuntimeException("Invalid token");
		}
		return user;
	}

	@Override
	public UserDto getUserByUsername(String userName) {
		UserEntity userEntity = userRepository.findByUserName(userName)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + userName));

		return modelMapper.map(userEntity, UserDto.class);
	}

}
