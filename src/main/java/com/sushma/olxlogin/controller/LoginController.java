package com.sushma.olxlogin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sushma.olxlogin.dto.AuthenticateResponseUserDto;
import com.sushma.olxlogin.dto.AuthenticateUserRequestDto;
import com.sushma.olxlogin.dto.UserDto;
import com.sushma.olxlogin.service.OlxLoginService;

@RestController
@RequestMapping("/olx/user")
public class LoginController {

	@Autowired
	private OlxLoginService loginService;

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticateResponseUserDto> authenticateUser(
			@RequestBody AuthenticateUserRequestDto userDto) {

		AuthenticateResponseUserDto authenticateUserDto = loginService.authenticateUser(userDto);

		return new ResponseEntity<AuthenticateResponseUserDto>(authenticateUserDto, HttpStatus.OK);
	}

	@DeleteMapping("/logout")
	public ResponseEntity<Boolean> logoutUser(@RequestHeader("Authorization") String authToken) {

		Boolean isUserLoggedOut = loginService.logoutUser(authToken);

		return new ResponseEntity<Boolean>(isUserLoggedOut, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {

		UserDto createdUser = loginService.createUser(userDto);

		return new ResponseEntity<UserDto>(createdUser, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<UserDto> getUserInformation(@RequestHeader("Authorization") String token) {

		// UserDto userDto = new UserDto(1, "Sushma", "Sarkar", "sg@12", "9870@",
		// "gh@gmail.com", 10986650);
		UserDto userDto = loginService.getUserInformation(token);

		return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
	}

	@GetMapping("/token/validate")
	public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
		return new ResponseEntity<Boolean>(loginService.validateToken(token), HttpStatus.OK);
	}

}
