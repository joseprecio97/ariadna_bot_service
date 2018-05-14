package com.joseprecio.projectefinalcurs.utils;

import java.sql.Timestamp;
import java.text.MessageFormat;

/**
 * Classe para escribir log
 * 
 * @author joseprecio
 *
 */

public class Logger {

	public static void writeConsole(String text) {
		System.out.println(new Timestamp(System.currentTimeMillis()) + " :"
				+ text);
	}
	
}
