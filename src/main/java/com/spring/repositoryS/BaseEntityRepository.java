package com.spring.repositoryS;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.modelS.BaseEntity;

@Repository
public interface BaseEntityRepository extends JpaRepository<BaseEntity, Integer> {

}
