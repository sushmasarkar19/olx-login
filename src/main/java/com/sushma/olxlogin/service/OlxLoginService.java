package com.sushma.olxlogin.service;

import com.sushma.olxlogin.dto.AuthResponseDto;
import com.sushma.olxlogin.dto.AuthRequestDto;
import com.sushma.olxlogin.dto.UserDto;

public interface OlxLoginService {
	public AuthResponseDto authenticateUser(AuthRequestDto userDto);

	public Boolean logoutUser(String token);

	public UserDto createUser(UserDto userDto);

	public Boolean validateToken(String token);

	public UserDto getUserByUsername(String userName);

}
