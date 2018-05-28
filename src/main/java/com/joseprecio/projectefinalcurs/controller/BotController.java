package com.joseprecio.projectefinalcurs.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.joseprecio.projectefinalcurs.service.CommandService;

@Controller
@RequestMapping("/")
public class BotController {

	@Autowired
	@Qualifier("CommandService")
	private CommandService commandServiceImpl;
	
	@Value("${ariadnabot.url}")
	private String ariadnaurl;
	
	/**
	 * Desplega el nuevo bot en producción
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("deploy")
	private String deploy(Model model) {
		//Obtenemos la localización
		Locale locale = LocaleContextHolder.getLocale();
				
		//Pasamos a la vista el idioma
		model.addAttribute("locale", locale.getLanguage());
		model.addAttribute("ariadnaurl", ariadnaurl);
		
		try {
			//Desplegamos el Bot
			commandServiceImpl.deploy();
			model.addAttribute("deploysuccess", true);
		}catch(Exception e) {
			model.addAttribute("deployerror", true);
			model.addAttribute("deployerrormessage", e.getMessage());
		}
		
		//Devolvemos la vista
		return ControllerConstants.indexView;
	}
	
	/**
	 * Entrena el Bot
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("train")
	private String train(Model model) {
		//Obtenemos la localización
		Locale locale = LocaleContextHolder.getLocale();
				
		//Pasamos a la vista el idioma
		model.addAttribute("locale", locale.getLanguage());
		model.addAttribute("ariadnaurl", ariadnaurl);
		
		try {
			//Entrenamos el bot de desarrollo
			commandServiceImpl.train();
			model.addAttribute("trainsucces", true);
		}catch(Exception e) {
			model.addAttribute("trainerror", true);
			model.addAttribute("trainerrormessage", e.getMessage());
		}
		
		//Devolvemos la vista
		return ControllerConstants.indexView;
	}
	
	@GetMapping("chat")
	private String chat(Model model) {
		//Obtenemos la localización
		Locale locale = LocaleContextHolder.getLocale();
		
		//Pasamos a la vista el idioma
		model.addAttribute("locale", locale.getLanguage());
		model.addAttribute("ariadnaurl", ariadnaurl);
		model.addAttribute("chat", true);
		
		//Devolvemos la vista
		return ControllerConstants.chatView;
	}
	
	@GetMapping("dashboard")
	private String dashboard(Model model) {
		//Obtenemos la localización
		Locale locale = LocaleContextHolder.getLocale();
		
		//Pasamos a la vista el idioma
		model.addAttribute("locale", locale.getLanguage());
		model.addAttribute("ariadnaurl", ariadnaurl);
		
		//Devolvemos la vista
		return ControllerConstants.indexView;
	}
	
}
