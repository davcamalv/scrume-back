package com.spring.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSaveDto {
	
	private String title;

    private Date date;
	
    private Integer sprint;
	
}