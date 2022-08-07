package com.learncode.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

import com.learncode.domain.Category;
import com.learncode.model.CategoryDto;
import com.learncode.service.CategoryService;

import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;


@Controller
@RequestMapping("admin/categories")
public class CategoryController {
	@Autowired
	CategoryService categoryService;
	
	@GetMapping("add")
	public String add(Model model) {
		model.addAttribute("category",new CategoryDto());
		return "admin/categories/addOrEdit";
	}
	
	@GetMapping("edit/{categoryId}")
	public ModelAndView edit(ModelMap model, @PathVariable("categoryId") Long categoryId) {
		Optional<Category> otp = categoryService.findById(categoryId);
		CategoryDto dto = new CategoryDto();
		if(otp.isPresent()) {
			Category entity = otp.get();
			BeanUtils.copyProperties(entity, dto);
			dto.setIsEdit(true);
			model.addAttribute("category",dto);
			return new ModelAndView("admin/categories/addOrEdit");
		}
		
		model.addAttribute("message", "Category is not existed");
		return new ModelAndView("redirect:/admin/categories");
}
	
	@GetMapping("delete/{categoryId}")
	public ModelAndView delete(ModelMap model, @PathVariable("categoryId") Long categoryId) {
		categoryService.deleteById(categoryId);
		model.addAttribute("message","Delete successfully !!!");
		return new ModelAndView("forward:/admin/categories/search",model);
	}
	
	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model
			,@Valid @ModelAttribute("category") CategoryDto dto
			,BindingResult result) {
		if(result.hasErrors()) {
			return new ModelAndView("admin/categories/addOrEdit");
		}
		
		Category entity = new Category();
		
		BeanUtils.copyProperties(dto, entity);
		
		categoryService.save(entity);
		
		model.addAttribute("message","Category is saved");
		
		return new ModelAndView("forward:/admin/categories",model);
	}
	
	@RequestMapping("")
	public String list(ModelMap model) {
		List<Category> list = categoryService.findAll();
		
		model.addAttribute("categories",list);
		
		return "admin/categories/list";
	}
	
	@GetMapping("search")
	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
		List<Category> list = null;
		if(StringUtils.hasText(name)) {
			list = categoryService.findByNameContaining(name);
		}else {
			list = categoryService.findAll();
		}
		
		model.addAttribute("categories",list);
		return "admin/categories/search";
	}
	
	@GetMapping("searchpaginated")
	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(5);
		
		Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by("name"));
		
		
		Page<Category> resultPage = null;
		
		if(StringUtils.hasText(name)) {
			resultPage = categoryService.findByNameContaining(name,pageable);
			model.addAttribute("name",name);
		}else {
			resultPage = categoryService.findAll(pageable);
		}
		
		int totalPages = resultPage.getTotalPages();
		if(totalPages > 0) {
			int start = Math.max(1, currentPage - 2);
			int end = Math.min(currentPage + 2, totalPages);
			
			if(totalPages > 5) {
				if(end == totalPages) start = end - 5;
				else if (start == 1) end = start + 5;
			}
			
			List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
										.boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers",pageNumbers);
		}
		model.addAttribute("categoryPage", resultPage);

		return "admin/categories/searchpaginated";
	}
}
