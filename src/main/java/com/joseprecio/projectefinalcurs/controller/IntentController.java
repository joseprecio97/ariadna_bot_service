package com.joseprecio.projectefinalcurs.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.joseprecio.projectefinalcurs.bot.BotConstants;
import com.joseprecio.projectefinalcurs.bot.Intent;
import com.joseprecio.projectefinalcurs.bot.Parameter;
import com.joseprecio.projectefinalcurs.model.IntentForm;
import com.joseprecio.projectefinalcurs.model.IntentPromptsForm;
import com.joseprecio.projectefinalcurs.model.ParameterForm;
import com.joseprecio.projectefinalcurs.model.ParameterPromptsForm;
import com.joseprecio.projectefinalcurs.model.RemoveIntentForm;
import com.joseprecio.projectefinalcurs.model.ScriptForm;
import com.joseprecio.projectefinalcurs.model.TrainingPhraseForm;
import com.joseprecio.projectefinalcurs.service.CommandService;

@Controller
@RequestMapping("/intents")
public class IntentController {

	@Autowired
	@Qualifier("CommandService")
	private CommandService commandServiceImpl;

	private String intentsModel(Model model, String message) {
		// Pasamos a la vista todos los intents que tiene el bot creados
		model.addAttribute("intents", commandServiceImpl.getIntents().values());

		// Obtenemos la localización
		Locale locale = LocaleContextHolder.getLocale();

		// Pasamos a la vista el idioma
		model.addAttribute("locale", locale.getLanguage());

		//Pasamos el mensaje a la vista
		if(message != null) {
			model.addAttribute(message, true);
		}
		
		// Devolvemos la vista
		return ControllerConstants.intentsView;
	}
	
	/**
	 * Muestra los intents creados
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("")
	public String showAll(Model model) {
		return intentsModel(model, null);
	}

	/**
	 * Crea la vista de creación de intent
	 * 
	 * @param model
	 * @param intent
	 */
	private String createModel(Model model, IntentForm intent, String message) {
		// Pasamos a la vista los lenguajes disponibles
		model.addAttribute("availableLanguages", commandServiceImpl.getAvailableLanguages());

		// Creamos un intent vacio
		model.addAttribute("intent", intent);

		// Obtenemos la localización
		Locale locale = LocaleContextHolder.getLocale();

		// Pasamos a la vista el idioma
		model.addAttribute("locale", locale.getLanguage());

		// Passamos el mensaje a la vista
		if (message != null) {
			model.addAttribute(message, true);
		}

		// Devolvemos la vista
		return ControllerConstants.createIntentView;
	}

	/**
	 * Muestra el formulario para crear un nuevo intent
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/create")
	public String create(Model model) {
		// Preparamos la vista
		return createModel(model, new IntentForm(), null);
	}

	/**
	 * Crea un nuevo intent
	 * 
	 * @param intent
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/create")
	public String create(@Valid @ModelAttribute("intent") IntentForm intent, BindingResult bindingResult, Model model)
			throws Exception {
		// Validamos los datos del formulario
		if (bindingResult.hasErrors()) {
			// Creamos la vista con errores
			return createModel(model, intent, null);
		}

		// Creamos un intent
		Intent newIntent = new Intent();

		// Binding de clase
		newIntent.setId(intent.getId());
		for (String language : intent.getLanguages()) {
			newIntent.addLanguage(language);
		}

		// Creamos el nuevo intent
		commandServiceImpl.newIntent(newIntent);

		// Redirigimos a la tabla con todos los intents
		return editModel(model, commandServiceImpl.getIntent(intent.getId()), new TrainingPhraseForm(),
				new ParameterForm(), null, "newintentcreated");
	}

	/**
	 * Crea el modelo para editar un intent
	 * 
	 * @param model
	 * @param intent
	 * @param trainingPhrases
	 * @param message
	 * @throws IOException
	 */
	private String editModel(Model model, Intent intent, TrainingPhraseForm trainingPhrase, ParameterForm parameter,
			ArrayList<String> validationErrors, String message) throws IOException {
		// Pasamos a la vista los lenguajes disponibles
		model.addAttribute("availableLanguages", commandServiceImpl.getAvailableLanguages());

		// Pasamos los prompts a la vista
		model.addAttribute("prompts", commandServiceImpl.getPrompts());

		// Pasamos a la vista el intent a editar
		model.addAttribute("intent", intent);

		// Pasamos las trainig phrases
		model.addAttribute("trainingPhrases", intent.getTrainingPhrases());

		// Pasamos el modelo de training phrase
		model.addAttribute("trainingphrase", trainingPhrase);

		// Obtenemos la localización
		Locale locale = LocaleContextHolder.getLocale();

		// Pasamos a la vista el idioma
		model.addAttribute("locale", locale.getLanguage());

		// Pasamos el script del intent a la vista
		model.addAttribute("script", intent.getScript());

		// Pasamos el parámetro a la vista
		model.addAttribute("parameter", parameter);

		// Pasamos a la vista los parámetros
		model.addAttribute("parameters", intent.getParameters().values());

		// Pasamos los errores de validación del formulario a la vista
		if (validationErrors != null) {
			model.addAttribute("validationerrors", validationErrors);
		}

		// Pasamos el mensaje a la vista
		if (message != null) {
			model.addAttribute(message, true);
		}

		// Devolvemos la vista
		return ControllerConstants.editIntentView;
	}

