package com.spring.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.spring.dto.PaymentEditDto;
import com.spring.dto.PaymentListDto;
import com.spring.jwt.JwtResponse;
import com.spring.jwt.JwtUserAccountService;
import com.spring.model.Payment;
import com.spring.model.User;
import com.spring.model.UserAccount;
import com.spring.repository.PaymentRepository;
import com.spring.security.UserAccountService;

@Service
@Transactional
public class PaymentService extends AbstractService {

	@Autowired
	private UserService serviceUser;

	@Autowired
	private BoxService serviceBox;

	@Autowired
	private JwtUserAccountService serviceJwt;

	@Autowired
	private PaymentRepository repository;

	public Collection<PaymentListDto> findPaymentsByUserLogged() {
		User user = this.serviceUser.getUserByPrincipal();
		return repository.findPaymentsByUser(user.getUserAccount().getId()).stream()
				.map(x -> new PaymentListDto(x.getExpiredDate(), x.getPaymentDate(), x.getBox().getName(),
						x.getBox().getPrice()))
				.collect(Collectors.toList());
	}

	public JwtResponse save(PaymentEditDto payment) {
		this.validateDate(payment.getExpiredDate());				
		Payment saveTo = null;

		UserAccount account = UserAccountService.getPrincipal();

		if (payment.getId() == 0) {

			Payment pagoAnterior = findByUserAccount(account);

			if (LocalDate.now().isAfter(pagoAnterior.getExpiredDate())) {
				saveTo = new Payment(LocalDate.now(), this.serviceBox.getOne(payment.getBox()), account,
				payment.getExpiredDate(), payment.getOrderId(), payment.getPayerId());
				saveTo = repository.saveAndFlush(saveTo);
				payment.setId(saveTo.getId());
			} else {
				this.calculateRenovation(payment, pagoAnterior, account);
			}

		}

		return serviceJwt.generateToken(account);
	}

	
	public PaymentEditDto save(UserAccount user, PaymentEditDto payment) {

		Payment saveTo = null;

		if (payment.getId() == 0) {
			if (payment.getExpiredDate().isAfter(LocalDate.now())) {
				saveTo = new Payment(LocalDate.now(), this.serviceBox.getOne(payment.getBox()), user,
						payment.getExpiredDate(), payment.getOrderId(), payment.getPayerId());
				saveTo = repository.saveAndFlush(saveTo);
				payment.setId(saveTo.getId());
			} else {
				throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED,
						"Expired date is not later than currently");
			}

		}

		return payment;
	}

	public void flush() {
		repository.flush();
	}

	public Payment findByUserAccount(UserAccount userAccount) {
		Payment res;
		List<Payment> payments = this.repository.findByUserAccount(userAccount);
		if (payments.isEmpty()) {
			res = null;
		} else {
			res = payments.get(0);
		}
		return res;
	}

	private void validateDate(LocalDate date) {
		if (!date.isAfter(LocalDate.now())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expired date is not later than currently");
		}
	}
	
	private Payment calculateRenovation(PaymentEditDto paymentEditDto, Payment oldPayment, UserAccount account) {
		long dayDifference = ChronoUnit.DAYS.between(LocalDate.now(), oldPayment.getExpiredDate());
		Payment payment;
		String newBox = this.serviceBox.findOne(paymentEditDto.getBox()).getName();
		String oldBox = oldPayment.getBox().getName();
		if(newBox.equals(oldBox)) {
			paymentEditDto.setExpiredDate(paymentEditDto.getExpiredDate().plusDays(dayDifference));
		}else if(oldBox.equals("STANDARD") && newBox.equals("PRO")) {
			dayDifference = dayDifference/2;
			paymentEditDto.setExpiredDate(paymentEditDto.getExpiredDate().plusDays(dayDifference));
		}else if(oldBox.equals("PRO") && newBox.equals("STANDARD")) {
			dayDifference = dayDifference*2;
			paymentEditDto.setExpiredDate(paymentEditDto.getExpiredDate().plusDays(dayDifference));
		}
		
		payment = repository.saveAndFlush(new Payment(LocalDate.now(), this.serviceBox.getOne(paymentEditDto.getBox()), account,
				paymentEditDto.getExpiredDate(), paymentEditDto.getOrderId(), paymentEditDto.getPayerId()));
		return payment;
	}
}
