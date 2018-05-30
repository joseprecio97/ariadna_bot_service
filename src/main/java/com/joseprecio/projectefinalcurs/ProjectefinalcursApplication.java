package com.joseprecio.projectefinalcurs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.security.acl.Permission;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Locale.Category;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.joseprecio.projectefinalcurs.bot.Bot;
import com.joseprecio.projectefinalcurs.bot.BotConstants;

@SpringBootApplication
@EnableAutoConfiguration
public class ProjectefinalcursApplication extends WebMvcConfigurerAdapter {

	/**
	 * Resuelve la ubicación de los textos de la web
	 * 
	 * @return
	 */
	@Bean
	public MessageSource messageSource() {
		final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:/messages");
		messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setDefaultEncoding("UTF-8");
		return messageSource;
	}

	/**
	 * Resuelve el lenguaje
	 * 
	 * @return
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.forLanguageTag("es"));
		return localeResolver;
	}

	/**
	 * Captura de la url el lenguaje en que queremos visualizar la web
	 * 
	 * @return
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	/**
	 * Añade un interceptor para obtener el lenguaje en que se quiere consultar la
	 * página
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	/**
	 * Devuelve una nueva instancia de Bot
	 * 
	 * @return Bot
	 */
	@Bean(name = "bot")
	public Bot getBot() {
		return new Bot();
	}

	@Bean(name = "bot_production")
	public Bot getBot_production() {
		return new Bot();
	}

	public static void main(String[] args) {
		// Obtenemos los mensajes de log
		ResourceBundle loggerResources = ResourceBundle.getBundle(ApplicationConstants.TEXTS_FILENAME,
				Locale.getDefault(Category.DISPLAY));

		// Iniciamos la aplicación spring
		ApplicationContext context = SpringApplication.run(ProjectefinalcursApplication.class, args);

		// Creamos los bots
		Bot bot = (Bot) context.getBean("bot");
		Bot bot_production = (Bot) context.getBean("bot_production");

		// Establecemos el Bot de producción y el de desarrollo
		bot.setProduction(false);
		bot_production.setProduction(true);

		/*ENTRENAMOS EL BOT DE DESARROLLO*/
		try {			
			// Inicializamos el bot
			System.out.println(new Timestamp(System.currentTimeMillis()) + " :"
					+ MessageFormat.format(loggerResources.getString("msg_init_bot_intentTraining_load"), "DEV"));
			bot.init();
			System.out.println(new Timestamp(System.currentTimeMillis()) + " :"
					+ MessageFormat.format(loggerResources.getString("msg_end_bot_intentTraining_load"), "DEV"));
		}catch(Exception e) {
			//Hay un error en el bot
			bot.setError(true);
			//Pintamos la excepción
			e.printStackTrace();
		}

		/*ENTRENAMOS EL BOT PRODUCTIVO*/
		try {
			// Inicializamos el bot
			System.out.println(new Timestamp(System.currentTimeMillis()) + " :"
					+ MessageFormat.format(loggerResources.getString("msg_init_bot_intentTraining_load"), "PROD"));
			bot_production.init();
			System.out.println(new Timestamp(System.currentTimeMillis()) + " :"
					+ MessageFormat.format(loggerResources.getString("msg_end_bot_intentTraining_load"), "PROD"));
		}catch(Exception e) {
			//Marcamos un error en el bot
			bot_production.setError(true);
			//Pintamos la excepcion
			e.printStackTrace();
		}
	}
}
