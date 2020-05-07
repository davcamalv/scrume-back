package com.spring.scrumeS;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import com.spring.dto.DiscountCodeDto;
import com.spring.dto.ValidCodeDto;
import com.spring.serviceS.DiscountCodeService;

public class DiscountCodeServiceTest extends AbstractTest {

	@Autowired
	private DiscountCodeService discountCodeService;
	
	@Test
	public void DiscountCodeServiceGetTest() throws Exception {
		
		Object[][] objects = {
			
			{"testuser1@gmail.com", entities().get("discountCode1"), ResponseStatusException.class},
			{"administrator@gmail.com", entities().get("discountCode1"), null}};

			Stream.of(objects).forEach(x -> driverDiscountCodeServiceGetTest((String) x[0],(Integer) x[1], (Class<?>) x[2]));
		}

	protected void driverDiscountCodeServiceGetTest(String user, Integer idDiscountCode, Class<?> expected) {
		Class<?> caught = null;
		try {
			super.authenticateOrUnauthenticate(user);
			this.discountCodeService.getOne(idDiscountCode);
			this.discountCodeService.flush();
			super.authenticateOrUnauthenticate(null);

		} catch (Exception oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
	@Test
	public void DiscountCodeServiceListTest() throws Exception {
		
		Object[][] objects = {
			
			{"testuser1@gmail.com", ResponseStatusException.class},
			{"administrator@gmail.com", null}};

			Stream.of(objects).forEach(x -> driverDiscountCodeServiceListTest((String) x[0], (Class<?>) x[1]));
		}

	protected void driverDiscountCodeServiceListTest(String user, Class<?> expected) {
		Class<?> caught = null;
		try {
			super.authenticateOrUnauthenticate(user);
			this.discountCodeService.list();
			this.discountCodeService.flush();
			super.authenticateOrUnauthenticate(null);

		} catch (Exception oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
	@Test
	public void DiscountCodeServiceSaveTest() throws Exception {
		
		LocalDateTime localDateTime1 = LocalDateTime.of(9999, 12, 30, 00, 00);
		Date localDate1 = Date.from(localDateTime1.atZone(ZoneId.systemDefault()).toInstant());
		
		LocalDateTime localDateTime2 = LocalDateTime.of(2019, 12, 30, 00, 00);
		Date localDate2 = Date.from(localDateTime2.atZone(ZoneId.systemDefault()).toInstant());
		
		DiscountCodeDto discountCodeDto1 = new DiscountCodeDto(localDate1, "covid20");
		DiscountCodeDto discountCodeDto2 = new DiscountCodeDto(localDate2, "covid20");

		Object[][] objects = {
			{"testuser1@gmail.com", discountCodeDto1, ResponseStatusException.class},
			{"administrator@gmail.com", discountCodeDto2, ResponseStatusException.class},
			{"administrator@gmail.com", discountCodeDto1, null}};

			Stream.of(objects).forEach(x -> driverDiscountCodeServiceSaveTest((String) x[0],(DiscountCodeDto) x[1], (Class<?>) x[2]));
		}

	protected void driverDiscountCodeServiceSaveTest(String user, DiscountCodeDto discountCodeDto, Class<?> expected) {
		Class<?> caught = null;
		try {
			super.authenticateOrUnauthenticate(user);
			this.discountCodeService.save(discountCodeDto);
			this.discountCodeService.flush();
			super.authenticateOrUnauthenticate(null);

		} catch (Exception oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
	@Test
	public void DiscountCodeServiceDeleteTest() throws Exception {
		
		Object[][] objects = {
			
			{"testuser1@gmail.com", entities().get("discountCode1"), ResponseStatusException.class},
			{"administrator@gmail.com", entities().get("discountCode1"), null}};

			Stream.of(objects).forEach(x -> driverDiscountCodeServiceDeleteTest((String) x[0],(Integer) x[1], (Class<?>) x[2]));
		}

	protected void driverDiscountCodeServiceDeleteTest(String user, Integer idDiscountCode, Class<?> expected) {
		Class<?> caught = null;
		try {
			super.authenticateOrUnauthenticate(user);
			this.discountCodeService.delete(idDiscountCode);
			this.discountCodeService.flush();
			super.authenticateOrUnauthenticate(null);

		} catch (Exception oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
	
	@Test
	public void DiscountCodeServiceIsAValidCodeTest() throws Exception {
		ValidCodeDto validCodeDto1 = new ValidCodeDto("covid19");
		ValidCodeDto validCodeDto2 = new ValidCodeDto("qwertyu");

		Object[][] objects = {
			
			{"testuser1@gmail.com", validCodeDto1, null},
			{"testuser1@gmail.com", validCodeDto2, null}};

			Stream.of(objects).forEach(x -> driverDiscountCodeServiceIsAValidCodeTest((String) x[0],(ValidCodeDto) x[1], (Class<?>) x[2]));
		}

	protected void driverDiscountCodeServiceIsAValidCodeTest(String user, ValidCodeDto validCodeDto, Class<?> expected) {
		Class<?> caught = null;
		try {
			super.authenticateOrUnauthenticate(user);
			this.discountCodeService.isAValidDiscountCode(validCodeDto);
			this.discountCodeService.flush();
			super.authenticateOrUnauthenticate(null);

		} catch (Exception oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);
	}
}