	/**
	 * Edita un intent
	 * 
	 * @param intent
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/{id}/edit")
	public String edit(@PathVariable(required = true) String id, @Valid @ModelAttribute("intent") IntentForm intent,
			BindingResult bindingResult, Model model) throws Exception {

		// Validamos los datos del formulario
		if (bindingResult.hasErrors()) {
			// Obtenemos los errores de validación
			ArrayList<String> validationErrors = new ArrayList<String>();

			for (ObjectError error : bindingResult.getAllErrors()) {
				validationErrors.add(((FieldError) error).getField() + ": " + error.getDefaultMessage());
			}

			// Devolvemos la vista
			return editModel(model, commandServiceImpl.getIntent(intent.getId()), new TrainingPhraseForm(),
					new ParameterForm(), validationErrors, null);
		}

		// Creamos un intent
		Intent editIntent = commandServiceImpl.getIntent(intent.getId());

		// Binding de clase
		editIntent.setLanguages((ArrayList<String>) intent.getLanguages());

		// Editamos el intent
		commandServiceImpl.saveIntent(editIntent);

		// Redirigimos a la tabla con todos los intents
		return editModel(model, editIntent, new TrainingPhraseForm(), new ParameterForm(), null, "intentedited");
	}

	/**
	 * Muestra el formulario para editar el Intent
	 * 
	 * @param id
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(required = true) String id, Model model) throws IOException {
		// Devolvemos el modelo para editar un intent
		return editModel(model, commandServiceImpl.getIntent(id), new TrainingPhraseForm(), new ParameterForm(), null,
				null);
	}

	@GetMapping("{id}/addtrainingphrase")
	public String addtrainingphrase(@PathVariable(required = true) String id, Model model) throws IOException {
		// Devolvemos la vista
		return editModel(model, commandServiceImpl.getIntent(id), new TrainingPhraseForm(), new ParameterForm(), null,
				null);
	}

	/**
	 * Añade una frase de entrenamiento a un intent
	 * 
	 * @param id
	 * @param trainingPhrase
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@PostMapping("{id}/addtrainingphrase")
	public String addtrainingphrase(@PathVariable(required = true) String id,
			@Valid @ModelAttribute("trainingphrase") TrainingPhraseForm trainingPhrase, BindingResult bindingResult,
			Model model) throws IOException {

		// Validamos los datos del formulario
		if (bindingResult.hasErrors()) {
			// Obtenemos los errores de validación
			ArrayList<String> validationErrors = new ArrayList<String>();

			for (ObjectError error : bindingResult.getAllErrors()) {
				validationErrors.add(((FieldError) error).getField() + ": " + error.getDefaultMessage());
			}

			// Devolvemos la vista
			return editModel(model, commandServiceImpl.getIntent(trainingPhrase.getId()), trainingPhrase,
					new ParameterForm(), validationErrors, null);
		}

		// Añadimos la frase de entrenamiento al intent
		Intent intent = commandServiceImpl.getIntent(trainingPhrase.getId());
		intent.addTrainingPhrase(trainingPhrase.getLanguage(), trainingPhrase.getPhrase());

		// Devolvemos la vista
		return editModel(model, intent, new TrainingPhraseForm(), new ParameterForm(), null, "trainingphraseadded");
	}

	@GetMapping("{id}/removetrainingphrase")
	public String removetrainingphrase(@PathVariable(required = true) String id, Model model) throws IOException {
		// Devolvemos la vista
		return editModel(model, commandServiceImpl.getIntent(id), new TrainingPhraseForm(), new ParameterForm(), null,
				null);
	}

	/**
	 * Elimina una frase de entrenamiento de un intent
	 * 
	 * @param id
	 * @param trainingPhrase
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping("{id}/removetrainingphrase")
	public String removetrainingphrase(@PathVariable(required = true) String id,
			@Valid @ModelAttribute("trainingphrase") TrainingPhraseForm trainingPhrase, BindingResult bindingResult,
			Model model) throws Exception {

		// Validamos los datos del formulario
		if (bindingResult.hasErrors()) {
			throw new Exception();
		}

		// Añadimos la frase de entrenamiento al intent
		Intent intent = commandServiceImpl.getIntent(trainingPhrase.getId());
		intent.removeTrainingPhrase(trainingPhrase.getLanguage(), trainingPhrase.getPhrase());

		// Devolvemos la vista
		return editModel(model, intent, new TrainingPhraseForm(), new ParameterForm(), null, "trainingphraseremoved");
	}

	@GetMapping("{id}/savescript")
	public String savescript(@PathVariable(required = true) String id, Model model) throws IOException {
		// Devolvemos la vista
		return editModel(model, commandServiceImpl.getIntent(id), new TrainingPhraseForm(), new ParameterForm(), null,
				null);
	}

	/**
	 * Edita el script de un intent
	 * 
	 * @param id
	 * @param script
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping("{id}/savescript")
	public String savescript(@PathVariable(required = true) String id,
			@Valid @ModelAttribute("scriptform") ScriptForm script, BindingResult bindingResult, Model model)
			throws Exception {

		// Validamos los datos del formulario
		if (bindingResult.hasErrors()) {
			throw new Exception();
		}

		// Editamos el script del intent
		Intent intent = commandServiceImpl.getIntent(script.getId());
		intent.editScript(script.getCode());

		// Devolvemos la vista
		return editModel(model, intent, new TrainingPhraseForm(), new ParameterForm(), null, "scriptedited");

	}

	@GetMapping("{id}/addparameter")
	public String addparameter(@PathVariable(required = true) String id, Model model) throws IOException {
		// Devolvemos la vista
		return editModel(model, commandServiceImpl.getIntent(id), new TrainingPhraseForm(), new ParameterForm(), null,
				null);
	}

	/**
	 * Añade un parámetro a un intent
	 * 
	 * @param id
	 * @param parameter
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping("{id}/addparameter")
	public String addparameter(@PathVariable(required = true) String id,
			@Valid @ModelAttribute("parameter") ParameterForm parameter, BindingResult bindingResult, Model model)
			throws Exception {

		// Validamos los datos del formulario
		if (bindingResult.hasErrors()) {
			// Obtenemos los errores de validación
			ArrayList<String> validationErrors = new ArrayList<String>();

			for (ObjectError error : bindingResult.getAllErrors()) {
				validationErrors.add(((FieldError) error).getField() + ": " + error.getDefaultMessage());
			}

			// Devolvemos la vista
			return editModel(model, commandServiceImpl.getIntent(parameter.getId()), new TrainingPhraseForm(),
					parameter, validationErrors, null);
		}

		// Creamos el parametro
		Parameter newParameter = new Parameter();

		newParameter.setName(parameter.getName());
		newParameter.setRequired(parameter.isRequired());
		newParameter.setType(parameter.getType());
		newParameter.setList(parameter.isList());

		// Inizializmos el valor del parametro
		if (!newParameter.isList()) {
			// EL PARAMETRO NO ES UNA LISTA
			newParameter.setValue(null);
		} else {
			// EL PARAMETRO ES UNA LISTA
			if (newParameter.getType().equals("String")) {
				newParameter.setValue(new ArrayList<String>());
			} else if (newParameter.getType().equals("Integer")) {
				newParameter.setValue(new ArrayList<Integer>());
			}
		}

		// Añadimos el parametro al intent
		Intent intent = commandServiceImpl.getIntent(parameter.getId());
		intent.addParameter(newParameter);

		// Guardamos el intent
		commandServiceImpl.saveIntent(intent);

		// Recorremos todos los lenguajes
		for (String language : BotConstants.BOT_AVAILABLE_LANGUAGE) {
			// Obtenemos los prompts del bot
			HashMap<String, Properties> languagePrompts = commandServiceImpl.getPrompts();
			Properties prompts = languagePrompts.get(language);

			// Añadimos los prompts del parametro que estamos creando
			prompts.put(parameter.getId() + "_" + parameter.getName(), "");
			prompts.put(parameter.getId() + "_" + parameter.getName() + "_bad_value", "");
			prompts.put(parameter.getId() + "_" + parameter.getName() + "_invalid", "");
		}

		// Guardamos los prompts en disco
		commandServiceImpl.savePrompts();

		// Devolvemos la vista
		return editModel(model, intent, new TrainingPhraseForm(), new ParameterForm(), null, "parameteradded");
	}

	@GetMapping("{id}/removeparameter")
	public String removeparameter(@PathVariable(required = true) String id, Model model) throws Exception {
		// Devolvemos la vista
		return editModel(model, commandServiceImpl.getIntent(id), new TrainingPhraseForm(), new ParameterForm(), null,
				null);
	}

	/**
	 * Elimina un parámetro
	 * 
	 * @param id
	 * @param parameter
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping("{id}/removeparameter")
	public String removeparameter(@PathVariable(required = true) String id,
			@Valid @ModelAttribute("parameter") ParameterForm parameter, BindingResult bindingResult, Model model)
			throws Exception {

		// Validamos el modelo recibido
		if (bindingResult.hasErrors()) {
			throw new Exception();
		}

		// Añadimos el parametro al intent
		Intent intent = commandServiceImpl.getIntent(parameter.getId());
		intent.removeParameter(parameter.getName());

		// Guardamos el intent
		commandServiceImpl.saveIntent(intent);

		// Recorremos todos los lenguajes
		for (String language : BotConstants.BOT_AVAILABLE_LANGUAGE) {
			// Obtenemos los prompts del bot
			HashMap<String, Properties> languagePrompts = commandServiceImpl.getPrompts();
			Properties prompts = languagePrompts.get(language);

			// Añadimos los prompts del parametro que estamos creando
			prompts.remove(parameter.getId() + "_" + parameter.getName());
			prompts.remove(parameter.getId() + "_" + parameter.getName() + "_bad_value");
			prompts.remove(parameter.getId() + "_" + parameter.getName() + "_invalid");
		}

		// Guardamos los prompts en disco
		commandServiceImpl.savePrompts();

		// Devolvemos la vista
		return editModel(model, intent, new TrainingPhraseForm(), new ParameterForm(), null, "parameterremoved");
	}

	@GetMapping("{id}/saveprompt")
	public String saveprompt(@PathVariable(required = true) String id, Model model) throws IOException {
		// Devolvemos la vista
		return editModel(model, commandServiceImpl.getIntent(id), new TrainingPhraseForm(), new ParameterForm(), null,
				null);
	}

	/**
	 * Guarda los prompts del intent
	 * 
	 * @param id
	 * @param intentPrompts
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping("{id}/saveprompt")
	public String saveprompt(@PathVariable(required = true) String id,
			@Valid @ModelAttribute IntentPromptsForm intentPrompts, BindingResult bindingResult, Model model)
			throws Exception {

		// Comprovamos si el modelo es valido
		if (bindingResult.hasErrors()) {
			throw new Exception();
		}

		// Obtenemos los prompts del bot
		HashMap<String, Properties> languagePrompts = commandServiceImpl.getPrompts();
		Properties prompts = languagePrompts.get(intentPrompts.getLanguage());

		// Actualizamos los prompts
		prompts.put(intentPrompts.getId() + "_error", intentPrompts.getPrompterror());
		prompts.put(intentPrompts.getId() + "_new_conversation_error", intentPrompts.getPromptnewconversationerror());
		prompts.put(intentPrompts.getId() + "_end_conversation_error", intentPrompts.getPromptendconversationerror());
		prompts.put(intentPrompts.getId() + "_final_message", intentPrompts.getPromptfinalmessage());
		prompts.put(intentPrompts.getId() + "_cancel", intentPrompts.getPromptcancel());

		// Guardamos los prompts en disco
		commandServiceImpl.savePrompts();

		// Devolvemos la vista
		return editModel(model, commandServiceImpl.getIntent(id), new TrainingPhraseForm(), new ParameterForm(), null,
				"promptssaved");
	}

	@GetMapping("{id}/saveparameterprompt")
	public String saveparameterprompt(@PathVariable(required = true) String id, Model model) throws Exception {
		// Devolvemos la vista
		return editModel(model, commandServiceImpl.getIntent(id), new TrainingPhraseForm(), new ParameterForm(), null,
				null);
	}
	
	/**
	 * Guarda los prompts del parámetro
	 * 
	 * @param id
	 * @param parameterPrompts
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping("{id}/saveparameterprompt")
	public String saveparameterprompt(@PathVariable(required = true) String id,
			@Valid @ModelAttribute ParameterPromptsForm parameterPrompts, BindingResult bindingResult, Model model)
			throws Exception {

		// Comprovamos si el modelo es valido
		if (bindingResult.hasErrors()) {
			throw new Exception();
		}

		// Obtenemos los prompts del bot
		HashMap<String, Properties> languagePrompts = commandServiceImpl.getPrompts();
		Properties prompts = languagePrompts.get(parameterPrompts.getLanguage());

		// Actualizamos los prompts
		prompts.put(parameterPrompts.getId() + "_" + parameterPrompts.getParametername(), 
				parameterPrompts.getPromptparameter());
		prompts.put(parameterPrompts.getId() + "_" + parameterPrompts.getParametername() + "_bad_value", 
				parameterPrompts.getPromptbadvalue());
		prompts.put(parameterPrompts.getId() + "_" + parameterPrompts.getParametername() + "_invalid", 
				parameterPrompts.getPromptinvalid());

		// Guardamos los prompts en disco
		commandServiceImpl.savePrompts();

		// Devolvemos la vista
		return editModel(model, commandServiceImpl.getIntent(id), new TrainingPhraseForm(), new ParameterForm(), null,
				"promptssaved");
	}
	
	@GetMapping("{id}/removeintent")
	public String removeintent(@PathVariable(required = true) String id, Model model) {
		//Devolvemos la vista
		return intentsModel(model, null);
	}
	
	/**
	 * Elimina un intent
	 * 
	 * @param id
	 * @param intentRemove
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping("{id}/removeintent")
	public String removeintent(@PathVariable(required = true) String id,
			@Valid @ModelAttribute RemoveIntentForm intentRemove, BindingResult bindingResult, Model model) throws Exception {
		
		//Validamos el modelo del formulario
		if(bindingResult.hasErrors()) {
			throw new Exception();
		}
		
		//Eliminamos el script
		File scriptFile = new File(BotConstants.BOT_CONFIG_FOLDER + BotConstants.BOT_SCRIPTS_FOLDER
				+ "\\" + intentRemove.getId() + ".js");
		scriptFile.delete();
		
		//Recorremos todos los lenguajes
		for(String language : BotConstants.BOT_AVAILABLE_LANGUAGE) {
			//Eliminamos el fichero de entrenamiento
			File trainingFile = new File(BotConstants.BOT_CONFIG_FOLDER + BotConstants.BOT_TRAINING_FOLDER
					+ "\\" + language + "\\" + language + "-" + intentRemove.getId() + ".txt");
			trainingFile.delete();
			
			// Obtenemos los prompts del bot
			HashMap<String, Properties> languagePrompts = commandServiceImpl.getPrompts();
			Properties prompts = languagePrompts.get(language);

			// Eliminamos los prompts
			prompts.remove(intentRemove.getId() + "_error");
			prompts.remove(intentRemove.getId() + "_new_conversation_error");
			prompts.remove(intentRemove.getId() + "_end_conversation_error");
			prompts.remove(intentRemove.getId() + "_final_message");
			prompts.remove(intentRemove.getId() + "_cancel");
			
			//Recorremos todos los parametros del intent
			for(Parameter parameter : commandServiceImpl.getIntent(intentRemove.getId()).getParameters().values()) {
				//Eliminamos los prompts
				prompts.remove(intentRemove.getId() + "_" + parameter.getName());
				prompts.remove(intentRemove.getId() + "_" + parameter.getName() + "_bad_value");
				prompts.remove(intentRemove.getId() + "_" + parameter.getName() + "_invalid");
			}
		}
		
		//Guardamos los prompts en disco
		commandServiceImpl.savePrompts();
		
		//Eliminamos el intent
		commandServiceImpl.removeIntent(commandServiceImpl.getIntent(intentRemove.getId()));
		
		//Devolvemos la vista
		return intentsModel(model, "intentremoved");
	}
	
}
