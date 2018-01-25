<!DOCTYPE html>

<html lang="en">

    <head>

        <meta charset="UTF-8"/>

        <title>Moodle Video Platform</title>
      	
      	<link href="http://www.jq22.com/jquery/bootstrap-3.3.4.css" rel="stylesheet">
      	<script src="http://www.jq22.com/jquery/1.11.1/jquery.min.js"></script>

        <link href="/moodlevideo/css/pure-min.css" rel="stylesheet">

        <link href="/moodlevideo/css/main.css" rel="stylesheet">
        
        <script src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.js"></script>

        <script src="/moodlevideo/js/jquery-1.9.0.min.js"></script>

        <script src="/moodlevideo/js/main.js" type="text/javascript"></script>
        
        <script src="/moodlevideo/js/videoAnimation.js" type="text/javascript"></script>
        
        <link href="/moodlevideo/css/star-rating.css" media="all" rel="stylesheet" type="text/css"/>

        <script src="/moodlevideo/js/star-rating.js" type="text/javascript"></script>

	<script src="/moodlevideo/js/recorder.js"></script>
//you can use recorder1.js or recorder2.js for half size of wav and recorder3.js for 1/4 size of wav file
        <script src="/moodlevideo/js/audio.js"></script>

        <script src="/moodlevideo/js/wav2mp3.js"></script>
        
       <script type="text/javascript">

       function show_video()
       {
    	   var v =document.getElementById("video");
    	   v.style.display="block";
    	   var s =document.getElementById("show_btn");
    	   s.style.display="none";
    	   var h =document.getElementById("hidden_btn");
    	   h.style.display="block";
       }
       function hidden_video()
       {
    	   var v =document.getElementById("video");
    	   v.style.display="none";
    	   var s =document.getElementById("show_btn");
    	   s.style.display="block";
    	   var h =document.getElementById("hidden_btn");
    	   h.style.display="none";
       }
       
      
       
       </script>
       
       <script type="text/javascript">
       
       </script>
       
       <style type="text/css">
       #contentHolder{position:absolute;top:100px;left:150px;}
       #canvas{display:none;}
       #show_btn{position:absolute;top:140px;left:60px;}
       #hidden_btn{position:absolute;top:140px;left:60px;}
       </style>
       
        
    </head>

    <body onfocus="getFocus()" onblur="Empty_Focus()">
    <div class="container">
    <img src="/moodlevideo/images/bg.jpg" onload="rcOpen(this,'${recommendList}')" class="bg" /> 
	    <div class="top">
			<a class="topL"href="videoheatmap">${login_username}(${login_userid})</a>
	        <a class="topC"href="videorank">本周视频观看排行榜</a>
	       	<a class="topR"href="${video_moodle_url}">返回平台</a>
	    </div>
	    <div class="mid">
			<div class="des"> 	
				<div class="desOut" onmouseover="dsc_view(this,'${video_description}')"onmouseout="dsc_hide(this)">课程简介</div>
		    </div>
	    	<div class="wrapper">
		        <div class="pure-g">
		            <div class="pure-u-1">
		                <div class="player">
		                    <video  id="player" width="100%">
		                        <source src="http://software-moodle.oss-cn-qingdao.aliyuncs.com/moodle_vedio/${video_url}.mp4" type="video/mp4"></source>		                        
		                    </video>
		
		                    <div class="pure-g" id="controller">
		                        <div class="pure-u-1-12">
		                            <span id="play" class="icon icon-play"></span>
		                            <span id="stop" class="icon icon-stop"></span>
		                        </div>
		                        <div class="pure-u-13-24 overflow-h">
		                            <div id="progressBar" class="controlBar">
		                                <div id="innerBar" class="controlInner"></div>
		                            </div>
		                        </div>
		                        <div class="pure-u-1-4">
		                            <span id="timer">0:00</span>
		                            <span id="volume" class="icon icon-volume"></span>
		                            <div id="volume-control" class="controlBar">
		                                <div id="volume-inner" class="controlInner"></div>
		                            </div>
		                        </div>
		                        <div class="pure-u-1-8 overflow-h">
		                            <span id="expand" class="icon icon-expand"></span>   
		                        </div>
		                    </div>
	                	</div>
	            	</div>
	       		</div>
	        	<div class="pure-g">
	          		<div class="pure-u-1">
	            	<input type="file" id="file">
	            	<input id="temp_userid" type="hidden" value="${login_userid}">
	            	<input id="temp_sectionid" type="hidden" value="${video_sectionid}">
	            	</div>
	        	</div>
			</div>
		</div>
		<div class="right">
			<div class="rcmd">
			<font  class="rcmdTip" id ='rcmdTip'>猜你想看</font>
			</div>
		</div>
		<div class="bottom">
		    <div class="rate">		
		    	<div class="tip">难度打分</div>       	
		        <div class="star">
		            	<input id="input-grade" value="1" type="number" class="rating" min=0 max=5 step=0.5 data-size="md" >
		        </div>
		            <button id="ibt_button" type="submit" class="btn btn-primary" onclick="forbid_grade()">提交评价</button>
        	</div>
        </div>
    </div>
    
    
    
    
  
    <p id="contentHolder" >
    <video id="video" width="170" height="170	" autoplay ></video>  
    <canvas id="canvas" width="640" height="480"></canvas>
    <button id="show_btn" style="display:none"  class="btn btn-primary" onclick="show_video()">显示</button>
    <button id="hidden_btn" class="btn btn-primary" onclick="hidden_video()">隐藏</button>
	</p>
	
	<script type="text/javascript">
        var aVideo=document.getElementById('video');
        var aCanvas=document.getElementById('canvas');  
        var ctx=aCanvas.getContext('2d');
        navigator.getUserMedia  = navigator.getUserMedia ||  
                          navigator.webkitGetUserMedia ||  
                          navigator.mozGetUserMedia ||  
                          navigator.msGetUserMedia|| window.getUserMedia;
        navigator.getUserMedia({video:true,audio:true}, gotStream, noStream);  //Ã¥ÂÂÃ¦ÂÂ°1Ã¨ÂÂ·Ã¥ÂÂÃ§ÂÂ¨Ã¦ÂÂ·Ã¦ÂÂÃ¥Â¼ÂÃ¦ÂÂÃ©ÂÂÃ¯Â¼ÂÃ¥ÂÂÃ¦ÂÂ°Ã¤ÂºÂÃ¦ÂÂÃ¥ÂÂÃ¦ÂÂÃ¥Â¼ÂÃ¥ÂÂÃ¨Â°ÂÃ§ÂÂ¨Ã¯Â¼ÂÃ¥Â¹Â¶Ã¤Â¼Â Ã¤Â¸ÂÃ¤Â¸ÂªÃ¨Â§ÂÃ©Â¢ÂÃ¦ÂµÂÃ¥Â¯Â¹Ã¨Â±Â¡Ã¯Â¼ÂÃ¥ÂÂÃ¦ÂÂ°Ã¤Â¸ÂÃ¦ÂÂÃ¥Â¼ÂÃ¥Â¤Â±Ã¨Â´Â¥Ã¥ÂÂÃ¨Â°ÂÃ§ÂÂ¨Ã¯Â¼ÂÃ¤Â¼Â Ã©ÂÂÃ¨Â¯Â¯Ã¤Â¿Â¡Ã¦ÂÂ¯
        function gotStream(stream) {  
            video.src = URL.createObjectURL(stream);  
            video.onerror = function () {  
              stream.stop();  
            };  
            stream.onended = noStream;  
            video.onloadedmetadata = function () {  
              //alert('Ã¦ÂÂÃ¥ÂÂÃ¦ÂÂÃ¥Â¼ÂÃ¯Â¼Â');  
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
            		  alert("æªå¾ä¿å­å¤±è´¥");
            	  }
              }  
          });
      }
</script>
    
      <pre id="log" style="display:none;"></pre>
    
    </body>

</html>
