package com.spring.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.dto.PaymentEditDto;
import com.spring.dto.PaymentListDto;
import com.spring.jwt.JwtResponse;
import com.spring.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentApiController extends AbstractApiController {

	@Autowired
	private PaymentService service;

	@GetMapping("/all")
	public Collection<PaymentListDto> allMine() {
		return service.findPaymentsByUserLogged();
	}

	@PostMapping("/pay")
	public JwtResponse pay(@RequestBody PaymentEditDto payment) {
		return service.save(payment);
	}

}
