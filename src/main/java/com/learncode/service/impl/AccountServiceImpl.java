package com.learncode.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.learncode.domain.Account;
import com.learncode.repository.AccountRespository;
import com.learncode.service.AccountService;



@Service
public class AccountServiceImpl implements AccountService{
	@Autowired
	AccountRespository accountRespository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public Account login(String username, String password) {
		Optional<Account> opExist = findById(username);
		
		if(opExist.isPresent() && bCryptPasswordEncoder.matches(password, opExist.get().getPassword())) {
			opExist.get().setPassword("");
			return opExist.get();
		}
		
		return null;
	}
	
	@Override
	public <S extends Account> S save(S entity) {
		Optional<Account> opExist = findById(entity.getUsername());
		if(opExist.isPresent()) {
			if(StringUtils.isEmpty(entity.getPassword())){
				entity.setPassword(opExist.get().getPassword());
			}else {
				entity.setPassword(bCryptPasswordEncoder.encode(entity.getPassword()));
			}
		}else {
			entity.setPassword(bCryptPasswordEncoder.encode(entity.getPassword()));
		}

		
		return accountRespository.save(entity);
	}

	@Override
	public <S extends Account> Optional<S> findOne(Example<S> example) {
		return accountRespository.findOne(example);
	}

	@Override
	public List<Account> findAll() {
		return accountRespository.findAll();
	}

	@Override
	public Page<Account> findAll(Pageable pageable) {
		return accountRespository.findAll(pageable);
	}

	@Override
	public List<Account> findAll(Sort sort) {
		return accountRespository.findAll(sort);
	}

	@Override
	public List<Account> findAllById(Iterable<String> ids) {
		return accountRespository.findAllById(ids);
	}

	@Override
	public Optional<Account> findById(String id) {
		return accountRespository.findById(id);
	}

	@Override
	public <S extends Account> List<S> saveAll(Iterable<S> entities) {
		return accountRespository.saveAll(entities);
	}

	@Override
	public void flush() {
		accountRespository.flush();
	}

	@Override
	public boolean existsById(String id) {
		return accountRespository.existsById(id);
	}

	@Override
	public <S extends Account> S saveAndFlush(S entity) {
		return accountRespository.saveAndFlush(entity);
	}

	@Override
	public <S extends Account> List<S> saveAllAndFlush(Iterable<S> entities) {
		return accountRespository.saveAllAndFlush(entities);
	}

	@Override
	public <S extends Account> Page<S> findAll(Example<S> example, Pageable pageable) {
		return accountRespository.findAll(example, pageable);
	}

	@Override
	public void deleteInBatch(Iterable<Account> entities) {
		accountRespository.deleteInBatch(entities);
	}

	@Override
	public <S extends Account> long count(Example<S> example) {
		return accountRespository.count(example);
	}

	@Override
	public void deleteAllInBatch(Iterable<Account> entities) {
		accountRespository.deleteAllInBatch(entities);
	}

	@Override
	public long count() {
		return accountRespository.count();
	}

	@Override
	public <S extends Account> boolean exists(Example<S> example) {
		return accountRespository.exists(example);
	}

	@Override
	public void deleteById(String id) {
		accountRespository.deleteById(id);
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<String> ids) {
		accountRespository.deleteAllByIdInBatch(ids);
	}

	@Override
	public void delete(Account entity) {
		accountRespository.delete(entity);
	}

	@Override
	public <S extends Account, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
		return accountRespository.findBy(example, queryFunction);
	}

	@Override
	public void deleteAllById(Iterable<? extends String> ids) {
		accountRespository.deleteAllById(ids);
	}

	@Override
	public void deleteAllInBatch() {
		accountRespository.deleteAllInBatch();
	}

	@Override
	public Account getOne(String id) {
		return accountRespository.getOne(id);
	}

	@Override
	public void deleteAll(Iterable<? extends Account> entities) {
		accountRespository.deleteAll(entities);
	}

	@Override
	public void deleteAll() {
		accountRespository.deleteAll();
	}

	@Override
	public Account getById(String id) {
		return accountRespository.getById(id);
	}

	@Override
	public Account getReferenceById(String id) {
		return accountRespository.getReferenceById(id);
	}

	@Override
	public <S extends Account> List<S> findAll(Example<S> example) {
		return accountRespository.findAll(example);
	}

	@Override
	public <S extends Account> List<S> findAll(Example<S> example, Sort sort) {
		return accountRespository.findAll(example, sort);
	}
	
}
