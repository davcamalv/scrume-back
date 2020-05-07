package com.spring.dto;

import java.util.Collection;
import java.util.Map;

import com.spring.modelS.Column;
import com.spring.modelS.Task;
import com.spring.modelS.Workspace;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkspaceDto {
	private Integer id;

	private Workspace workspace;

	private Map<Column, Collection<Task>> columns;
}
