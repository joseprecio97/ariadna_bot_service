package com.joseprecio.projectefinalcurs.bot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.joseprecio.projectefinalcurs.ApplicationConstants;
import com.joseprecio.projectefinalcurs.bot.exceptions.NotBotTrainingFolderException;
import com.joseprecio.projectefinalcurs.bot.exceptions.NotIntentTrainingException;
import com.joseprecio.projectefinalcurs.bot.exceptions.NotLanguageIntentTrainingException;
import com.joseprecio.projectefinalcurs.bot.exceptions.NotParameterIntentException;
import com.joseprecio.projectefinalcurs.model.CommandReceivedModel;
import com.joseprecio.projectefinalcurs.model.CommandResponseModel;
import com.joseprecio.projectefinalcurs.utils.Logger;

import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.ObjectStreamUtils;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;

/**
 * Implementación del bot
 * 
 * @author Josep Recio
 *
 */

public class Bot {

	// Conversaciones en curso del BOT por usuario
	private HashMap<String, Conversation> conversations;
	// Todos los intents disponibles del BOT por id de intent
	private HashMap<String, Intent> intents;
	// Todos los prompts por lenguaje
	private HashMap<String, Properties> languagePrompts;
	// Detector de intent por lenguaje
	private HashMap<String, DocumentCategorizerME> categorizer;
	// Detector de entities por lenguaje y intent
	private HashMap<String, HashMap<String, NameFinderME>> nameFinderME;
	// Textos loggin
	private ResourceBundle loggerResources;
	// Invocador javascript
	private HashMap<String, Invocable> js = null;

	/**
	 * Constructor
	 * 
	 */
	public Bot() {
		loggerResources = ResourceBundle.getBundle(ApplicationConstants.TEXTS_FILENAME,
				Locale.getDefault(Category.DISPLAY));

		this.categorizer = new HashMap<String, DocumentCategorizerME>();
		this.nameFinderME = new HashMap<String, HashMap<String, NameFinderME>>();
		this.intents = new HashMap<String, Intent>();
		this.conversations = new HashMap<String, Conversation>();
		this.languagePrompts = new HashMap<String, Properties>();
		this.js = new HashMap<String, Invocable>();
	}

	/**
	 * Función que carga los scripts JS para cada intent
	 * 
	 * @throws Exception
	 */
	private void LoadJSScripts() throws Exception {

		// Creamos el script manager
		ScriptEngineManager SEM = new ScriptEngineManager();

		// Recorremos todos los intents
		for (Intent intent : intents.values()) {
			try {
				// Obtenemos el intérprete para js
				ScriptEngine SE = SEM.getEngineByName("JavaScript");
				Invocable intentJS = null;

				// Creamos el string builder
				StringBuilder codigoJS = new StringBuilder();
				String lineaJS = "";

				// Obtenemos el fichero JS del .jar
				Resource f = new ClassPathResource(BotConstants.BOT_XML_CONFIG_FOLDER + BotConstants.BOT_SCRIPTS_FOLDER
						+ "\\" + intent.getId() + ".js");

				// Leemos el fichero JS del intent
				BufferedReader b = new BufferedReader(new InputStreamReader(f.getInputStream()));
				while ((lineaJS = b.readLine()) != null) {
					codigoJS.append(lineaJS);
				}
				b.close();

				// Evaluamos el código
				SE.eval(codigoJS.toString());

				// Creamos un objeto invocable a partir del código evaluado
				intentJS = (Invocable) SE;

				// Añadimos el invocable al map de invocables
				js.put(intent.getId(), intentJS);

				// Mostramos que se ha cargado correctamente el javascript del intent en
				// question
				Logger.writeConsole(
						MessageFormat.format(loggerResources.getString("msg_jsintent_loaded"), intent.getId()));
			} catch (FileNotFoundException FNF) {
				// El intent no contiene un fichero JS

			} catch (ScriptException E) {
				// Se produce una excepción al interpretar el JS
				throw E;
			}
		}

	}

