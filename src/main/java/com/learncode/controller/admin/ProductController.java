package com.learncode.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
import com.learncode.domain.Product;
import com.learncode.model.CategoryDto;
import com.learncode.model.ProductDto;
import com.learncode.service.CategoryService;
import com.learncode.service.ProductService;

import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;


@Controller
@RequestMapping("admin/products")
public class ProductController {
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ProductService productService;
	
	@ModelAttribute("categories")
	public List<CategoryDto> getCategories(){
		return categoryService.findAll().stream().map(item->{
			CategoryDto dto = new CategoryDto();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).toList();
	}
	
	@GetMapping("add")
	public String add(Model model) {
		model.addAttribute("product",new ProductDto());
		return "admin/products/addOrEdit";
	}
	
	@GetMapping("edit/{productId}")
	public ModelAndView edit(ModelMap model, @PathVariable("productId") Long productId) {
		Optional<Category> otp = categoryService.findById(productId);
		CategoryDto dto = new CategoryDto();
		if(otp.isPresent()) {
			Category entity = otp.get();
			BeanUtils.copyProperties(entity, dto);
			dto.setIsEdit(true);
			model.addAttribute("category",dto);
			return new ModelAndView("admin/products/addOrEdit");
		}
		
		model.addAttribute("message", "Category is not existed");
		return new ModelAndView("redirect:/admin/products");
}
	
	@GetMapping("delete/{productId}")
	public ModelAndView delete(ModelMap model, @PathVariable("productId") Long productId) {
		categoryService.deleteById(productId);
		model.addAttribute("message","Delete successfully !!!");
		return new ModelAndView("forward:/admin/products/search",model);
	}
	
	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model
			,@Valid @ModelAttribute("product") ProductDto dto
			,BindingResult result) {
		if(result.hasErrors()) {
			return new ModelAndView("admin/products/addOrEdit");
		}
		
		Product entity = new Product();
		
		BeanUtils.copyProperties(dto, entity);
		
		Category category = new Category();
		category.setCategoryId(dto.getCategoryId());
//		if(!dto.getImageFile().isEmpty()) {
//			UUID uuid = UUID.randomUUID();
//			String uuString = uuid.toString();
//			entity.setImage(st);
//		}
		productService.save(entity);
		
		model.addAttribute("message","Category is saved");
		
		return new ModelAndView("forward:/admin/products",model);
	}
	
	@RequestMapping("")
	public String list(ModelMap model) {
		List<Category> list = categoryService.findAll();
		
		model.addAttribute("products",list);
		
		return "admin/products/list";
	}
	
	@GetMapping("search")
	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
		List<Category> list = null;
		if(StringUtils.hasText(name)) {
			list = categoryService.findByNameContaining(name);
		}else {
			list = categoryService.findAll();
		}
		
		model.addAttribute("products",list);
		return "admin/products/search";
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

		return "admin/products/searchpaginated";
	}
}
