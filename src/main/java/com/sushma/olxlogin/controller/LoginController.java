package com.sushma.olxlogin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sushma.olxlogin.dto.AuthRequestDto;
import com.sushma.olxlogin.dto.AuthResponseDto;
import com.sushma.olxlogin.dto.UserDto;
import com.sushma.olxlogin.service.OlxLoginService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/user")
@Log4j2
public class LoginController {

	@Autowired
	private OlxLoginService loginService;

	@PostMapping("/authenticate")
	public ResponseEntity<AuthResponseDto> authenticateUser(@RequestBody AuthRequestDto userDto) {

		AuthResponseDto authResponseUserDto = loginService.authenticateUser(userDto);

		return new ResponseEntity<AuthResponseDto>(authResponseUserDto, HttpStatus.OK);
	}

	@DeleteMapping("/logout")
	public ResponseEntity<Boolean> logoutUser(@RequestHeader("Authorization") String authHeader) {

		Boolean isUserLoggedOut = loginService.logoutUser(authHeader);
		return new ResponseEntity<Boolean>(isUserLoggedOut, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {

		UserDto createdUser = loginService.createUser(userDto);

		return new ResponseEntity<UserDto>(createdUser, HttpStatus.CREATED);
	}

	@GetMapping("/info")
	public ResponseEntity<UserDto> getUserInformation(@RequestHeader("Authorization") String token) {
		log.info("Get user info");
		// UserDto userDto = new UserDto(1, "Sushma", "Sarkar", "sg@12", "9870@",
		// "gh@gmail.com", 10986650);

		// Spring Security has already authenticated the request via
		// JwtAuthenticationFilter
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		UserDto user = loginService.getUserByUsername(username);
		// return ResponseEntity.ok(user);

		return new ResponseEntity<UserDto>(user, HttpStatus.OK);
	}

	@GetMapping("/token/validate")
	public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
		return new ResponseEntity<Boolean>(loginService.validateToken(authHeader), HttpStatus.OK);
	}

}
