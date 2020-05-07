package com.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.dto.SecurityBreachDto;
import com.spring.model.SecurityBreach;
import com.spring.service.SecurityBreachService;

@RestController
@RequestMapping("/api/administrator")
public class AdministratorApiController extends AbstractApiController {

	@Autowired
	private SecurityBreachService securityBreachService;
	
	@PutMapping
	public SecurityBreach updateSecurityBreach(@RequestBody SecurityBreachDto securityBreachDto){
		return this.securityBreachService.updateSecurityBreach(securityBreachDto);
	}
	
}
