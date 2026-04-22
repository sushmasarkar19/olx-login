package com.sushma.olxlogin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

	private Long id;
	private String firstName;
	private String lastName;
	private String userName;
	private String password;
	private String email;
	private int phone;

    private String roles;
 
    private String active;
 
	
}
