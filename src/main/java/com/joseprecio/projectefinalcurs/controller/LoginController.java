package com.joseprecio.projectefinalcurs.controller;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("")
public class LoginController {

	/**
	 * Muestra el formulario de login
	 * 
	 * @return
	 */
	@GetMapping("/login")
	public String login(@RequestParam(required=false, name="error") String error, @RequestParam(required=false, name="logout") String logout, 
			Model model) {
		
		//Pasamos a la vista si hay un error de login
		if(error != null) {
			model.addAttribute("loginerror", true);
		}
		
		//Pasamos a la vista si hemos salido de la web
		if(logout != null) {
			model.addAttribute("logout", true);
		}
		
		//Devolvemos la vista
		return ControllerConstants.loginView;
	}
	
	/**
	 * Login correcto
	 * 
	 * @return
	 */
	@PostMapping("/loginok")
	public String loginok() {
		// Obtenemos la localización
		Locale locale = LocaleContextHolder.getLocale();
		
		return "redirect:/dashboard?lang="+locale.getLanguage();
	}
	
}
