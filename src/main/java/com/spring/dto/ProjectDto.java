package com.spring.dto;

import com.spring.model.Team;

import lombok.Data;

@Data
public class ProjectDto {
	
	private Integer id;
	
	private String name;
	
	private String description;
	
	private Team team;
}
