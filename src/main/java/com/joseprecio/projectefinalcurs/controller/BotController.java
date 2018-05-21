package com.joseprecio.projectefinalcurs.controller;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class BotController {

	@GetMapping("dashboard")
	private String dashboard(Model model) {
		//Obtenemos la localización
		Locale locale = LocaleContextHolder.getLocale();
		
		//Pasamos a la vista el idioma
		model.addAttribute("locale", locale.getLanguage());
		
		//Devolvemos la vista
		return ControllerConstants.indexView;
	}
	
}
