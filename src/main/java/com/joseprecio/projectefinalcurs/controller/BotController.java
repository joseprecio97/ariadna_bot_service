package com.joseprecio.projectefinalcurs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class BotController {

	@GetMapping("dashboard")
	private String dashboard() {
		System.out.println("Eiii");
		
		//Devolvemos la vista
		return "pages/index";
	}
	
}
