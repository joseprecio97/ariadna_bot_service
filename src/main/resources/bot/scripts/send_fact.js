function num_factSetValue(conversation){
	var obj = JSON.parse(conversation);
	
	if(obj.conversation.parameters.num_fact.value == 1){
		obj.valid = false;
		obj.prompts.es = "No es valida la factura 1";
		
		return JSON.stringify(obj);
	}else{
		if(obj.conversation.parameters.num_fact.value == 2){
			
			obj.valid = true;
			obj.prompts.es = "La factura 2 se puede enviar a: domingo@email y manel@email. A quien lo quieres enviar?";
			obj.conversation.nextCommingParameter = "email";
			
			return JSON.stringify(obj);
			
		}
	}
	
}

function otherEmailSetValue(conversation){
	var obj = JSON.parse(conversation);
	
	if(obj.conversation.parameters.otherEmail.value == "si"){
		obj.conversation.parameters.otherEmail.value = null;
		obj.conversation.nextCommingParameter = "email";
		obj.prompts.es = "A que otro destinatario quieres enviar la factura";
		
		return JSON.stringify(obj); 
	}
}