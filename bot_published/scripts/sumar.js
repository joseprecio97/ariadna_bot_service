function endConversation(conversacion){
  /*Deserializamos el objeto conversaci�n*/
  var obj = JSON.parse(conversacion);
  /*Recuperamos el valor del parametro 1*/
  var valor1 = parseInt(obj.conversation.parameters.valor1.value);
  /*Recuperamos el valor del parametro 2*/
  var valor2 = parseInt(obj.conversation.parameters.valor2.value);
  /*Calculamos el valor del parametro resultado*/
  var resultado = valor1 + valor2;
  
  /*Guardamos en la conversaci�n el resultado de la operaci�n*/
  obj.conversation.parameters.resultado.value = resultado.toFixed(0);
  
  print("JOSEP");
  
  /*Devolvemos el objeto conversaci�n serializado en JSON*/
  return JSON.stringify(obj);
}







