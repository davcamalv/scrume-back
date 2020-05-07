package com.spring.repositoryS;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spring.modelS.DiscountCode;

@Repository
public interface DiscountCodeRepository extends AbstractRepository<DiscountCode>{

	@Query(value = "select d from DiscountCode d where d.expiredDate < current_timestamp")
	List<DiscountCode> findAllExpiredDiscountCodes();

	@Query(value = "select d from DiscountCode d where d.expiredDate > current_timestamp and d.code = ?1")
	List<DiscountCode> findByCode(String code);

}
