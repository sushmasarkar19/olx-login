package com.sushma.olxlogin.dto;

public class AuthResponseDto {

	private String token;

	public AuthResponseDto(String token) {
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
