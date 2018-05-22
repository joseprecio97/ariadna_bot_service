function endConversation(conversation){
  var conv = JSON.parse(conversation);
  
  conv.conversation.parameters.resultado.value = parseInt(conv.conversation.parameters.value1.value) + parseInt(conv.conversation.parameters.value2.value);
  
  return JSON.stringify(conv);
}