	/**
	 * Carga la configuración del Bot
	 * 
	 * @throws Exception
	 */
	private void LoadBotConfig() throws Exception {
		try {
			// Creamos un objeto File a partir del XML de configuración del Bot
			Resource fXmlFile = new ClassPathResource(
					BotConstants.BOT_XML_CONFIG_FOLDER + BotConstants.BOT_XML_CONFIG_LOAD);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			// Parseamos y normalizamos el documento XML
			Document XMLDocument = dBuilder.parse(fXmlFile.getInputStream());
			XMLDocument.getDocumentElement().normalize();

			// Cargamos los intents
			NodeList intentsList = XMLDocument.getElementsByTagName("intent");

			// Cargamos cada intent detectado en el XML
			for (int i = 0; i < intentsList.getLength(); i++) {
				Node intentNode = intentsList.item(i);

				if (intentNode.getNodeType() == Node.ELEMENT_NODE) {
					Element intentElement = (Element) intentNode;

					// Creamos el intent
					Intent newIntent = new Intent();

					// Establecemos los atributos del intent
					newIntent.setId(intentElement.getAttribute("id"));

					// Obtenemos los lenguajes en que está disponible el intent
					for (String language : intentElement.getAttribute("language").split(",")) {
						newIntent.addLanguage(language);
					}

					// Obtenemos los parametros
					NodeList parametersList = intentElement.getElementsByTagName("parameter");

					// Recorremos los parámetros
					for (int j = 0; j < parametersList.getLength(); j++) {
						Node parameterNode = parametersList.item(j);

						if (parameterNode.getNodeType() == Node.ELEMENT_NODE) {
							Element parameterElement = (Element) parameterNode;

							// Obtenemos la info del parámetro
							String name = parameterElement.getAttribute("name");
							String str_required = parameterElement.getAttribute("required");

							boolean required;

							if (str_required.equals("true")) {
								required = true;
							} else {
								required = false;
							}

							String str_list = parameterElement.getAttribute("list");
							String str_type = parameterElement.getAttribute("type");

							// Creamos el parametro
							Parameter newParameter = new Parameter();

							newParameter.setName(name);
							newParameter.setRequired(required);
							newParameter.setType(str_type);

							// Inizializmos el valor del parametro
							if (str_list.equals("false")) {
								// EL PARAMETRO NO ES UNA LISTA

								newParameter.setList(false);
								newParameter.setValue(null);
							} else {
								// EL PARAMETRO ES UNA LISTA
								newParameter.setList(true);

								if (str_type.equals("String")) {
									newParameter.setValue(new ArrayList<String>());
								} else if (str_type.equals("Integer")) {
									newParameter.setValue(new ArrayList<Integer>());
								}
							}

							// Añadimos el parametro al intent
							newIntent.addParameter(newParameter.getName(), newParameter);
						}
					}

					// Añadimos el intent en el map de intents
					intents.put(newIntent.getId(), newIntent);

					Logger.writeConsole(
							MessageFormat.format(loggerResources.getString("msg_intent_loaded"), newIntent.getId()));
				}
			}

			// Cerramos el documento
			fXmlFile.getInputStream().close();
		} catch (Exception e) {
			// Lanzamos la excepción producida hacia arriba
			throw e;
		}

	}

	/**
	 * Carga los prompts
	 * 
	 * @param categoryStreams
	 * @param nameStreams
	 */
	private void loadPrompts(HashMap<String, List<ObjectStream<DocumentSample>>> categoryStreams,
			HashMap<String, HashMap<String, ObjectStream<NameSample>>> nameStreams) {
		// Creamos el categoryStreams y nameStreams y nameFinderME para cada idioma
		for (String language : BotConstants.BOT_AVAILABLE_LANGUAGE) {
			categoryStreams.put(language, new ArrayList<ObjectStream<DocumentSample>>());
			nameStreams.put(language, new HashMap<String, ObjectStream<NameSample>>());
			nameFinderME.put(language, new HashMap<String, NameFinderME>());
			InputStream promptFile = null;

			try {
				// Mostramos el fichero de prompts que se va a cargar
				Logger.writeConsole(MessageFormat.format(loggerResources.getString("msg_bot_prompts_file"), language));

				// Creamos un FileInputStream a partir del fichero prompts del lenguaje
				Resource promptFileResource = new ClassPathResource(BotConstants.BOT_XML_CONFIG_FOLDER
						+ BotConstants.BOT_PROMPTS_FOLDER + "\\" + language + "\\" + language + "-prompts.xml");
				promptFile = promptFileResource.getInputStream();
				Properties prompts = new Properties();

				// Cargamos el fichero de prompts
				prompts.loadFromXML(promptFile);

				// Ponemos el properties con los prompts en el mapa
				languagePrompts.put(language, prompts);

				// Cerramos el recurso
				promptFileResource.getInputStream().close();

				// Mostramos que se ha cargado correctamente el fichero de prompts
				Logger.writeConsole(
						MessageFormat.format(loggerResources.getString("msg_bot_prompts_file_loaded"), language));
			} catch (Exception e) {
				// No hay un fichero de prompts para el lenguaje en question
			}
		}
	}

