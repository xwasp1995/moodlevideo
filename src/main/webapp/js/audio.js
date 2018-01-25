  function __log(e, data) {
    log.innerHTML += "\n" + e + " " + (data || '');
  }

  var audio_context;
  var recorder;

  function startUserMedia(stream) {
    var input = audio_context.createMediaStreamSource(stream);
    __log('Media stream created.');

    // Uncomment if you want the audio to feedback directly
    //input.connect(audio_context.destination);
    //__log('Input connected to audio context destination.');
    
    recorder = new Recorder(input);
    document.getElementById('play').onclick=startRecording;

    __log('Recorder initialised.');
  }

  function startRecording(button) {
    recorder && recorder.record();
    document.getElementById('play').onclick=stopRecording;
    __log('Recording...');
  }

  function stopRecording(button) {
    recorder && recorder.stop();
/*     button.disabled = true;
    button.previousElementSibling.disabled = false; */
    __log('Stopped recording.');
    
    // create WAV download link using audio data blob
    //createDownloadLink();
    //alert("正在发送！");
    saveFile();
    document.getElementById('play').onclick=startRecording;
    
    recorder.clear();
  }

  function createDownloadLink() {
    recorder && recorder.exportWAV(function(blob) {
      var url = URL.createObjectURL(blob);
      var li = document.createElement('li');
      var au = document.createElement('audio');
      var hf = document.createElement('a');
      
      au.controls = true;
      au.src = url;
      hf.href = url;
      hf.download = new Date().toISOString() + '.wav';
      hf.innerHTML = hf.download;
      li.appendChild(au);
      li.appendChild(hf);
      recordingslist.appendChild(li);
    });
  }

  function saveFile() {
  //alert("正在发送！");
    recorder && recorder.exportWAV(function(blob) {
      
      //wav2mp3(blob, send(blob));
      send(blob);
      
    });
  }
  
  function send(blob) {
      	//alert("正在发送");
        var xhr = new XMLHttpRequest(),
            fd = new FormData();

        fd.append( 'file', blob);
        /* xhr.onreadystatechange = function(){

        var b = xhr.responseText;  

        if(xhr.readyState == 4 && xhr.status == 200){    
            alert(b);
        }

      	} */
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
  