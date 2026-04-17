package com.sushma.olxlogin.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AuthenticatedUserEntity {
	@Column
	private String username;
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID token;

	public AuthenticatedUserEntity(String username, UUID token) {
		super();
		this.username = username;
		this.token = token;
	}

	public AuthenticatedUserEntity() {
		// TODO Auto-generated constructor stub
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UUID getToken() {
		return token;
	}

	public void setToken(UUID token) {
		this.token = token;
	}

}