	/**
	 * Carga los modelos de inteligencia del bot
	 * 
	 * @param categoryStreams
	 * @param nameStreams
	 * @throws NotBotTrainingFolderException
	 * @throws NotLanguageIntentTrainingException
	 * @throws NotIntentTrainingException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void loadModels(HashMap<String, List<ObjectStream<DocumentSample>>> categoryStreams,
			HashMap<String, HashMap<String, ObjectStream<NameSample>>> nameStreams)
			throws NotBotTrainingFolderException, NotLanguageIntentTrainingException, NotIntentTrainingException,
			UnsupportedEncodingException, IOException {
		// Array de streams de string
		ObjectStream<String> lineStream = null;

		Resource trainingFolder = new ClassPathResource(
				BotConstants.BOT_XML_CONFIG_FOLDER + BotConstants.BOT_TRAINING_FOLDER);

		if (!trainingFolder.exists()) {
			// No existe el directorio training en la carpeta dónde se encuentra el xml del
			// bot
			throw new NotBotTrainingFolderException(BotConstants.BOT_XML_CONFIG_LOAD);
		}

		// Recorremos todos los intents detectados en ese fichero de configuración
		for (Intent intent : intents.values()) {
			for (String language : intent.getLanguages()) {
				Resource languageTrainingFolder = new ClassPathResource(
						BotConstants.BOT_XML_CONFIG_FOLDER + BotConstants.BOT_TRAINING_FOLDER + "\\" + language);

				if (!(languageTrainingFolder.exists())) {
					// No existe el directorio con el idioma para el training
					throw new NotLanguageIntentTrainingException(intent.getId(), language);
				}

				Resource intentTraining = new ClassPathResource(
						BotConstants.BOT_XML_CONFIG_FOLDER + BotConstants.BOT_TRAINING_FOLDER + "\\" + language + "\\"
								+ language + "-" + intent.getId() + ".txt");

				if (!(intentTraining.exists())) {
					// No existe el fichero de entrenamiento para el intent
					throw new NotIntentTrainingException(intent.getId());
				}

				// **************************** Document Sample
				// ************************************
				// Leemos el fichero y devolvemos las líneas como String
				lineStream = new PlainTextByLineStream(intentTraining.getInputStream(), "UTF-8");

				// Eliminamos las entidades de las líneas leidas
				ObjectStream<DocumentSample> documentSampleStream = new IntentDocumentSampleStream(intent.getId(),
						lineStream);

				// Añadimos al listado el DocumentSample -> (Contiene categoria y documento)
				categoryStreams.get(language).add(documentSampleStream);

				// **************************** Name Sample
				// *************************************
				// Leemos el fichero y devolvemos las líneas como String
				lineStream = new PlainTextByLineStream(intentTraining.getInputStream(), "UTF-8");

				// Convierte los String leidos a NameSample
				ObjectStream<NameSample> nameSampleStream = new NameSampleDataStream(lineStream);

				// Añadimos el NameSample al listado de nama sample
				nameStreams.get(language).put(intent.getId(), nameSampleStream);
				nameFinderME.get(language).put(intent.getId(), null);

				// Cerramos el stream
				intentTraining.getInputStream().close();
			}
		}
	}

	/**
	 * Entrena el bot
	 * 
	 * @param categoryStreams
	 * @param nameStreams
	 * @throws IOException
	 */
	private void train(HashMap<String, List<ObjectStream<DocumentSample>>> categoryStreams,
			HashMap<String, HashMap<String, ObjectStream<NameSample>>> nameStreams) throws IOException {
		// Entrenamos al BOT para cada lenguaje
		for (String language : BotConstants.BOT_AVAILABLE_LANGUAGE) {
			Logger.writeConsole(MessageFormat.format(loggerResources.getString("msg_bot_training_language"), language));

			try {
				// Concatenamos los DocumentSample en un solo DocumentSample
				ObjectStream<DocumentSample> combinedDocumentSampleStream = ObjectStreamUtils
						.createObjectStream(categoryStreams.get(language).toArray(new ObjectStream[0]));

				// Entrenamos al modelo a detectar categorias
				categorizer.put(language, new DocumentCategorizerME(
						DocumentCategorizerME.train(language, combinedDocumentSampleStream, 0, 100)));

				// Cerramos el Stream
				combinedDocumentSampleStream.close();

				// Entrenamos al bot a identificar entities segun el idioma
				for (String intent : nameFinderME.get(language).keySet()) {
					// Concatenamos los NameSample
					ObjectStream<NameSample> combinedNameSampleStream = ObjectStreamUtils
							.createObjectStream(nameStreams.get(language).get(intent));

					// Entrenamos al modelo a detectar entidades
					nameFinderME.get(language).put(intent,
							new NameFinderME(NameFinderME.train(language, null, combinedNameSampleStream,
									TrainingParameters.defaultParams(), (AdaptiveFeatureGenerator) null,
									Collections.<String, Object>emptyMap())));

					// Cerramos el stream
					combinedNameSampleStream.close();
				}
			} catch (NullPointerException e) {

			}
		}
	}

	/**
	 * Inicializa el BOT
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		// Listado de DocumentSample -> (Contiene categoria y documento)
		HashMap<String, List<ObjectStream<DocumentSample>>> categoryStreams = new HashMap<String, List<ObjectStream<DocumentSample>>>();
		// Listado de NameSample
		HashMap<String, HashMap<String, ObjectStream<NameSample>>> nameStreams = new HashMap<String, HashMap<String, ObjectStream<NameSample>>>();

		// Cargamos la configuración del bot
		Logger.writeConsole(MessageFormat.format(loggerResources.getString("msg_bot_config_load"),
				BotConstants.BOT_XML_CONFIG_LOAD));
		LoadBotConfig();
		Logger.writeConsole(MessageFormat.format(loggerResources.getString("msg_bot_config_loaded"),
				BotConstants.BOT_XML_CONFIG_LOAD));

		// Cargamos los scripts de negocio del bot
		Logger.writeConsole(loggerResources.getString("msg_init_js_load"));
		LoadJSScripts();
		Logger.writeConsole(loggerResources.getString("msg_end_js_load"));

		// Cargamos los prompts
		Logger.writeConsole(loggerResources.getString("msg_bot_prompts_load"));
		loadPrompts(categoryStreams, nameStreams);
		Logger.writeConsole(loggerResources.getString("msg_bot_prompts_loaded"));

		// Cargamos los modelos de inteligencia
		Logger.writeConsole(loggerResources.getString("msg_bot_trainmodel_load"));
		loadModels(categoryStreams, nameStreams);
		Logger.writeConsole(loggerResources.getString("msg_bot_trainmodel_loaded"));

		// Entrenamos el BOT
		Logger.writeConsole(loggerResources.getString("msg_bot_train_start"));
		train(categoryStreams, nameStreams);
		Logger.writeConsole(loggerResources.getString("msg_bot_train_end"));
	}

	/**
	 * Detecta el intent a partir de un texto y un lenguaje
	 * 
	 * @param text
	 * @param language
	 * @return
	 */
	private String detectIntent(String text, String language) {
		// Obtenemos el detector de categorias según el idioma del usuario
		DocumentCategorizerME langCategorizer = categorizer.get(language);

		// Detectamos el intent
		double[] outcome = langCategorizer.categorize(text);
		String intent = langCategorizer.getBestCategory(outcome);

		// Devolvemos el intent detectado
		return intent;
	}

