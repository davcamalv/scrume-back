package com.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.dto.DiscountCodeDto;
import com.spring.dto.ValidCodeDto;
import com.spring.modelS.DiscountCode;
import com.spring.serviceS.DiscountCodeService;

@RestController
@RequestMapping("/api/discount-code")
public class DiscountCodeApiController extends AbstractApiController {
	
	@Autowired
	private DiscountCodeService discountCodeService;
	

	@GetMapping("{idDiscountCode}")
	public DiscountCode get(@PathVariable int idDiscountCode){
		super.logger.info("GET /api/discount-code/" + idDiscountCode);
		return this.discountCodeService.getOne(idDiscountCode);
	}
	
	@GetMapping("/showAll")
	public List<DiscountCode> showAll(){
		super.logger.info("GET /api/discount-code/showAll");
		return this.discountCodeService.list();
	}
	
	@PostMapping
	public DiscountCode save(@RequestBody DiscountCodeDto discountCodeDto) {
		super.logger.info("POST /api/discount-code");
		return this.discountCodeService.save(discountCodeDto);
	}
	
	@DeleteMapping("{idDiscountCode}")
	public void delete(@PathVariable int idDiscountCode) {
		super.logger.info("DELETE /api/discount-code/" + idDiscountCode);
		this.discountCodeService.delete(idDiscountCode);
	}
	
	@GetMapping("/isAValidCode")
	public boolean update(@RequestBody ValidCodeDto validCodeDto) {
		super.logger.info("GET /api/discount-code/isAValidCode" + validCodeDto);
		return this.discountCodeService.isAValidDiscountCode(validCodeDto);
	}
	

}
