(function($) {

	// Declaración del plugin.
	$.fn.ariadna_bot_interface = function(options) {
		this
				.each(function() {
					var element = $(this);

					//Actualizamos el objeto
					globalOptions = options;
					
					//Creamos el header div
					var panel_header = $('<div class="panel-heading">');
					var chat_text = $('<i class="fa fa-comments fa-fw"></i>');
					var btn_group_langs = $('<div class="btn-group pull-right">');
					var select_langs = $('<select style="margin-top: -3px;padding: 4px;border-radius: 5px;">');
					var es_option = $('<option value="es">Spanish</option>');
					var en_option = $('<option value="en">English</option>');
					
					if(options.locale=='es'){
						es_option.attr("selected", true);
					}else{
						en_option.attr("selected", true);
					}
					
					select_langs.append(es_option);
					select_langs.append(en_option);
					
					select_langs.change(function(){
						conv_language=this.value;
					});
					conv_language=options.locale;
					btn_group_langs.append(select_langs);
					
					panel_header.append(chat_text);
					panel_header.append(btn_group_langs);
					element.append(panel_header);
					element.addClass('chat-panel');
					element.addClass('panel');
					element.addClass('panel-default');
					
					// Creamos el div panel body
					var panel_body = $("<div/>");
					panel_body.addClass("panel-body");

					// Creamos el ul chat
					var chat = $("<ul/>");
					chat.addClass("chat");

					// Añadimos el chat al panel body
					panel_body.append(chat);

					// Creamos el panel footer
					var panel_footer = $("<div/>");
					panel_footer.addClass("panel-footer");

					// Creamos el input-group
					var input_group = $("<div/>");
					input_group.addClass("input-group");

					//Imagen micrófono
					var img = $('<img id="microphone-btn" src="' + options.url + '/images/microphone.png" class="img-circle" style="height: 30px;width: 30px;float: left; margin-right: 10px;" />');
					img.fadeTo( "slow" , 0.6);
					img.click(function(){
						if ('webkitSpeechRecognition' in window) {
							$("#microphone-btn").fadeTo( "slow" , 1);
							startRecording();
						}
					});
					input_group.append(img);
					
					// Creamos el input
					var input = $("<input id='ariadna-text-input' type='text' class='form-control input-sm' style='width: 95%;' placeholder='Type your message here...' />");

					input.keypress(function(event){
						if ( event.which == 13 ) {
							sendMessage(options);
						}
					});
					
					// Creamos el span
					var span = $("<span class='input-group-btn'>");

					// Creamos el botón de búsquedo
					var btn_chat = $("<button class='btn btn-warning btn-sm' id='ariadna-btn-chat'>Send</button>");

					// Evento al hacer click sobre el botón chat
					btn_chat.click(function() {
						//Enviamos el mensaje
						sendMessage(options);
					});

					// Añadimos el botón al span
					span.append(btn_chat);

					// Añadimos el input al input
					input_group.append(input);

					// Añadimos el span al panel_footer
					input_group.append(span);

					// Añadimos el input-group al panel-footer
					panel_footer.append(input_group);

					// Añadimos el panel body al div
					element.append(panel_body);
					// Añadimos el panel footer al div
					element.append(panel_footer);
					
					
				});

		return this;
	}

})(jQuery);

var globalOptions = null;
var conv_language = null;
var voices = null;
var recording = 0;

window.speechSynthesis.onvoiceschanged = function() {
	if ('speechSynthesis' in window) {
		voices = window.speechSynthesis.getVoices();
	}
};

function sendMessage(options){
	//Obtenemos el mensaje a enviar
	var message = $('#ariadna-text-input').val();

	if (message != '') {
		//Obtenemos el conversation id de la cookie
		var conversationId = getCookie("ariadna-service-conversationId");
		
		var url = "";
		
		if (options.production){
			url = "/api/v1/command";
		}else{
			url = "/api/v1/dev-command";
		}
		
		//Enviamos el mensaje
		$.ajax({
	        type: "POST",
	        url: options.url + url,
	        dataType: 'json',
	        contentType : 'application/json',
	        async: false,
	        data: '{"command":"' + message + '", "language":"' + conv_language + '", "conversationId":"' + conversationId + '"}',
	        success: function (response) {
	        	//Nos guardamos el id de conversación
	        	if(conversationId == ""){
	        		setCookie("ariadna-service-conversationId", response.conversationId);
	        	}
	        	
	        	//Añadimos el mensaje al chat
	    		var p = $("<p>" + message + "</p>");
	    		var chat_body = $("<div class='chat-body clearfix'>");
	    		chat_body.append(p);
	    		var img = $('<img src="' + options.url + '/images/avatar.png" class="img-circle" style="height: 50px;width: 50px;" />');
	    		var span = $('<span class="chat-img pull-right">');
	    		span.append(img);
	    		var li = $('<li class="right clearfix">');
	    		li.append(span);
	    		li.append(chat_body);
	    		$(".chat").append(li);
	        	
	        	//Añadimos la respuesta al chat
	        	var p = $("<p>" + response.command + "</p>");
	        	var chat_body = $("<div class='chat-body clearfix'>");
	        	chat_body.append(p);
	        	var img = $('<img src="' + options.url + '/images/ariadnaavatar.png" class="img-circle" style="height: 50px;width: 50px;" />');
	        	var span = $('<span class="chat-img pull-left">');
	        	span.append(img);
	        	var li = $('<li class="left clearfix">');
	        	li.append(span);
	        	li.append(chat_body);
	        	$(".chat").append(li);
	        	
	        	//Vaciamos el input del mensaje
	        	$('#ariadna-text-input').val('');
	        	
	        	//Hacemos el scroll
	        	document.getElementsByClassName('panel-body')[0].scrollTop += 100000;
	        	
	        	//Comprovamos si el navegador soporta speechSynthesis
	        	if ('speechSynthesis' in window) {
		        	//Dictamos el mensaje
		        	var msg = new SpeechSynthesisUtterance();
		            
		            for(var i = 0; i < voices.length; i++){
		            	if(conv_language == "es" && voices[i].name=="Microsoft Helena Desktop - Spanish (Spain)"){
		            		msg.voice = voices[i];
		            	}else if(conv_language == "en" && voices[i].name=="Microsoft Zira Desktop - English (United States)"){
		            		msg.voice = voices[i];
		            	}
		            }
		            
		            msg.rate = 10 / 10;
		            msg.pitch = 1;
		            msg.text = response.command;
		            speechSynthesis.speak(msg);
	        	}
	        }, 
	        error: function() {
	        	
	        }
	    });
	}
};

//Obtiene una cookie
function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

//Establece una cookie
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}