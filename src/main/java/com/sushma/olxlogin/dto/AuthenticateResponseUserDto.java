package com.sushma.olxlogin.dto;

public class AuthenticateResponseUserDto {

	private String token;

	public AuthenticateResponseUserDto(String token) {
		super();
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
