package com.spring.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.spring.dto.DiscountCodeDto;
import com.spring.model.DiscountCode;
import com.spring.model.User;
import com.spring.repository.DiscountCodeRepository;
import com.spring.security.Role;

@Service
@Transactional
public class DiscountCodeService extends AbstractService {
	
	@Autowired
	private DiscountCodeRepository discountCodeRepository;
	
	@Autowired
	private UserService userService;
	
	public DiscountCode findOne(int idDiscountCode) {
		return this.discountCodeRepository.findById(idDiscountCode).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,  "The requested code not exists"));
	}
	
	public DiscountCode getOne(Integer idDiscountCode) {
		User principal = this.userService.getUserByPrincipal();
		this.validateIsLogged(principal);
		this.validatePrincipal(principal);
		return this.discountCodeRepository.findById(idDiscountCode).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,  "The requested code not exists"));
	}
	
	public DiscountCode save(DiscountCodeDto discountCodeDto) {
		User principal = this.userService.getUserByPrincipal();
		this.validateIsLogged(principal);
		this.validatePrincipal(principal);
		this.validateDate(discountCodeDto.getExpiredDate());
		return this.discountCodeRepository.saveAndFlush(new DiscountCode(discountCodeDto.getExpiredDate(), discountCodeDto.getCode()));
	}
	
	public List<DiscountCode> list() {
		User principal = this.userService.getUserByPrincipal();
		this.validateIsLogged(principal);
		this.validatePrincipal(principal);
		this.discountCodeRepository.deleteAll(this.discountCodeRepository.findAllExpiredDiscountCodes());
		return this.discountCodeRepository.findAll();
	}
	
	public void delete(int idDiscountCode) {
		User principal = this.userService.getUserByPrincipal();
		this.validateIsLogged(principal);
		this.validatePrincipal(principal);
		DiscountCode discountCode = this.findOne(idDiscountCode);
		this.discountCodeRepository.delete(discountCode);
	}
	
	public void deleteVoid(Integer codeId) {
		DiscountCode discountCode = this.findOne(codeId);
		this.discountCodeRepository.delete(discountCode);
	}
	
	public Integer isAValidDiscountCode(String code) {
		List<DiscountCode> discountCodes = this.discountCodeRepository.findByCode(code);
		this.validateCodes(discountCodes);
		return discountCodes.get(0).getId();
	}
	
	private void validateCodes(List<DiscountCode> discountCodes) {
		if(discountCodes.isEmpty())	{
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
					"Invalid code");
		}	
	}

	private void validateIsLogged(User principal) {
		if(principal == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
					"The user must be logged in");
		}
	}
	
	private void validatePrincipal(User principal) {
		if(!principal.getUserAccount().getRoles().contains(Role.ROLE_ADMIN)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
				"The user does not have permission to manage the security breach");
		}
	}
	
	private void validateDate(Date date) {
		if (date.before(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expired date cannot be earlier than the current one");
		}
	}

	public void flush() {
		this.discountCodeRepository.flush();
	}

	

	

}
