package com.learncode.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.learncode.domain.Account;
import com.learncode.model.AccountDto;
import com.learncode.model.AdminLoginDto;
import com.learncode.service.AccountService;
import com.learncode.service.AccountService;

import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;


@Controller
@RequestMapping("admin/accounts")
public class AccountController {
	@Autowired
	AccountService accountService;
	
	@Autowired
	private HttpSession session;
	
	@GetMapping("add")
	public String add(Model model) {
		model.addAttribute("account",new AccountDto());
		return "admin/accounts/addOrEdit";
	}
		
	
	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model
			,@Valid @ModelAttribute("account") AccountDto dto
			,BindingResult result) {
		if(result.hasErrors()) {
			return new ModelAndView("admin/accounts/addOrEdit");
		}
		
		Account entity = new Account();
		
		BeanUtils.copyProperties(dto, entity);
		
		accountService.save(entity);
		
		model.addAttribute("message","account is saved");
		
		return new ModelAndView("forward:/admin/accounts",model);
	}
	
	@RequestMapping("")
	public String list(ModelMap model) {
		List<Account> list = accountService.findAll();
		
		model.addAttribute("accounts",list);
		
		return "admin/accounts/list";
	}
	@GetMapping("edit/{username}")
	public ModelAndView edit(ModelMap model, @PathVariable("username") String username) {
		Optional<Account> otp = accountService.findById(username);
		AccountDto dto = new AccountDto();
		if(otp.isPresent()) {
			Account entity = otp.get();
			BeanUtils.copyProperties(entity, dto);
			dto.setIsEdit(true);
			dto.setPassword("");
			model.addAttribute("account",dto);
			return new ModelAndView("admin/accounts/addOrEdit");
		}
		
		model.addAttribute("message", "account is not existed");
		return new ModelAndView("redirect:/admin/accounts");
}
	
//	@GetMapping("delete/{accountId}")
//	public ModelAndView delete(ModelMap model, @PathVariable("accountId") Long accountId) {
//		accountService.deleteById(accountId);
//		model.addAttribute("message","Delete successfully !!!");
//		return new ModelAndView("forward:/admin/accounts/search",model);
//	}
//	
//	@PostMapping("saveOrUpdate")
//	public ModelAndView saveOrUpdate(ModelMap model
//			,@Valid @ModelAttribute("account") AccountDto dto
//			,BindingResult result) {
//		if(result.hasErrors()) {
//			return new ModelAndView("admin/accounts/addOrEdit");
//		}
//		
//		account entity = new account();
//		
//		BeanUtils.copyProperties(dto, entity);
//		
//		accountService.save(entity);
//		
//		model.addAttribute("message","account is saved");
//		
//		return new ModelAndView("forward:/admin/accounts",model);
//	}
//	
//	@RequestMapping("")
//	public String list(ModelMap model) {
//		List<account> list = accountService.findAll();
//		
//		model.addAttribute("accounts",list);
//		
//		return "admin/accounts/list";
//	}
//	
//	@GetMapping("search")
//	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
//		List<account> list = null;
//		if(StringUtils.hasText(name)) {
//			list = accountService.findByNameContaining(name);
//		}else {
//			list = accountService.findAll();
//		}
//		
//		model.addAttribute("accounts",list);
//		return "admin/accounts/search";
//	}
//	
//	@GetMapping("searchpaginated")
//	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name,
//			@RequestParam("page") Optional<Integer> page,
//			@RequestParam("size") Optional<Integer> size) {
//		int currentPage = page.orElse(1);
//		int pageSize = size.orElse(5);
//		
//		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("name"));
//		
//		
//		Page<account> resultPage = null;
//		
//		if(StringUtils.hasText(name)) {
//			resultPage = accountService.findByNameContaining(name,pageable);
//			model.addAttribute("name",name);
//		}else {
//			resultPage = accountService.findAll(pageable);
//		}
//		
//		int totalPages = resultPage.getTotalPages();
//		if(totalPages > 0) {
//			int start = Math.max(1, currentPage - 2);
//			int end = Math.min(currentPage + 2, totalPages);
//			
//			if(totalPages > 5) {
//				if(end == totalPages) start = end - 5;
//				else if (start == 1) end = start + 5;
//			}
//			
//			List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
//										.boxed().collect(Collectors.toList());
//			model.addAttribute("pageNumbers",pageNumbers);
//		}
//		model.addAttribute("accountPage", resultPage);
//
//		return "admin/accounts/searchpaginated";
//	}
}
