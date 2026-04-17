package com.sushma.olxlogin.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sushma.olxlogin.Repository.AuthenticatedUserRepository;
import com.sushma.olxlogin.Repository.OlxLoginRepository;
import com.sushma.olxlogin.dto.AuthenticateResponseUserDto;
import com.sushma.olxlogin.dto.AuthenticateUserRequestDto;
import com.sushma.olxlogin.dto.UserDto;
import com.sushma.olxlogin.entity.AuthenticatedUserEntity;
import com.sushma.olxlogin.entity.UserEntity;
import com.sushma.olxlogin.exception.handler.ConflictException;
import com.sushma.olxlogin.exception.handler.InvalidTokenException;
import com.sushma.olxlogin.service.OlxLoginService;

@Service
public class OlxLoginServiceImpl implements OlxLoginService {

	@Autowired
	public OlxLoginRepository loginRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private AuthenticatedUserRepository authenticatedUserRepository;

	private Map<String, String> tokenStore = new HashMap();

	@Override
	public AuthenticateResponseUserDto authenticateUser(AuthenticateUserRequestDto userDto) {
		try {
			AuthenticatedUserEntity requestEntity = new AuthenticatedUserEntity();
			requestEntity.setUsername(userDto.getUserName());

			AuthenticatedUserEntity authenticatedUserEntity = authenticatedUserRepository.save(requestEntity);

			return new AuthenticateResponseUserDto(authenticatedUserEntity.getToken().toString());
		} catch (Exception e) {
			throw new InvalidTokenException("Failed to authenticate user.");
		}
	}

	@Override
	public Boolean logoutUser(String token) {
		UUID uuid = UUID.fromString(token);
		authenticatedUserRepository.deleteById(uuid);
		return true;
	}

	@Override
	public UserDto createUser(UserDto userDto) {
		try {
			System.out.println("userDto:"+userDto.toString());
			UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
			System.out.println("userEntity:"+userEntity.toString());
			UserEntity savedUser = loginRepository.save(userEntity);
			System.out.println("savedUser:"+savedUser.toString());
			return modelMapper.map(savedUser, UserDto.class);
		} catch (Exception e) {

			throw new ConflictException("User already exists"); // will handle it later
		}
	}

	@Override
	public UserDto getUserInformation(String token) {

		UUID tokenUUID = UUID.fromString(token);

		AuthenticatedUserEntity authenticatedUserEntity = authenticatedUserRepository.findById(tokenUUID)
				.orElseThrow(() -> new RuntimeException("Token not valid."));

		// String userName = getUserFromToken(token);
		try {
			UserEntity userEntity = loginRepository.findByUserName(authenticatedUserEntity.getUsername())
					.orElseThrow(() -> new RuntimeException("User not found"));

			return modelMapper.map(userEntity, UserDto.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new InvalidTokenException("Invalid token");
		}

	}

	@Override
	public Boolean validateToken(String token) {
		AuthenticatedUserEntity authenticatedUserEntity = authenticatedUserRepository.findById(UUID.fromString(token))
				.orElseThrow(() -> new RuntimeException("Invalid token."));

		if (authenticatedUserEntity.getUsername() != null)
			return true;
		else
			return false;
	}

	public String getUserFromToken(String token) {
		String user = tokenStore.get(token);
		if (user == null) {
			throw new RuntimeException("Invalid token");
		}
		return user;
	}

}
