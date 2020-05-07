package com.spring.dto;

import com.spring.modelS.Team;
import com.spring.modelS.User;

import lombok.Data;

@Data
public class UserRolDto {
	private Integer id;

		private Team team;
		private User user;
		private Boolean admin;

}
