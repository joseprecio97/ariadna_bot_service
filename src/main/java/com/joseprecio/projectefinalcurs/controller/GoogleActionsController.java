package com.joseprecio.projectefinalcurs.controller;

import java.util.ArrayList;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.joseprecio.projectefinalcurs.model.GAUpdateModel;
import com.joseprecio.projectefinalcurs.model.LanguagesForm;
import com.joseprecio.projectefinalcurs.model.ProjectIdForm;
import com.joseprecio.projectefinalcurs.model.WelcomeIntentForm;
import com.joseprecio.projectefinalcurs.model.googleactionssdk.GoogleActionsSDKResponse;
import com.joseprecio.projectefinalcurs.model.googleactionssdk.GoogleActionsSdkInformation;
import com.joseprecio.projectefinalcurs.service.CommandService;
import com.joseprecio.projectefinalcurs.service.GoogleActionsService;

@Controller
@RequestMapping("/googleactions")
public class GoogleActionsController {

	@Autowired
	private CommandService commandServiceImpl;

	@Autowired
	private GoogleActionsService googleActionsServiceImpl;

	/**
	 * /*Devuelve la vista de Google Actions /
	 * 
	 * @throws Exception
	 * *
	 */
	private String createGaupdate(String sdkresponse, int sdkcode, ArrayList<String> validationErrors, String message,
			Model model) throws Exception {

		// Obtenemos la información de google actions
		GoogleActionsSdkInformation GAInfo = googleActionsServiceImpl.getInfo();

		LanguagesForm galanguages = new LanguagesForm();
		galanguages.setLanguages(GAInfo.getLanguages());

		// Pasamos a la vista los intents disponibles
		model.addAttribute("intents", commandServiceImpl.getIntents().values());

		// Pasamos a la vista la información de google actions
		model.addAttribute("gainfo", GAInfo);

		// Pasamos a la vista los lenguajes integrados
		model.addAttribute("galanguages", galanguages);

		// Pasamos a la vista los lenguajes disponibles
		model.addAttribute("availableLanguages", commandServiceImpl.getAvailableLanguages());

		// Pasamos a la vista el mensaje
		if (message != null) {
			model.addAttribute(message, true);
		}

		// Obtenemos el id de proyecto
		model.addAttribute("projectid", GAInfo.getProjectId());

		// Iniciamos la variable sdkresponse
		model.addAttribute("sdkresponse", sdkresponse);
		model.addAttribute("sdkcode", sdkcode);

		// Obtenemos la localización
		Locale locale = LocaleContextHolder.getLocale();

		// Pasamos a la vista el idioma
		model.addAttribute("locale", locale.getLanguage());

		// Pasamos los errores de validación del formulario a la vista
		if (validationErrors != null) {
			model.addAttribute("validationerrors", validationErrors);
		}

		// Devolvemos la vista
		return ControllerConstants.gaUpdateView;
	}

	@GetMapping("/update")
	public String update(Model model) throws Exception {
		// Devolvemos la vista
		return createGaupdate("", 3, null, null, model);
	}

