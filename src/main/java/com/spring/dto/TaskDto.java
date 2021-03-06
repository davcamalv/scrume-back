package com.spring.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskDto {

	private String title;

	private String description;

	private Integer points;
	private Integer project;

	private Set<UserForWorkspaceDto> users;

	private Integer column;

}