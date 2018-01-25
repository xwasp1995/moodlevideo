<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Moodle Video Platform</title>
    <link href="http://www.jq22.com/jquery/bootstrap-3.3.4.css" rel="stylesheet">
    <script src="http://www.jq22.com/jquery/1.11.1/jquery.min.js"></script>
    <link href="${pageContext.request.contextPath}/css/star-rating.css" media="all" rel="stylesheet" type="text/css"/>
    <script src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.js"></script>
    <script src="${pageContext.request.contextPath}/js/star-rating.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-1.9.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
    <script type="text/javascript">
         function isCanvasSupported(){
             var elem = document.createElement('canvas');
             return !!(elem.getContext && elem.getContext('2d'));
         };
    </script>
</head>
<body>
    <input type="file" id="myImage" name="myImage"/>   
    <input type="button" onclick="uploadFile();" value="上传"> 
    <p id="contentHolder">
    <video id="video" width="100" height="100" autoplay></video>  
    <button id="snap" value="qwe" action="qwe">Snap Photo</button>  
    <canvas id="canvas" width="640" height="480"></canvas>
</p>
<script type="text/javascript">
        var aVideo=document.getElementById('video');
        var aCanvas=document.getElementById('canvas');  
        var ctx=aCanvas.getContext('2d');
        navigator.getUserMedia  = navigator.getUserMedia ||  
                          navigator.webkitGetUserMedia ||  
                          navigator.mozGetUserMedia ||  
                          navigator.msGetUserMedia|| window.getUserMedia;
        navigator.getUserMedia({video:true,audio:true}, gotStream, noStream);  //åæ°1è·åç¨æ·æå¼æéï¼åæ°äºæåæå¼åè°ç¨ï¼å¹¶ä¼ ä¸ä¸ªè§é¢æµå¯¹è±¡ï¼åæ°ä¸æå¼å¤±è´¥åè°ç¨ï¼ä¼ éè¯¯ä¿¡æ¯
        function gotStream(stream) {  
            video.src = URL.createObjectURL(stream);  
            video.onerror = function () {  
              stream.stop();  
            };  
            stream.onended = noStream;  
            video.onloadedmetadata = function () {  
              //alert('æåæå¼ï¼');  
            };  
        }  
        function noStream(err) {  
            alert(err);  
      }  

      var cnt=0;
      setInterval("pictures()", 5000);

      function pictures() {
          ctx.drawImage(aVideo, 0, 0, 500, 500);
          var imgData = aCanvas.toDataURL("image/png");
          var base64Data = imgData.split(",")[1];
          $.ajax({  
              type : "POST",  
              url : '${pageContext.request.contextPath}/videojob/uploadPic',  
              data : {data:imgData},  
              timeout : 60000,  
              success : function(data){
            	  if("1" != data) {
            		  alert("截图保存失败");
            	  }
              }  
          });
      }
</script>
    </body>
</html>
