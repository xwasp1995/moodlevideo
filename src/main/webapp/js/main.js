var openPageTime = new Date(); // 打开页面时调用，记录打开页面时间
var pastTime = 0; // 上一状态的时间

// 禁止选择（五角）评分插件
function forbid_grade(){ 
    document.getElementById("ibt_button").disabled="disabled";
}

// 得到焦点时的动作处理
function getFocus(){ 
    var playerElement = document.getElementById("player"); 
    if(document.hasFocus()){
        // 回到页面
        sendPostRequestWaitMiSeconds(9);
    }else { //（一般没用）
        playerElement.pause();
        // 设置‘开始/暂停’按钮
        var play = document.getElementById("play"); 
        play.setAttribute("class", "icon icon-play"); 

        sendPostRequestWaitMiSeconds(8);
    }
    
} 
// 失去焦点时的动作处理
function Empty_Focus(){ 
    var playerElement = document.getElementById("player"); 
    if(document.hasFocus()){ //（一般没用）
        sendPostRequestWaitMiSeconds(9); 
    }else {
        // 隐藏页面
        playerElement.pause();
        // 设置‘开始/暂停’按钮
        var play = document.getElementById("play"); 
        play.setAttribute("class", "icon icon-play"); 
        
        sendPostRequestWaitMiSeconds(8);
    }
} 
// 对隐藏/恢复页面动作做数毫秒的延迟发送
function sendPostRequestWaitMiSeconds(seleNum){
    setTimeout( function(){sendPostRequest(seleNum);}, 400 );
}



