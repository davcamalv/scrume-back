package com.spring.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.dto.HistoryTaskDto;
import com.spring.model.HistoryTask;
import com.spring.service.HistoryTaskService;

@RestController
@RequestMapping("/api/history-task")
public class HistoryTaskApiController extends AbstractApiController {

	@Autowired
	private HistoryTaskService serviceHistoryTask;

	@GetMapping("/historical/{workspace}")
	public Collection<HistoryTask> findHistoricalByWorkspace(@PathVariable int workspace) {
		super.logger.info("GET /api/history-task/historical/" + workspace);
		return this.serviceHistoryTask.findHistoricalByWorkspace(workspace);
	}

	@PostMapping("/move")
	@ResponseBody
	public HistoryTaskDto moveTask(@RequestBody HistoryTaskDto dto) {
		super.logger.info("POST /api/history-task/move");
		return serviceHistoryTask.save(dto);
	}

}
