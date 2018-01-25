<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>  
<html lang="en">  
<head>  
    <meta http-equiv="Content-Type" content="text/html" charset="UTF-8">  
    <title>Login</title>  

    <script src="http://www.jq22.com/jquery/1.11.1/jquery.min.js"></script>
    <link href="/moodlevideo/css/login.css" rel="stylesheet" type="text/css"/>  
</head>  
<body onfocus="getFocus()" onblur="Empty_Focus()">  
    <img src="/moodlevideo/images/bg.jpg" onload="setBg(this)"class="bg" /> 
<div class="container">
    <div id="login">  
        <h1>Login</h1>  
        <input id="userid" type="text" required="required" placeholder="id" ></input>  
        <input id="password" type="password" required="required" placeholder="password" ></input> 
        <input id="temp_sectionid" type="hidden" value="${sectionid}">   
        <button id="login_btn" class="but" type="submit">登录</button> 
        
        <div style="font-size:18px; margin: 5px 0px; ">
        <font color="#DC143C">
            <b>${login_error}</b>
        </font>
		</div>
	</div> 
</div> 
</body> 

<script>
	function setBg(bg){
		console.log(document.body.scrollWidth);
		bg.style.width= document.body.scrollWidth+'px';
		bg.style.height=document.body.scrollHeight+'px';
	}

    $(function() {
        var
            $login_btn = $('#login_btn');

        if(getCookie("MOODLE_VIDEO_USERID") != null){
            var userid = getCookie("MOODLE_VIDEO_USERID");
            var sectionid = document.getElementById("temp_sectionid").value;
            var post_url = '/moodlevideo/videojob/section/sectionid='+sectionid;

            // 设置无效
            document.getElementById("userid").value = userid;  
            document.getElementById("password").value = userid; 
            document.getElementById("userid").setAttribute("disabled", "");
            document.getElementById("password").setAttribute("disabled", "");
            document.getElementById("login_btn").setAttribute("disabled", "");
            
            console.log(document.cookie);
            post(post_url, { userid: userid, sectionid: sectionid });
        }

        $login_btn
            .on('click', function(e) {
	           	var userid = document.getElementById("userid").value;
	           	var sectionid = document.getElementById("temp_sectionid").value;
	            var post_url = '/moodlevideo/videojob/section/sectionid='+sectionid;
	                
	            post(post_url, { userid: userid, sectionid: sectionid }); 
            	
            });

    });

    function post(urls, params) {
        var temp = document.createElement("form");
        temp.action = urls;
        temp.method = "post";
        temp.style.display = "none";
        for (var x in params) {
            var opt = document.createElement("input");
            opt.name = x;
            opt.value = params[x];
            temp.appendChild(opt);

        }
        document.body.appendChild(temp);
        temp.submit();
        return temp;
    }

    // 取得cookie
    function getCookie(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';'); //把cookie分割成组
        for(var i=0;i < ca.length;i++) {
           var c = ca[i]; //取得字符串
            while (c.charAt(0)==' ') { //判断一下字符串有没有前导空格
                c = c.substring(1,c.length); //有的话，从第二位开始取
            }
            if (c.indexOf(nameEQ) == 0) { //如果含有我们要的name
                return unescape(c.substring(nameEQ.length,c.length)); //解码并截取我们要值
            }
        }
        return null;
    }
</script>

</html>  