	/**
	 * Crea una nueva conversación a partir de un usuario, intent y lenguaje
	 * 
	 * @param username
	 * @param intent
	 * @param language
	 * @return
	 */
	private Conversation createConversation(String username, String intent, String language) {
		// Creamos una nueva conversación con los valores por defecto
		Conversation conversation = new Conversation();
		// Guardamos el usuario que esta realizando la conversa
		conversation.setUsername(username);
		// Guardamos el id del intent en la conversa
		conversation.setId(intents.get(intent).getId());
		// Guardamos el lenguaje del usuario como lenguaje de la conversa
		conversation.setLanguage(language);
		// Establecemos la conversación como válida
		conversation.setCancel(false);

		// Añadimos los parametros que tiene el intent a los parametros a conseguir de
		// la conversa
		LinkedHashMap<String, Parameter> newConversationIntentParameters = new LinkedHashMap<String, Parameter>();
		for (Parameter parameter : intents.get(intent).getParameters().values()) {
			Parameter parameterCopy = new Parameter();

			parameterCopy.setName(parameter.getName());
			parameterCopy.setRequired(parameter.isRequired());
			parameterCopy.setType(parameter.getType());

			// Inizializmos el valor del parametro
			if (!(parameter.isList())) {
				// EL PARAMETRO NO ES UNA LISTA
				parameterCopy.setList(false);
				parameterCopy.setValue(null);
			} else {
				// EL PARAMETRO ES UNA LISTA
				parameterCopy.setList(true);

				if (parameterCopy.getType().equals("String")) {
					parameterCopy.setValue(new ArrayList<String>());
				} else if (parameterCopy.getType().equals("Integer")) {
					parameterCopy.setValue(new ArrayList<Integer>());
				}
			}

			newConversationIntentParameters.put(parameterCopy.getName(), parameterCopy);
		}
		conversation.setParameters(newConversationIntentParameters);

		// Devolvemos la nueva conversación
		return conversation;
	}

	/**
	 * Da valor a un parametro de una conversación
	 * 
	 * @param conversation
	 * @param parameter_name
	 * @param value
	 * @throws NotParameterIntentException
	 */
	private void setParameterValue(Conversation conversation, String parameter_name, String value, boolean remove)
			throws NotParameterIntentException {
		try {
			// Obtenemos el parametro
			Parameter parameter = conversation.getParameters().get(parameter_name);

			// Guardamos el valor del parametro detectado
			if (parameter.getValue() instanceof List) {
				if (parameter.getType().equals("String")) {
					// Recuperamos el listado de valores
					ArrayList<String> listValues = (ArrayList<String>) parameter.getValue();

					if (!remove) {
						// Añadimos el valor detectado
						listValues.add(value);
					} else {
						// Eliminamos el valor detectado
						listValues.remove(listValues.indexOf(value));
					}

					// Guadamos el list en el parametro
					parameter.setValue(listValues);
				} else {
					// Recuperamos el listado de valores
					ArrayList<Integer> listValues = (ArrayList<Integer>) parameter.getValue();

					if (!remove) {
						// Añadimos el valor detectado
						listValues.add(Integer.parseInt(value));
					} else {
						// Eliminamos el valor detectado
						listValues.remove(listValues.indexOf(Integer.parseInt(value)));
					}

					// Guadamos el list en el parametro
					parameter.setValue(listValues);
				}
			} else {
				if (!remove) {
					if (parameter.getType().equals("String")) {
						parameter.setValue(value);
					} else {
						parameter.setValue(Integer.parseInt(value));
					}
				} else {
					parameter.setValue(null);
				}
			}
		} catch (NullPointerException e) {
			// EL INTENT NO CONTIENE EL PARAMETRO DETECTADO
			throw new NotParameterIntentException(parameter_name, conversation.getId(), value);
		}
	}

	/**
	 * Detecta las entidades de un texto y actualiza una conversación
	 * 
	 * @param text
	 * @param intent
	 * @param language
	 * @param conversation
	 * @throws NotParameterIntentException
	 */
	private void findEntities(String text, Conversation conversation) throws NotParameterIntentException {
		NameFinderME langNameFinder = null;
		// Obtenemos el detector de entidades según el idioma del usuario
		if ((langNameFinder = nameFinderME.get(conversation.getLanguage()).get(conversation.getId())) != null) {
			// Detectamos las entidades de la cadena
			String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(text);
			Span[] spans = langNameFinder.find(tokens);
			String[] names = Span.spansToStrings(spans, tokens);

			// Para cada parametro detectado en la cadena
			for (int i = 0; i < spans.length; i++) {
				// Guardamos el nombre del parámetro en la conversa
				setParameterValue(conversation, spans[i].getType(), names[i], false);
			}
		}
	}

