var recognition = null;

$(document).ready(function () {
    if (!('webkitSpeechRecognition' in window)) {
        //Speech recognition no soportado en el navegador
        console.log("Speech recognition not supported in browser");
    } else {
        //Objeto SpeechRecognition
         recognition = new webkitSpeechRecognition();

        //El reconocimiento de voz es continuo
        recognition.continuous = true;

        //Los resultados son devueltos son definitivos y no cambiar�n
        recognition.interimResults = false;

        //Lenguaje del reconocimiento
        recognition.lang = "es";
        
        //Inicio del reconocimiento de voz
        recognition.onstart = function () {
            
        };

        //Tenemos un comando de voz detectado
        recognition.onresult = function (event) {
        	$("#microphone-btn").fadeTo( "slow" , 0.6);
        	
        	//Obtenemos el id de resultado
            var resultIndex = event.resultIndex;

            //Obtenemos lo que nos ha dicho el usuario
            if (event.results[resultIndex].isFinal) {
            	$("#ariadna-text-input").val(event.results[resultIndex][0].transcript.trim());
            	sendMessage(globalOptions);
            }
            
            this.stop();
        };

        //Error en el reconocimiento de voz
        recognition.onerror = function (event) {
            console.log(event);
        };

        //Fin de reconocimiento de voz
        recognition.onend = function () {
        	
        };
    }
});

function startRecording(){
	recognition.start();
}