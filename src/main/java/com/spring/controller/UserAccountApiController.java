package com.spring.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.BeanDefinitionDsl.Role;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.spring.dto.UserAccountDto;
import com.spring.dto.UsernameDto;
import com.spring.jwt.JwtRequest;
import com.spring.jwt.JwtResponse;
import com.spring.jwt.JwtUserAccountService;
import com.spring.security.UserAccountService;

@RestController
@RequestMapping("/api/login")
public class UserAccountApiController extends AbstractApiController {

	@Autowired
	private UserAccountService service;

	@Autowired
	private JwtUserAccountService serviceJwt;
	
	@PostMapping("/authenticate")
	public JwtResponse authenticate(@RequestBody JwtRequest request) {
		return serviceJwt.generateToken(request);
	}
	
	@GetMapping("/roles")
	public Role[] findAllRoles() {
		super.logger.info("GET /api/login/roles");
		return Role.values();
	}

	@GetMapping("/logout")
	public void logout(HttpSession session) {
		super.logger.info("GET /api/profile/logout ");
		super.authenticateOrUnauthenticate(null);
		session.invalidate();
	}

	@PostMapping
	public UserAccountDto save(@RequestBody UserAccountDto userAccountDto) {
		super.logger.info("POST /api/userAccount");
		return this.service.save(userAccountDto);
	}

	@PutMapping("/{idUserAccount}")
	public UserAccountDto update(@PathVariable Integer idUserAccount, @RequestBody UserAccountDto userAccountDto) {
		super.logger.info("UPDATE /api/userAccount");
		return this.service.update(idUserAccount, userAccountDto);
	}

	@PostMapping("/isAValidEmail")
	@CrossOrigin(origins = "*", methods = { RequestMethod.POST })
	public Boolean isAValidEmail(@RequestBody UsernameDto usernameDto) {
		super.logger.info("POST /api/login/isAValidEmail");
		return !this.service.isAValidEmail(usernameDto.getUsername());
	}

}
