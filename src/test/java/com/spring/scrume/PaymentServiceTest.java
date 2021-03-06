package com.spring.scrume;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import com.spring.dto.PaymentEditDto;
import com.spring.service.PaymentService;

public class PaymentServiceTest extends AbstractTest {

	@Autowired
	private PaymentService service;

	@Test
	public void testFindAllPaymentsByUserLogged() {
		Object[][] objects = { { "testuser1@gmail.com", null }, { null, AssertionError.class } };
		Stream.of(objects).forEach(x -> driverFindAllPaymentsByUserLogged((String) x[0], (Class<?>) x[1]));
	}

	protected void driverFindAllPaymentsByUserLogged(String user, Class<?> expected) {
		Class<?> caught = null;

		try {
			super.authenticateOrUnauthenticate(user);
			this.service.findPaymentsByUserLogged();
			service.flush();
			super.authenticateOrUnauthenticate(null);
		} catch (Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}

	@Test
	public void testPago() {
		Object[][] objects = { 
				{ "testuser1@gmail.com", LocalDate.of(2020, 01, 24), ResponseStatusException.class }, 
				{ "testuser1@gmail.com", LocalDate.of(9999, 04, 24), null }
				};
		Stream.of(objects).forEach(x -> driverPago((String) x[0], (LocalDate) x[1], (Class<?>) x[2]));
	}

	protected void driverPago(String user, LocalDate date, Class<?> expected) {
		Class<?> caught = null;

		try {
			super.authenticateOrUnauthenticate(user);
			PaymentEditDto payment = new PaymentEditDto(0, super.entities().get("proBox"), date, "ABCD1234",
					"ASFDFD59842", null);
			this.service.save(payment);
			service.flush();
			super.authenticateOrUnauthenticate(null);
		} catch (Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
}
