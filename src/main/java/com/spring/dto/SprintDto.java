package com.spring.dto;

import java.util.Date;

import com.spring.modelS.Project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SprintDto {
	
	private Integer id;
	
	private Date startDate;
	
	private Date endDate;
	
	private Project project;
}
