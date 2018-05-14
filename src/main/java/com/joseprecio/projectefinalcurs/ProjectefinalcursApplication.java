package com.joseprecio.projectefinalcurs;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.ResourceBundle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.joseprecio.projectefinalcurs.bot.Bot;

@SpringBootApplication
public class ProjectefinalcursApplication {

	/**
	 * Creamos el Bot de la aplicación
	 * 
	 * @return Bot
	 */
	@Bean
	public Bot getBot() {
		return new Bot();
	}

	public static void main(String[] args) {
		ResourceBundle loggerResources = ResourceBundle.getBundle(ApplicationConstants.TEXTS_FILENAME, Locale.getDefault(Category.DISPLAY));
		
		//Iniciamos la aplicación spring
		ApplicationContext context = SpringApplication.run(ProjectefinalcursApplication.class, args);

		//Obtenemos el bot del contexto
		Bot bot = context.getBean(Bot.class);
		
		try {			
			//Inicializamos el bot
			System.out.println(new Timestamp(System.currentTimeMillis()) + " :" + loggerResources.getString("msg_init_bot_intentTraining_load"));
			bot.init();
			System.out.println(new Timestamp(System.currentTimeMillis()) + " :" + loggerResources.getString("msg_end_bot_intentTraining_load"));
		} catch (Exception e) {
			//Mostramos la traza de error
			e.printStackTrace();
			
			//Cerramos la aplicación
			int exitValue = SpringApplication.exit(context);
			System.exit(exitValue);
		}
	}
}
