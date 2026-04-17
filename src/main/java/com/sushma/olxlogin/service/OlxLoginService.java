package com.sushma.olxlogin.service;

import com.sushma.olxlogin.dto.AuthenticateResponseUserDto;
import com.sushma.olxlogin.dto.AuthenticateUserRequestDto;
import com.sushma.olxlogin.dto.UserDto;

public interface OlxLoginService {
	public AuthenticateResponseUserDto authenticateUser(AuthenticateUserRequestDto userDto);

	public Boolean logoutUser(String token);

	public UserDto createUser(UserDto userDto);

	public UserDto getUserInformation(String token);

	public Boolean validateToken(String token);

}