	/**
	 * Código que se ejecuta al dar valor a un parámetro de la conversación
	 * 
	 * @param username
	 * @param value
	 * @return
	 * @throws ScriptException
	 * @throws NotParameterIntentException
	 */
	private CommandResponseModel onParameterSetValue(Conversation conversation, String value)
			throws ScriptException, NotParameterIntentException {
		Gson json = new Gson();
		String prompt = null;
		CommandResponseModel commandResponse = new CommandResponseModel();

		// EVENTO AFTER PARAMETER SET VALUE

		// Creamos un objeto ScriptResponse
		ScriptResponse response = new ScriptResponse();

		// Rellenamos la respuesta ScriptResponse
		response.setValid(true);
		response.setConversation(conversation);

		Invocable inv = null;

		// Comprovamos si existe un invocable para el intent
		if ((inv = js.get(conversation.getId())) != null) {
			try {

				// Ejecutamos el script
				String JSONresponse = (String) inv.invokeFunction(
						conversation.getNextCommingParameter() + BotConstants.BOT_PARAMETERSETVALUE_EVENT,
						json.toJson(response));

				if (JSONresponse != null) {
					// Descodificamos la respuesta
					response = json.fromJson(JSONresponse, ScriptResponse.class);

					// Si hemos recibido algun prompt en la respuesta del script
					String str = null;
					if (response.getPrompts() != null
							&& (str = response.prompts.get(conversation.getLanguage())) != null) {
						// Mostramos el mensaje recibido por JSON como respuesta del comando
						prompt = str;
					}

					// Comprovamos si el valor de la entidad no es válido
					if (!response.isValid()) {
						// Eliminamos el valor del parámetro
						setParameterValue(conversation, conversation.getNextCommingParameter(), value, true);

						// Comprovamos si no hemos recibido ningún prompt en el script
						if (prompt == null) {
							// Obtenemos el mensaje de error para mostrar
							prompt = languagePrompts.get(conversation.getLanguage())
									.getProperty(conversation.getId() + "_" + conversation.getNextCommingParameter()
											+ BotConstants.BOT_PARAMETERNOTVALID_SUFIX);

							// Comprovamos si no hay un prompt para el error
							if (prompt == null) {
								// Obtenemos el prompt para pedir de nuevo el parámetro
								prompt = languagePrompts.get(conversation.getLanguage()).getProperty(
										conversation.getId() + "_" + conversation.getNextCommingParameter());
							}
						}

						//Formateamos el prompt
						prompt = formatPrompt(prompt, conversation);
						
						// Establecemos el prompt del bot
						commandResponse.setCommand(prompt);

						// Devolvemos la respuesta del bot
						return commandResponse;
					} else {
						// Comprovamos si nos llega una conversación en la respuesta
						if (response.getConversation() != null) {
							// Actualizamos la conversación
							conversations.replace(conversation.getUsername(), response.getConversation());

							// Si la conversación se cancela o nos llega un prompt
							if (conversation.isCancel() || prompt != null) {
								if (prompt == null) {
									// Obtenemos el mensaje de operación cancelada para mostrar
									prompt = languagePrompts.get(conversation.getLanguage()).getProperty(
											conversation.getId() + BotConstants.BOT_CONVERSATIONCANCEL_SUFIX);
								}

								//Formateamos el prompt
								prompt = formatPrompt(prompt, conversation);
								
								// Establecemos la respuesta
								commandResponse.setCommand(prompt);

								// Comprovamos si se cancela la conversa
								if (conversation.isCancel()) {
									// Eliminamos la conversa del histórico
									conversations.put(conversation.getUsername(), null);
								}

								// Devolvemos la respuesta
								return commandResponse;
							} else {
								// Devolvemos nulo
								return null;
							}
						} else {
							// Damos el valor dado al parámetro como válido
							conversation.setNextCommingParameter(null);

							// Devolvemos nulo
							return null;
						}
					}
				} else {
					// Damos el valor dado al parámetro como válido
					conversation.setNextCommingParameter(null);

					// Devolvemos nulo
					return null;
				}
			} catch (NoSuchMethodException NSME) {
				// No se implementa un script de integración al darle valor a una entidad

				// Damos el valor dado al parámetro como válido
				conversation.setNextCommingParameter(null);

				// Devolvemos nulo
				return null;
			} catch (ScriptException SE) {
				// Excepción en la ejecucción del script
				throw new ScriptException(SE.getMessage());
			}
		} else {
			// Damos el valor dado al parámetro como válido
			conversation.setNextCommingParameter(null);

			// Devolvemos nulo
			return null;
		}
	}

	/**
	 * Código que se ejecuta cuándo un usuario inicia una nueva conversación
	 * 
	 * @param username
	 * @return
	 * @throws ScriptException
	 */
	private CommandResponseModel onNewConversation(Conversation conversation) throws ScriptException {
		// EVENTO ON CONVERSATION START
		Gson json = new Gson();

		// Creamos un objeto ScriptResponse
		ScriptResponse response = new ScriptResponse();

		// Rellenamos la respuesta ScriptResponse
		response.setValid(true);
		response.setConversation(conversation);

		Invocable inv = null;

		// Obtenemos el objeto invocable del intent
		if ((inv = js.get(conversation.getId())) != null) {
			try {
				// Ejecutamos el script
				String JSONresponse = (String) inv.invokeFunction(BotConstants.BOT_NEWCONVERSATION_EVENT,
						json.toJson(response));

				if (JSONresponse != null) {
					// Descodificamos la respuesta
					response = json.fromJson(JSONresponse, ScriptResponse.class);
					CommandResponseModel commandResponse = new CommandResponseModel();
					String prompt = null;
					String str = null;

					// Si hemos recibido algun prompt en la respuesta del script
					if (response.getPrompts() != null
							&& (str = response.prompts.get(conversation.getLanguage())) != null) {
						// Mostramos el mensaje recibido por JSON como respuesta del comando
						prompt = str;
					}

					// Comprovamos si se ha producido algún error en el script de nueva conversa
					if (!response.isValid()) {

						if (prompt == null) {
							// Obtenemos el mensaje de error para mostrar
							prompt = languagePrompts.get(conversation.getLanguage())
									.getProperty(conversation.getId() + BotConstants.BOT_NEWCONVERSATIONERROR_SUFIX);
						}

						//Formateamos el prompt
						prompt = formatPrompt(prompt, conversation);
						
						// Establecemos la respuesta del BOT
						commandResponse.setCommand(prompt);
						
						// Eliminamos la conversa
						conversations.replace(conversation.getUsername(), null);

						// Devolvemos la respuesta
						return commandResponse;
					} else {
						if (response.getConversation() != null) {
							// Actualizamos la conversación
							conversations.replace(conversation.getUsername(), response.getConversation());

							// Si la conversación se cancela o nos llega un prompt
							if (conversation.isCancel() || prompt != null) {
								if (prompt == null) {
									// Obtenemos el mensaje de operación cancelada para mostrar
									prompt = languagePrompts.get(conversation.getLanguage()).getProperty(
											conversation.getId() + BotConstants.BOT_CONVERSATIONCANCEL_SUFIX);
								}

								//Formateamos el prompt
								prompt = formatPrompt(prompt, conversation);
								
								// Establecemos la respuesta
								commandResponse.setCommand(prompt);

								// Comprovamos si se cancela la conversa
								if (conversation.isCancel()) {
									// Eliminamos la conversa del histórico
									conversations.put(conversation.getUsername(), null);
								}

								// Devolvemos la respuesta
								return commandResponse;
							}
						}

						// Devolvemos nulo
						return null;
					}
				} else {
					// Devolvemos nulo
					return null;
				}
			} catch (NoSuchMethodException NSME) {
				// No se implementa un método de integración al iniciar una nueva conversa
				return null;
			} catch (ScriptException SE) {
				// Excepción en la ejecucción del script
				throw new ScriptException(SE.getMessage());
			}
		} else {
			// Devolvemos nulo
			return null;
		}
	}

