function productSetValue(conversation){
	var obj = JSON.parse(conversation);
	
	obj.valid = true;
	
	return JSON.stringify(obj);
}

function confirmSetValue(conversation){
	var obj = JSON.parse(conversation);
	
	obj.conversation.cancel = true;
	obj.prompts.es = "Me cago en ti";
	
	return JSON.stringify(obj);
}