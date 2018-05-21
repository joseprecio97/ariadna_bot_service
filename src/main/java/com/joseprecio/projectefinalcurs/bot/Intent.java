package com.joseprecio.projectefinalcurs.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Intent del Bot
 * 
 * @author joseprecio
 *
 */

public class Intent {

	private String id;
	private List<String> languages;
	private LinkedHashMap<String, Parameter> parameters;

	public Intent() {
		this.languages = new ArrayList<String>();
		this.parameters = new LinkedHashMap<String, Parameter>();
	}

	/**
	 * Elimina un parameter del intent
	 * 
	 * @param name
	 * @return
	 */
	public LinkedHashMap<String, Parameter> removeParameter(String name){
		parameters.remove(name);
		
		return parameters;
	}
	
	/**
	 * Añade un parámetro a un intent
	 * 
	 * @param newParameter
	 * @return
	 */
	public LinkedHashMap<String, Parameter> addParameter(Parameter newParameter){
		parameters.put(newParameter.getName(), newParameter);
		
		return parameters;
	}
	
	/**
	 * Obtiene el script del intent
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getScript() throws IOException {
		//Objetos
		String linea = null;
		StringBuilder script = new StringBuilder();
		
		// Cargamos el script
		File trainingFile = new File(BotConstants.BOT_CONFIG_FOLDER + BotConstants.BOT_SCRIPTS_FOLDER
			+ "\\" + id + ".js");

		// Leemos el script
		BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(trainingFile)));
		while ((linea = b.readLine()) != null) {
			script.append(linea);
			script.append(System.lineSeparator());
		}

		// Cerramos el buffer
		b.close();

		//Devolvemos el script
		return script.toString();
	}
	
	/**
	 * Edita el script de un intent
	 * 
	 * @param script
	 * @throws IOException
	 */
	public void editScript(String script) throws IOException {
		// Obtenemos el script del intent
		FileWriter scriptFile = new FileWriter(BotConstants.BOT_CONFIG_FOLDER + BotConstants.BOT_SCRIPTS_FOLDER
				+ "\\" + id + ".js", false);
		PrintWriter writer = new PrintWriter(scriptFile);

		// Guardamos el script en el fichero
		writer.println(script);

		// Cerramos el stream
		writer.close();
		scriptFile.close();
	}

	/**
	 * Elimina una frase de entrenamiento del intent
	 * 
	 * @param language
	 * @param phrase
	 * @throws IOException
	 */
	public void removeTrainingPhrase(String language, String phrase) throws IOException {
		// Obtenemos las training phrases
		HashMap<String, ArrayList<String>> trainingPhrases = getTrainingPhrases();

		// Obtenemos el fichero de frases de entrenamiento
		FileWriter trainingFile = new FileWriter(BotConstants.BOT_CONFIG_FOLDER + BotConstants.BOT_TRAINING_FOLDER
				+ "\\" + language + "\\" + language + "-" + id + ".txt", false);
		PrintWriter writer = new PrintWriter(trainingFile);

		// Recorremos todas las training phrases del idioma correspondiente
		for (String trainingPhrase : trainingPhrases.get(language)) {
			// Comprovamos si no es la frase que queremos eliminar
			if (!phrase.equals(trainingPhrase)) {
				// Guardamos la frase en el fichero
				writer.println(trainingPhrase);
			}
		}

		// Cerramos el stream
		writer.close();
		trainingFile.close();
	}

	/**
	 * Añade una nueva frase de entrenamiento para el intent
	 * 
	 * @param language
	 * @param phrase
	 * @throws IOException
	 */
	public void addTrainingPhrase(String language, String phrase) throws IOException {
		// Obtenemos el fichero de frases de entrenamiento
		FileWriter trainingFile = new FileWriter(BotConstants.BOT_CONFIG_FOLDER + BotConstants.BOT_TRAINING_FOLDER
				+ "\\" + language + "\\" + language + "-" + id + ".txt", true);
		PrintWriter writer = new PrintWriter(trainingFile);

		// Guardamos los intents en el fichero
		writer.println(phrase);

		// Cerramos el stream
		writer.close();
		trainingFile.close();
	}

	/**
	 * Obtiene las frases de entrenamiento de un intent
	 * 
	 * @return
	 * @throws IOException
	 */
	public HashMap<String, ArrayList<String>> getTrainingPhrases() throws IOException {
		// Creamos el hash map
		HashMap<String, ArrayList<String>> trainingPhrases = new HashMap<String, ArrayList<String>>();

		// Objetos
		String linea = "";

		// Recorremos todos los lenguajes
		for (String language : BotConstants.BOT_AVAILABLE_LANGUAGE) {
			// Creamos el fichero de entrenamiento
			File trainingFile = new File(BotConstants.BOT_CONFIG_FOLDER + BotConstants.BOT_TRAINING_FOLDER + "\\"
					+ language + "\\" + language + "-" + id + ".txt");

			// Array de frases de entrenamiento
			ArrayList<String> phrases = new ArrayList<String>();

			// Leemos el fichero de entrenamiento
			BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(trainingFile)));
			while ((linea = b.readLine()) != null) {
				phrases.add(linea);
			}

			// Cerramos el buffer
			b.close();

			// Añadimos el array de frases al hashmap
			trainingPhrases.put(language, phrases);
		}

		// Devolvemos el hash map
		return trainingPhrases;
	}

	public List<String> addLanguage(String language) {
		this.languages.add(language);

		return this.languages;
	}

	public List<String> getLanguages() {
		return languages;
	}

	public void setLanguages(ArrayList<String> languages) {
		this.languages = languages;
	}

	public void addParameter(String name, Parameter parameter) {
		parameters.put(name, parameter);
	}

	public HashMap<String, Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(LinkedHashMap<String, Parameter> parameters) {
		this.parameters = parameters;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
