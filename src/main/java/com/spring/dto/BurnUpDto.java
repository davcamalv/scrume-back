package com.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BurnUpDto {
	String date;
	long pointsBurnUp;
	long totalHistoryTask;

}