	/**
	 * Da formato a un prompt
	 * 
	 * @param prompt
	 * @param conversation
	 * @return
	 */
	private String formatPrompt(String prompt, Conversation conversation) {
		//Creamos un array 
		ArrayList<String> params = new ArrayList<String>();
		
		for(Parameter param : conversation.getParameters().values()) {
			if(!param.isList()) {
				if(param.getType().equals("String")) {
					params.add((String) param.getValue());
				}else {
					params.add(String.valueOf(param.getValue()));
				}
			}else {
				if (param.getType().equals("String")) {
					// Recuperamos el listado de valores
					ArrayList<String> listValues = (ArrayList<String>) param.getValue();

					//Concatenamos los valores
					String str = "";
					for(String value: listValues) {
						if(!str.equals("")) {
							str += ", ";
						}
						
						str += value;
					}
					
					//Añadimos la cadena al array de parametros
					params.add(str);
				} else {
					// Recuperamos el listado de valores
					ArrayList<Integer> listValues = (ArrayList<Integer>) param.getValue();

					//Concatenamos los valores
					String str = "";
					for(Integer value: listValues) {
						if(!str.equals("")) {
							str += ", ";
						}
						
						str += String.valueOf(value);
					}
					
					//Añadimos la cadena al array de parametros
					params.add(str);
				}

			}
		}
		
		//Formateamos el prompt
		prompt = MessageFormat.format(prompt, params.toArray());
		
		//Devolvemos el prompt formateado
		return prompt;
	}
	
	/**
	 * Obtiene el próximo parámetro a solicitar al usuario
	 * 
	 * @param username
	 * @return
	 * @throws NotLanguageIntentTrainingException
	 * @throws NotParameterPromptException
	 */
	private CommandResponseModel getNextCommingParameterPrompt(Conversation conversation)
			throws NotLanguageIntentTrainingException {
		String prompt = null;
		CommandResponseModel commandResponse = new CommandResponseModel();
		Gson json = new Gson();

		for (Parameter parameter : conversation.getParameters().values()) {
			// Comprovamos si el parametro tiene valor
			if (parameter.getValue() == null && parameter.isRequired()
					|| parameter.isList() && ((ArrayList) parameter.getValue()).size() <= 0 && parameter.isRequired()) {
				// Guardamos cual es el parametro que pedimos al usuario
				conversation.setNextCommingParameter(parameter.getName());

				try {
					// Obtenemos el mensaje a mostrar
					prompt = languagePrompts.get(conversation.getLanguage())
							.getProperty(conversation.getId() + "_" + parameter.getName());

					//Formateamos el prompt
					prompt = formatPrompt(prompt, conversation);
					
					// Establecemos el mensaje de la respuesta
					commandResponse.setCommand(prompt);

					// Mostramos la conversación en curso
					Logger.writeConsole("DEBUG -- Conversation in course: " + json.toJson(conversation));

					// Devolvemos la respuesta
					return commandResponse; 
				} catch (NullPointerException e) {
					// No se soporta el lenguaje de la conversación
					throw new NotLanguageIntentTrainingException(conversation.getId(), conversation.getLanguage());
				}
			}
		}

		// Devolvemos nulo
		return null;
	}

