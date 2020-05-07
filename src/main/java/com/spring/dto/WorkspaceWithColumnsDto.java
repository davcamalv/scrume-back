package com.spring.dto;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkspaceWithColumnsDto {
	private Integer id;

	private String name;

	private Collection<ColumnDto> columns;
}
