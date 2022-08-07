package com.learncode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learncode.domain.Account;
@Repository
public interface AccountRespository extends JpaRepository<Account, String>{
	
}