	/**
	 * Actualiza el proyecto en google actions
	 * 
	 * @param updateModel
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/update")
	public String update(@Valid @ModelAttribute GAUpdateModel updateModel, BindingResult bindingResult, Model model)
			throws Exception {

		// Validamos que el modelo no tenga errores
		if (bindingResult.hasErrors()) {
			// Obtenemos los errores de validación
			ArrayList<String> validationErrors = new ArrayList<String>();

			for (ObjectError error : bindingResult.getAllErrors()) {
				validationErrors.add(((FieldError) error).getField() + ": " + error.getDefaultMessage());
			}

			// Devolvemos la vista
			return createGaupdate("", 3, validationErrors, null, model);
		}

		try {
			// Actualizamos el proyecto en google actions
			GoogleActionsSDKResponse sdkResponse = googleActionsServiceImpl.updateGoogleActions(updateModel.getToken());

			// Devolvemos la vista
			return createGaupdate(sdkResponse.getConsoleMessage(), sdkResponse.getCode(), null, null, model);
		} catch (Exception e) {
			// Devolvemos la vista
			return createGaupdate(e.getMessage(), 1, null, null, model);
		}
	}

	@GetMapping("/saveprojectid")
	public String saveprojectid(Model model) throws Exception {
		// Devolvemos la vista
		return createGaupdate("", 3, null, null, model);
	}

	/**
	 * /*Actualiza el projectid /
	 * 
	 * @throws Exception
	 * *
	 */
	@PostMapping("/saveprojectid")
	public String saveprojectid(@Valid @ModelAttribute ProjectIdForm projectId, BindingResult bindingResult,
			Model model) throws Exception {

		// Validamos que el modelo no tenga errores
		if (bindingResult.hasErrors()) {
			// Obtenemos los errores de validación
			ArrayList<String> validationErrors = new ArrayList<String>();

			for (ObjectError error : bindingResult.getAllErrors()) {
				validationErrors.add(((FieldError) error).getField() + ": " + error.getDefaultMessage());
			}

			// Devolvemos la vista
			return createGaupdate("", 3, validationErrors, null, model);
		}

		// Obtenemos la información de google actions
		GoogleActionsSdkInformation GAInfo = googleActionsServiceImpl.getInfo();

		// Actualizamos la información
		GAInfo.setProjectId(projectId.getProjectid());
		googleActionsServiceImpl.setInfo(GAInfo);

		// Devolvemos la vista
		return createGaupdate("", 3, null, "projectidedited", model);

	}

	@GetMapping("/savelanguages")
	public String savelanguages(Model model) throws Exception {
		// Devolvemos la vista
		return createGaupdate("", 3, null, null, model);
	}

	/**
	 * Guarda los lenguajes a integrar
	 * 
	 * @param languagesForm
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/savelanguages")
	public String savelanguages(@Valid @ModelAttribute LanguagesForm languagesForm, BindingResult bindingResult,
			Model model) throws Exception {

		// Validamos que el modelo no tenga errores
		if (bindingResult.hasErrors()) {
			// Obtenemos los errores de validación
			ArrayList<String> validationErrors = new ArrayList<String>();

			for (ObjectError error : bindingResult.getAllErrors()) {
				validationErrors.add(((FieldError) error).getField() + ": " + error.getDefaultMessage());
			}

			// Devolvemos la vista
			return createGaupdate("", 3, validationErrors, null, model);
		}

		// Obtenemos la información de google actions
		GoogleActionsSdkInformation GAInfo = googleActionsServiceImpl.getInfo();

		// Actualizamos la información
		GAInfo.setLanguages(languagesForm.getLanguages());
		googleActionsServiceImpl.setInfo(GAInfo);

		// Devolvemos la vista
		return createGaupdate("", 3, null, "languagesedited", model);

	}

	@GetMapping("/savewelcomeintent")
	public String addwelcomephrase(Model model) throws Exception {
		// Devolvemos la vista
		return createGaupdate("", 3, null, null, model);
	}

	/**
	 * Actualiza el intent de bienvenida de google actions
	 * 
	 * @param welcomeIntent
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/savewelcomeintent")
	public String savewelcomeintent(@Valid @ModelAttribute WelcomeIntentForm welcomeIntent, BindingResult bindingResult,
			Model model) throws Exception {

		// Validamos que el modelo no tenga errores
		if (bindingResult.hasErrors()) {
			// Obtenemos los errores de validación
			ArrayList<String> validationErrors = new ArrayList<String>();

			for (ObjectError error : bindingResult.getAllErrors()) {
				validationErrors.add(((FieldError) error).getField() + ": " + error.getDefaultMessage());
			}

			// Devolvemos la vista
			return createGaupdate("", 3, validationErrors, null, model);
		}

		// Obtenemos la información de google actions
		GoogleActionsSdkInformation GAInfo = googleActionsServiceImpl.getInfo();
		GAInfo.setWelcomeIntent(welcomeIntent.getIntent());
		googleActionsServiceImpl.setInfo(GAInfo);

		// Devolvemos la vista
		return createGaupdate("", 3, null, "welcomeintentsaved", model);

	}

}
