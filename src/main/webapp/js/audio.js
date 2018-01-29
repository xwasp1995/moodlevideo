  function __log(e, data) {
    log.innerHTML += "\n" + e + " " + (data || '');
  }

  var audio_context;
  var recorder;
  var flag_watching = 0;
  
  function startUserMedia(stream) {
    var input = audio_context.createMediaStreamSource(stream);
    __log('Media stream created.');
    
    recorder = new Recorder(input);
    document.getElementById('play').onclick=startRecording;
    var int=self.setInterval("clock()",5*60000);
    

    __log('Recorder initialised.');
  }
  
  function clock(){
	if(flag_watching == 0){return;}
	recorder && recorder.record();
	setTimeout(saveAndStop(),5000);
  }
  
  function saveAndStop(){
	  recorder && recorder.stop();
	  saveFile();
	  recorder.clear();
  }

  function startRecording(button) {
	flag_watching = 1;
    recorder && recorder.record();
    document.getElementById('play').onclick=stopRecording;
    __log('Recording...');
    
  }

  function stopRecording(button) {
	flag_watching = 0;
    recorder && recorder.stop();
    __log('Stopped recording.');

    saveFile();
    document.getElementById('play').onclick=startRecording;
    
    recorder.clear();
  }

  function saveFile() {
  //alert("正在发送！");
    recorder && recorder.exportWAV(function(blob) {
      //wav2mp3(blob, send(blob));
      send(blob);
      
    });
  }
  
  function send(blob) {
    	alert("正在发送");
      var xhr = new XMLHttpRequest(),
          fd = new FormData();

      //fd.append( 'file', blob, Date.now()+"_"+${login_username}+"_"+${video_sectionid});
      fd.append( 'file', blob);
      fd.append( 'name', Date.now());
      fd.append( 'time', Date.now());
      //fd.append( 'username', ${login_username});
      //fd.append( 'videoid', ${video_sectionid});
      
      
      xhr.onreadystatechange = function(){

      var b = xhr.responseText;  

      if(xhr.readyState == 4 && xhr.status == 200){    
          alert(b);
      }

    	} 
      xhr.open('POST', "http://101.201.68.238/soundRecord/demo/file_upload/upload1.php");
      xhr.send( fd );
      //alert("发送成功")
	  } 

  window.onload = function init() {
    try {
      // webkit shim
      window.AudioContext = window.AudioContext || window.webkitAudioContext;
      navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia;
      window.URL = window.URL || window.webkitURL;
      
      audio_context = new AudioContext;
      __log('Audio context set up.');
      __log('navigator.getUserMedia ' + (navigator.getUserMedia ? 'available.' : 'not present!'));
    } catch (e) {
      alert('No web audio support in this browser!');
    }
    
    navigator.getUserMedia({audio: true}, startUserMedia, function(e) {
      __log('No live audio input: ' + e);
    });
  };
  