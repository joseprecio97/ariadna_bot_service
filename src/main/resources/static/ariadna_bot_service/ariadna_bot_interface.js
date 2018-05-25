(function($) {

	// Declaración del plugin.
	$.fn.ariadna_bot_interface = function(options) {
		this
				.each(function() {
					var element = $(this);

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

					// Creamos el input
					var input = $("<input id='ariadna-text-input' type='text' class='form-control input-sm' placeholder='Type your message here...' />");

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

function sendMessage(options){
	//Obtenemos el mensaje a enviar
	var message = $('#ariadna-text-input').val();

	if (message != '') {
		//Obtenemos el conversation id de la cookie
		var conversationId = getCookie("ariadna-service-conversationId");
		
		//Enviamos el mensaje
		$.ajax({
	        type: "POST",
	        url: options.url + "/api/v1/command",
	        dataType: 'json',
	        contentType : 'application/json',
	        async: false,
	        data: '{"command":"' + message + '", "language":"es", "conversationId":"' + conversationId + '"}',
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
		            var voices = window.speechSynthesis.getVoices();
		            console.log(voices);
		            msg.voice = voices[0];
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