$(function() {
    var
        $player = $('#player'),
        $play = $('#play'),
        $stop = $('#stop'),
        $volume = $('#volume'),
        $expand = $('#expand'),
        $upload = $('#upload');

    var player = $player[0];
    var
        $file = $('#file'),
        $timer = $('#timer');
    var
        $progressBar = $('#progressBar'),
        $innerBar = $('#innerBar'),
        $volumeControl = $('#volume-control'),
        $volumeInner = $('#volume-inner'),
        $ibt_button = $('#ibt_button');


    //获取进入界面的时间如下：
    var start_hour = openPageTime.getHours(); //获取开始时的系统时间：小时
    var start_minute = openPageTime.getUTCMinutes(); //获取开始时的系统时间：分钟
    var start_second = openPageTime.getSeconds(); //获取开始时的系统时间：秒
    console.log("登入或刷新页面的时间  " + start_hour + ":" + start_minute + ":" + start_second);

    var heart_flag = 2;  // 用于timeupdate时发送心跳包
    var tip15_flag = 15; // 用于每15分钟触发一次"休息一下"
    var cookie_userid = document.getElementById("temp_userid").value;
    var cookie_sectionid = document.getElementById("temp_sectionid").value;
    setCookie("MOODLE_VIDEO_USERID", cookie_userid, 60);
    setCookie("MOODLE_VIDEO_SECTIONID", cookie_sectionid, 60);


    // 记录视频观看记录事件
    if(cookie_sectionid!=null && cookie_sectionid!=""){
        var post_url = '/moodlevideo/videojob/behavior';
        var current_time = getNowFormatDate();

        $.ajax({
            url: post_url,
            type: 'post',
            dataType: 'json',
            data: { userid: cookie_userid, sectionid: cookie_sectionid,
                 behave: 1, happentime: current_time },
            error:function(){
                alert('video_watch_record ajax post_request error!');
            },
            success:function(data){
                //alert('success');
                console.log(data);
            }
        })

    }

    // 记录视频播放结束行为
    player.onended = function() 
    {
        sendPostRequest(15);
    }

    // 记录离开页面（统计观看时长）事件
    var thisPage = false;
    window.onunload = function checkLeave(e) {

        var evt = e ? e : (window.event ? window.event : null); //此方法为了在firefox中的兼容$ajax等等
        if (!thisPage){
            evt.returnValue = '离开会使编写的内容丢失。';  
            alert("离开页面");
        } 

        // 记录离开页面（统计观看时长）事件
        sendPostRequestByAsync(7);
    }

    
    $ibt_button
        .on('click', function() {
            var post_url = '/moodlevideo/videojob/rating';
            var video_time = getNowVideoPageTime(openPageTime); // 获取页面打开时间
            var current_time = getNowFormatDate();
            var star_grade = document.getElementById("input-grade").value;
            
            // 打分（评星）事件
            if(cookie_sectionid!=null && cookie_sectionid!=""){

                $.ajax({
                    url: post_url,
                    type: 'post',
                    dataType: 'json',
                    data: { userid: cookie_userid, sectionid: cookie_sectionid,
                         videotime: video_time, happentime: current_time, 
                         stargrade: star_grade },
                    error:function(){
                        alert('ibt_button ajax post_request error!');
                    },
                    success:function(data){
                        //alert('success');
                        console.log(data);
                        //alert(data.Hello);
                    }
                })
            }
        });    


    $play
        .on('click', function() {
            if (player.paused) {
                player.play();
                $(this).removeClass('icon-play').addClass('icon-pause');
                //console.log("播放总时间：" + player.duration.toFixed(3));
                //console.log("播放");
            
                // 点击“开始”事件
               sendPostRequest(2);

            } else {
                player.pause();
                $(this).removeClass('icon-pause').addClass('icon-play');
                //console.log("暂停");

                // 点击“暂停”事件
                sendPostRequest(3);

            }
        });

    $stop
        .on('click', function() {
            //console.log("重新开始");
            player.currentTime = 0;
            $innerBar.css('width', 0 + 'px');
            
            // 点击“重新开始”事件
            sendPostRequest2(4);

        });

    $volume
        .on('click', function() {
            if (player.muted) {
                player.muted = false;
                $(this).removeClass('icon-volume-mute').addClass('icon-volume');
                $volumeInner.css('width', 100 + '%');
                sendPostRequest(13); // 解除静音
            } else {
                player.muted = true;
                $(this).removeClass('icon-volume').addClass('icon-volume-mute');
                $volumeInner.css('width', 0);
                sendPostRequest(12); // 静音
            }
        });



    $expand
        .on('click', function() {
        	var str = getBrowser();
            if (str=='Chrome'||str=='Safari') {
            	if(!document.webkitIsFullScreen){
	                sendPostRequest(10);
	                player.webkitRequestFullScreen(); //全屏
	                $(this).removeClass('icon-expand').addClass('icon-contract');
	                //console.log("全屏");
            	}
            	else{
                    sendPostRequest(11);
                    document.webkitCancelFullScreen();
                    $(this).removeClass('icon-contract').addClass('icon-expand');
                    //console.log("非全屏");
            	}
            }else if(str=='Firefox'){
            	if(!document.mozIsFullScreen){
	            	sendPostRequest(10);
	                player.mozRequestFullScreen(); //全屏
	                $('#expand').removeClass('icon-expand').addClass('icon-contract');
	                //console.log("全屏");
            	}   
            	else{
                    sendPostRequest(11);
                    document.mozCancelFullScreen();
                    $('#expand').removeClass('icon-contract').addClass('icon-expand');
                    //console.log("非全屏");
            	}
            }
            else{
            	if(!document.msIsFullScreen){
	               	sendPostRequest(10);
	                player.msRequestFullScreen(); //全屏
	                $(this).removeClass('icon-expand').addClass('icon-contract');
	                //console.log("全屏");
               	}   
               	else{
                    sendPostRequest(11);
                    document.msCancelFullScreen();
                    $(this).removeClass('icon-contract').addClass('icon-expand');
                    //console.log("非全屏");
               	}
            }
        });

    $upload
        .on('click', function() {
            $file.trigger('click');
        });

    $file
        .on('change', function(e) {
            var file = e.target.files[0],
                canPlayType = player.canPlayType(file.type);

            if (canPlayType === 'maybe' || canPlayType === 'probably') {
                src = window.URL.createObjectURL(file);
                player.src = src;
                $play.removeClass('icon-pause').addClass('icon-play'); //新打开的视频处于paused状态
                player.onload = function() {
                    window.URL.revokeObjectURL(src);
                };
            } else {
                alert("浏览器不支持您选择的文件格式");
            }
        });

    $player
        .on('timeupdate', function() {

            // 每两分钟发一个心跳包
            var curTime = new Date();
            var duration = (curTime.getTime() / 1000 - openPageTime.getTime() / 1000).toFixed(3);
            var f_minutes = Math.floor(duration / 60);
            if(f_minutes >= heart_flag){
                //console.log("++++++**" + f_minutes);
                heart_flag = f_minutes + 2;
                sendPostRequest(16);
            }

            // 每15分钟触发一次“休息一下”提示
            if(f_minutes >= tip15_flag){
                player.pause(); // 视频自动暂停
                sendPostRequest(3); // 发送’暂停‘行为记录
                tip15_flag = f_minutes + 15;
                // 改变‘开始/暂停’按钮状态
                $play.removeClass('icon-pause').addClass('icon-play');

                alert("你已经学习了很长时间，休息一下继续！");
            }
            
            
            //秒数转换
            var time = player.currentTime.toFixed(3), //小数点后三位
                minutes = Math.floor((time / 60) % 60),
                seconds = Math.floor(time % 60);
            if (time - pastTime > 1) {
                // 点击“快进”事件
                sendPostRequest2(5);
                //console.log("快进了" + (time - pastTime).toFixed(3) + "秒");
            }else if (pastTime - time > 1) {                
                // 点击“后退”事件
               sendPostRequest2(6);
               //console.log("回退了" + (pastTime - time).toFixed(3) + "秒");
            }

            pastTime = time;

            //console.log("当前播放时间" + time);


            if (seconds < 10) {
                seconds = '0' + seconds;
            }

            $timer.text(minutes + ':' + seconds);

            var w = $progressBar.width();
            if (player.duration) {
                var per = (player.currentTime / player.duration).toFixed(3);
                window.per = per;
            } else {
                per = 0;
            }
            $innerBar.css('width', (w * per).toFixed(0) + 'px');

            if (player.ended) { //播放完毕
                $play.removeClass('icon-pause').addClass('icon-play');
                //console.log("播放完毕");
            }
        });



    $progressBar
        .on('click', function(e) {
            var w = $(this).width(),
                x = e.offsetX;
            window.per = (x / w).toFixed(3); //全局变量

            var duration = player.duration;
            player.currentTime = (duration * window.per).toFixed(0);

            $innerBar.css('width', x + 'px');
        });

    $volumeControl
        .on('click', function(e) {
            var w = $(this).width(),
                x = e.offsetX;
            window.vol = (x / w).toFixed(1); //全局变量

            player.volume = window.vol;
            $volumeInner.css('width', x + 'px');
            sendPostRequest(14); // 音量控制
        });

    $(document)
        .on('webkitfullscreenchange', function(e) {
            var w = $progressBar.width(),
                w1 = $volumeControl.width();
            if (window.per) {
                $innerBar.css('width', (window.per * w).toFixed(0) + 'px');
            }
            if (window.vol) {
                $volumeInner.css('width', (window.vol * w1).toFixed(0) + 'px')
            }
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
    return_data = temp.submit();
    return return_data;
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

// 清除cookie
function clearCookie(name) {
    setCookie(name, "", -1);
}

// 设置cookie
function setCookie(name, value, days) {
    days = days || 0; //days有值就直接赋值，没有为0
    var expires = "";
    if (days != 0 ) { //设置cookie生存时间
        var date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));
        expires = "; expires="+date.toGMTString();
    }
    document.cookie = name+"="+escape(value)+expires+"; path=/"; //转码并赋值
}

// “yyyy-MM-dd HH:MM:SS” 获取当前系统标准时间
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
            + " " + date.getHours() + seperator2 + date.getMinutes()
            + seperator2 + date.getSeconds();
    return currentdate;
}

// 获取当前视频播放时间点
function getNowVideoTime() {
    
    var time = player.currentTime.toFixed(3), //小数点后三位
        minutes = Math.floor(time / 60),
        seconds = Math.floor(time % 60);

    if (minutes >= 0 && minutes <= 9) {
        minutes = "0" + minutes;
    }
    if (seconds >= 0 && seconds <= 9) {
        seconds = "0" + seconds;
    }
    var currenttime = minutes + ":" + seconds;

    return currenttime;
}

// 获取上一时刻状态的视频播放时间点
function getPastVideoTime(ptime) {
    
    var minutes = Math.floor(ptime / 60),
        seconds = Math.floor(ptime % 60);

    if (minutes >= 0 && minutes <= 9) {
        minutes = "0" + minutes;
    }
    if (seconds >= 0 && seconds <= 9) {
        seconds = "0" + seconds;
    }
    var pasttime = minutes + ":" + seconds;

    return pasttime;
}

// 获取当前视频页面打开时间
function getNowVideoPageTime(openPageTime) {
    var curTime = new Date();
    var duration = (curTime.getTime() / 1000 - openPageTime.getTime() / 1000).toFixed(3);
    var minutes = Math.floor(duration / 60),
        seconds = Math.floor(duration % 60)

    if (minutes >= 0 && minutes <= 9) {
        minutes = "0" + minutes;
    }
    if (seconds >= 0 && seconds <= 9) {
        seconds = "0" + seconds;
    }
    var currenttime = minutes + ":" + seconds;

    return currenttime;
}


// （非进入页面、重新开始、快进、后退以外的其他行为）行为操作公用请求发送函数
function sendPostRequest(requestNo){

    var cookie_userid = document.getElementById("temp_userid").value;
    var cookie_sectionid = document.getElementById("temp_sectionid").value;
    // 确定当前观看视频id不为空
    if(cookie_sectionid!=null && cookie_sectionid!=""){
        var post_url = '/moodlevideo/videojob/behavior';
        var video_time = getNowVideoPageTime(openPageTime); // 获取页面打开时间
        var start_time = getNowVideoTime();   // 获取当前视频播放时间点
        var current_time = getNowFormatDate();

        $.ajax({
            url: post_url,
            type: 'post',
            dataType: 'json', 
            data: { userid: cookie_userid, sectionid: cookie_sectionid, starttime:start_time,
                 behave: requestNo, duration: video_time, happentime: current_time },
            error:function(){
                console.log('video_mix_post behave:' + requestNo + ' ajax post_request error!');
                if(requestNo==2 || requestNo==16){ // 判断是否断网
                    alert("网络连接失败，请检查计算机网络！");
                }
            },
            success:function(data){
                //alert('success');
                console.log(data);
            }
        })
    }

}


// （进入页面、重新开始、快进、后退行为）行为操作公用请求发送函数
function sendPostRequest2(requestNo){

    var cookie_userid = document.getElementById("temp_userid").value;
    var cookie_sectionid = document.getElementById("temp_sectionid").value;
    // 确定当前观看视频id不为空
    if(cookie_sectionid!=null && cookie_sectionid!=""){
        var post_url = '/moodlevideo/videojob/behavior';
        var video_time = getNowVideoPageTime(openPageTime); // 获取页面打开时间
        var start_time = getPastVideoTime(pastTime); // 获取上一时刻状态的视频播放时间点
        var end_time = getNowVideoTime();  // 获取当前视频播放时间点
        var current_time = getNowFormatDate();

        $.ajax({
            url: post_url,
            type: 'post',
            dataType: 'json', 
            data: { userid: cookie_userid, sectionid: cookie_sectionid,
                    behave: requestNo, starttime: start_time, endtime: end_time,
                    duration: video_time, happentime: current_time },
            error:function(){
                console.log('video_mix_post behave:' + requestNo + ' ajax post_request error!');
            },
            success:function(data){
                //alert('success');
                console.log(data);
            }
        })
    }

}


// 行为操作公用请求发送函数（ajax非异步发送）
function sendPostRequestByAsync(requestNo){

     var cookie_userid = document.getElementById("temp_userid").value;
    var cookie_sectionid = document.getElementById("temp_sectionid").value;
    // 确定当前观看视频id不为空
    if(cookie_sectionid!=null && cookie_sectionid!=""){
        var post_url = '/moodlevideo/videojob/behavior';
        var video_time = getNowVideoPageTime(openPageTime); // 获取页面打开时间
        var start_time = getNowVideoTime();   // 获取当前视频播放时间点
        var current_time = getNowFormatDate();

        $.ajax({
            async:false, // 同步发送
            url: post_url,
            type: 'post',
            dataType: 'json', 
            data: { userid: cookie_userid, sectionid: cookie_sectionid, starttime:start_time,
                 behave: requestNo, duration: video_time, happentime: current_time },
            error:function(){
                console.log('video_mix_post behave:' + requestNo + ' ajax post_request error!');
                if(requestNo==2 || requestNo==16){ // 判断是否断网
                    alert("网络连接失败，请检查计算机网络！");
                }
            },
            success:function(data){
                //alert('success');
                console.log(data);
            }
        })
    }

}


//从URL字段中提取sectionId
function getSectionId(URL){
	var sectionId = URL.substring(URL.indexOf('sectionid=')+10,URL.length);
	return sectionId;
}
// 发送“点击推荐列表行为”请求发送函数
function sendRecommendBehavePostRequest(object){
    var videoId = getSectionId(object.href);
    
    var cookie_userid = document.getElementById("temp_userid").value;
    var cookie_sectionid = document.getElementById("temp_sectionid").value+ "-" +videoId;
    // 确定当前观看视频id不为空
    if(cookie_sectionid!=null && cookie_sectionid!=""){
        var post_url = '/moodlevideo/videojob/behavior';
        var video_time = getNowVideoPageTime(openPageTime); // 获取页面打开时间
        var start_time = getNowVideoTime();   // 获取当前视频播放时间点
        var current_time = getNowFormatDate();

        $.ajax({
            url: post_url,
            type: 'post',
            dataType: 'json', 
            data: { userid: cookie_userid, sectionid: cookie_sectionid, starttime:start_time,
                 behave: 17, duration: video_time, happentime: current_time },
            error:function(){
                console.log('video_mix_post behave:' + requestNo + ' ajax post_request error!');
                if(requestNo==2 || requestNo==16){ // 判断是否断网
                    alert("网络连接失败，请检查计算机网络！");
                }
            },
            success:function(data){
                //alert('success');
                console.log(data);
            }
        })
    }

}


// 返回浏览器型号
function getBrowser() {  
    var ua = window.navigator.userAgent;  
    var isIE = window.ActiveXObject != undefined && ua.indexOf("MSIE") != -1;  
    var isFirefox = ua.indexOf("Firefox") != -1;  
    var isOpera = window.opr != undefined;  
    var isChrome = ua.indexOf("Chrome") && window.chrome;  
    var isSafari = ua.indexOf("Safari") != -1 && ua.indexOf("Version") != -1;  
    if (isIE) {  
        return "IE";  
    } else if (isFirefox) {  
        return "Firefox";  
    } else if (isOpera) {  
        return "Opera";  
    } else if (isChrome) {  
        return "Chrome";  
    } else if (isSafari) {  
        return "Safari";  
    } else {  
        return "Unkown";  
    }  
}  

