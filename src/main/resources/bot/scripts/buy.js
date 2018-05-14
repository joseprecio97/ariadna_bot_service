function newConversation(response){ 
	var obj = JSON.parse(response);
	
	if(obj.conversation.id = "buy"){
		obj.valid = true;
		obj.conversation = null;
	}
	
	return JSON.stringify(obj);
}

function productSetValue(conversation, value){
	/*
	var obj = JSON.parse(conversation);
	
	if(value == "cmimo88"){
		obj.parameters.color.value = "rosita cuqui";
	}
	
	return JSON.stringify(obj);*/
}

function endConversation(response){
	var obj = JSON.parse(response);
	
	obj.valid = true;
	
	return JSON.stringify(obj);
}