	/**
	 * Evento que se produce al terminar una conversación
	 * 
	 * @param username
	 * @return
	 * @throws ScriptException
	 */
	private String onConversationEnd(Conversation conversation) throws ScriptException {
		Gson json = new Gson();
		String prompt = null;

		// EVENTO ON CONVERSATION END

		// Creamos un objeto ScriptResponse
		ScriptResponse response = new ScriptResponse();

		// Rellenamos la respuesta ScriptResponse
		response.setValid(true);
		response.setConversation(conversation);

		Invocable inv = null;

		// Comprovamos si hay un invocable para el intent
		if ((inv = js.get(conversation.getId())) != null) {
			try {

				// Ejecutamos el script
				String JSONresponse = (String) inv.invokeFunction(BotConstants.BOT_ENDCONVERSATION_EVENT,
						json.toJson(response));

				// Si el script nos devuelve un JSON
				if (JSONresponse != null) {
					// Descodificamos la respuesta
					response = json.fromJson(JSONresponse, ScriptResponse.class);

					// Comprovamos si se ha producido algún error en el script de integración
					if (!response.isValid()) {
						// Obtenemos el mensaje de error para mostrar
						prompt = languagePrompts.get(conversation.getLanguage())
								.getProperty(conversation.getId() + BotConstants.BOT_ENDCONVERSATIONERROR_SUFIX);
					} else if (response.getConversation().isCancel()) {
						// Obtenemos el mensaje de operación cancelada para mostrar
						prompt = languagePrompts.get(conversation.getLanguage())
								.getProperty(conversation.getId() + BotConstants.BOT_CONVERSATIONCANCEL_SUFIX);
					} else {
						// Obtenemos el mensaje de que el intent se ha completado correctamente
						prompt = languagePrompts.get(conversation.getLanguage())
								.getProperty(conversation.getId() + BotConstants.BOT_ENDCONVERSATIONMESSAGE_SUFIX);
					}

					// Si hemos recibido algun prompt en la respuesta del script
					String str = null;
					if (response.getPrompts() != null
							&& (str = response.prompts.get(conversation.getLanguage())) != null) {
						// Mostramos el mensaje recibido por JSON como respuesta del comando
						prompt = str;
					}
				} else {
					// El script de integración no devuelve un objeto válido, obtenemos el prompt
					// por defecto
					prompt = languagePrompts.get(conversation.getLanguage())
							.getProperty(conversation.getId() + BotConstants.BOT_ENDCONVERSATIONMESSAGE_SUFIX);
				}

				// Devolvemos el prompt
				return prompt;
			} catch (NoSuchMethodException NSME) {
				// No hay script de integración, obtenemos el prompt por defecto
				prompt = languagePrompts.get(conversation.getLanguage())
						.getProperty(conversation.getId() + BotConstants.BOT_ENDCONVERSATIONMESSAGE_SUFIX);

				// Devolvemos el prompt
				return prompt;
			} catch (ScriptException SE) {
				// Excepción en la ejecucción del script
				throw new ScriptException(SE.getMessage());
			}
		} else {
			// No hay script de integración, obtenemos el prompt por defecto
			prompt = languagePrompts.get(conversation.getLanguage())
					.getProperty(conversation.getId() + BotConstants.BOT_ENDCONVERSATIONMESSAGE_SUFIX);

			// Devolvemos el prompt
			return prompt;
		}
	}

