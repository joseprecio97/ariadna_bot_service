package com.joseprecio.projectefinalcurs.bot;

/**
 * Constantes del objeto Bot
 * 
 * @author joseprecio
 *
 */

public class BotConstants {

	public static final String BOT_GOOGLEASSISTANT_URL = "/api/v1/googleassistant";
	
	public static final String BOT_CONFIG_FOLDER = "bot//";
	public static final String BOT_CONFIG_LOAD = "bot.json";
	public static final String BOT_TRAINING_FOLDER = "training";
	public static final String BOT_PROMPTS_FOLDER = "prompts";
	public static final String BOT_SCRIPTS_FOLDER = "scripts";
	public static final String BOT_GOOGLEACTIONS_FOLDER = "googleactions";
	public static final String[] BOT_AVAILABLE_LANGUAGE = {"es", "en", "ca"};
	public static final String BOT_GOOGLEACTIONS_FILE = "information.json";
	
	public static final String BOT_NEWCONVERSATION_EVENT = "newConversation";
	public static final String BOT_PARAMETERSETVALUE_EVENT = "SetValue";
	public static final String BOT_ENDCONVERSATION_EVENT = "endConversation";
	
	public static final String BOT_SENDMESSAGEERROR_SUFIX = "_error";
	public static final String BOT_NEWCONVERSATIONERROR_SUFIX = "_new_conversation_error";
	public static final String BOT_ENDCONVERSATIONERROR_SUFIX = "_end_conversation_error";
	public static final String BOT_ENDCONVERSATIONMESSAGE_SUFIX = "_final_message";
	public static final String BOT_PARAMETERBADVALUE_SUFIX = "_bad_value";
	public static final String BOT_PARAMETERNOTVALID_SUFIX = "_invalid";
	public static final String BOT_CONVERSATIONCANCEL_SUFIX = "_cancel";
}