	/**
	 * Envia un mensaje al Bot
	 * 
	 * @param commandReceived
	 * @throws NotParameterIntentException
	 * @throws NotParameterPromptException
	 * @throws NotLanguageIntentTrainingException
	 * @throws ScriptException
	 */
	public CommandResponseModel sendMessage(CommandReceivedModel commandReceived) {
		Gson json = new Gson();
		String username = "josep.recio@outlook.com";
		String language = "es";
		String prompt = null;
		CommandResponseModel commandResponse = new CommandResponseModel();

		// Comprovamos si el usuario ha iniciado alguna conversación con el Bot
		if (conversations.get(username) == null) {
			// Detectamos el intent a partir del comando recibido
			String intent = detectIntent(commandReceived.getCommand(), language);

			// Creamos una nueva conversación para el usuario, el intent detectado y el
			// lenguaje del usuario
			Conversation newConversation = createConversation(username, intent, language);

			try {
				// Detectamos las entidades del texto
				findEntities(commandReceived.getCommand(), newConversation);
			} catch (NotParameterIntentException e) {
				//Mostramos la excepción
				e.printStackTrace();
				
				// Obtenemos el mensaje que indica que hay un error con el valor que se ha dado
				// al parámetro
				prompt = languagePrompts.get(conversations.get(username).getLanguage())
						.getProperty(conversations.get(username).getId() + BotConstants.BOT_SENDMESSAGEERROR_SUFIX);

				//Formateamos el prompt
				prompt = formatPrompt(prompt, conversations.get(username));
				
				// Establecemos el prompt como mensaje de respuesta
				commandResponse.setCommand(prompt);

				//Marcamos como que se ha producido un error de servidor
				commandResponse.setServerError(true);
				
				// Devolvemos la respuesta del BOT
				return commandResponse;
			}

			// Añadimos la conversa al listado de conversas activas del bot
			conversations.put(username, newConversation);

			CommandResponseModel newConversationEventResponse = null;

			try {
				// Ejecutamos el script del evento nueva conversa
				if ((newConversationEventResponse = onNewConversation(conversations.get(username))) != null) {
					// Mostramos la conversación que tiene en curso el usuario que ha enviado el
					// parámetro
					if (conversations.get(username) != null) {
						Logger.writeConsole(
								"DEBUG -- Conversation in course: " + json.toJson(conversations.get(username)));
					}

					// Devolvemos la respuesta del evento
					return newConversationEventResponse;
				}
			} catch (ScriptException SE) {
				// Error de ejecución del script

				//Mostramos la traza de error
				SE.printStackTrace();
				
				// Obtenemos el mensaje que indica que hay un error con el valor que se ha dado
				// al parámetro
				prompt = languagePrompts.get(conversations.get(username).getLanguage())
						.getProperty(conversations.get(username).getId() + BotConstants.BOT_SENDMESSAGEERROR_SUFIX);

				//Formateamos el prompt
				prompt = formatPrompt(prompt, conversations.get(username));
				
				// Establecemos el prompt como mensaje de respuesta
				commandResponse.setCommand(prompt);

				//Marcamos como que se ha producido un error de servidor
				commandResponse.setServerError(true);
				
				// Devolvemos la respuesta del BOT
				return commandResponse;
			}
		} else {
			try {
				// Establecemos el valor del parámetro
				setParameterValue(conversations.get(username), conversations.get(username).getNextCommingParameter(),
						commandReceived.getCommand(), false);

				CommandResponseModel setValueEventResponse = null;

				// Ejecutamos el script de set value
				if ((setValueEventResponse = onParameterSetValue(conversations.get(username),
						commandReceived.getCommand())) != null) {
					// Mostramos la conversación que tiene en curso el usuario que ha enviado el
					// parámetro
					if (conversations.get(username) != null) {
						Logger.writeConsole(
								"DEBUG -- Conversation in course: " + json.toJson(conversations.get(username)));
					}

					// Devolvemos la respuesta del evento
					return setValueEventResponse;
				}
			} catch (ScriptException SE) {
				// Error de ejecución del script

				//Mostramos la traza de error
				SE.printStackTrace();
				
				// Obtenemos el mensaje que indica que hay un error con el valor que se ha dado
				// al parámetro
				prompt = languagePrompts.get(conversations.get(username).getLanguage())
						.getProperty(conversations.get(username).getId() + BotConstants.BOT_SENDMESSAGEERROR_SUFIX);

				//Formateamos el prompt
				prompt = formatPrompt(prompt, conversations.get(username));
				
				// Establecemos el prompt como mensaje de respuesta
				commandResponse.setCommand(prompt);

				//Marcamos como que se ha producido un error de servidor
				commandResponse.setServerError(true);
				
				// Devolvemos la respuesta del BOT
				return commandResponse;
			} catch (Exception e) {
				// Error con el valor que se ha dado al parámetro

				//Mostramos la excepción
				e.printStackTrace();
				
				// Obtenemos el mensaje que indica que hay un error con el valor que se ha dado
				// al parámetro
				prompt = languagePrompts.get(conversations.get(username).getLanguage())
						.getProperty(conversations.get(username).getId() + "_"
								+ conversations.get(username).getNextCommingParameter()
								+ BotConstants.BOT_PARAMETERBADVALUE_SUFIX);

				//Formateamos el prompt
				prompt = formatPrompt(prompt, conversations.get(username));
				
				// Establecemos el prompt como mensaje de respuesta
				commandResponse.setCommand(prompt);

				//Marcamos como que se ha producido un error de servidor
				commandResponse.setServerError(true);
				
				// Devolvemos la respuesta del BOT
				return commandResponse;
			}
		}

		// Comprovamos si no sabemos cual es el siguiente parámetro
		if (conversations.get(username).getNextCommingParameter() == null) {
			CommandResponseModel nextCommingParameter = null;

			try {
				// Obtenemos el prompt para pedir el próximo parámetro
				if ((nextCommingParameter = getNextCommingParameterPrompt(conversations.get(username))) != null) {
					return nextCommingParameter;
				} else {
					try {
						// Ejecutamos el script final
						prompt = onConversationEnd(conversations.get(username));
					} catch (ScriptException SE) {
						// Error de ejecución del script

						//Mostramos la traza
						SE.printStackTrace();
						
						// Obtenemos el mensaje que indica que hay un error con el valor que se ha dado
						// al parámetro
						prompt = languagePrompts.get(conversations.get(username).getLanguage()).getProperty(
								conversations.get(username).getId() + BotConstants.BOT_SENDMESSAGEERROR_SUFIX);

						//Formateamos el prompt
						prompt = formatPrompt(prompt, conversations.get(username));
						
						// Establecemos el prompt como mensaje de respuesta
						commandResponse.setCommand(prompt);

						//Marcamos como que se ha producido un error de servidor
						commandResponse.setServerError(true);
						
						// Devolvemos la respuesta del BOT
						return commandResponse;
					}

					//Formateamos el prompt
					prompt = formatPrompt(prompt, conversations.get(username));
					
					// Establecemos la respuesta
					commandResponse.setCommand(prompt);

					// Mostramos la conversa completa
					Logger.writeConsole("DEBUG -- Conversation complete: " + json.toJson(conversations.get(username)));

					// Eliminamos la conversa del histórico
					conversations.put(username, null);
				}
			} catch (NotLanguageIntentTrainingException e) {
				//Mostramos la traza
				e.printStackTrace();
				
				// Obtenemos el mensaje que indica que hay un error con el valor que se ha dado
				// al parámetro
				prompt = languagePrompts.get(conversations.get(username).getLanguage())
						.getProperty(conversations.get(username).getId() + BotConstants.BOT_SENDMESSAGEERROR_SUFIX);

				//Formateamos el prompt
				prompt = formatPrompt(prompt, conversations.get(username));
				
				// Establecemos el prompt como mensaje de respuesta
				commandResponse.setCommand(prompt);

				//Marcamos como que se ha producido un error de servidor
				commandResponse.setServerError(true);
				
				// Devolvemos la respuesta del BOT
				return commandResponse;
			}
		}

		// Mostramos la conversación que tiene en curso el usuario que ha enviado el
		// parámetro
		if (conversations.get(username) != null) {
			Logger.writeConsole("DEBUG -- Conversation in course: " + json.toJson(conversations.get(username)));
		}

		// Devolvemos la respuesta
		return commandResponse;
	}
}
