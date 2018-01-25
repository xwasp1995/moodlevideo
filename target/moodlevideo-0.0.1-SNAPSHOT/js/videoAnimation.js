var gl={
	timer:0,//定时器句柄，用于及时取消未完成的动画
	state:0,//推荐列表的状态值
}
//随着屏幕大小设置背景
function setBg(bg){
	bg.style.width= document.body.scrollWidth+'px';
	bg.style.height=document.body.scrollHeight+'px';
}

//课程描述动画，受点击时展开
function dsc_view(node,str){
	node.innerHTML='';
	node.style.textIndent='25px';
	node.style.width = 670+'px';
	node.style.height=55+'px';
	node.style.fontSize=13+'px'
	node.style.textAlign='left';
	node.style.backgroundColor='rgba(0,0,0,0.7)';
	node.title = str;
	
	gl.timer = setTimeout(function(){
		node.innerHTML=str;
	},350);
}
//课程描述动画，受点击时缩小
function dsc_hide(node){
	if(gl.timer) clearTimeout(gl.timer);
	node.style.textIndent=0;
	node.style.width = 200+'px';
	node.style.height=30+'px';
	node.style.fontSize=20+'px'
	node.style.textAlign='center';
	node.style.backgroundColor='rgba(0,40,0,0.3)';
	node.innerHTML='课程简介';
}


//从推荐数据的字符串中提取出URL和视频描述字段
function splitData(data){
	var p = 0;
	var flag = 0;
	
	var arr = [],i=0;
	var arr2 = [],j=0;
	var arr3 = [];
	
	var k;
	
	while(data[p]!=']'){
		if(data[p]=='{'){
			arr[i++] = p;
			p++;
		}
		else if(data[p]=='}'){
			arr2[j++] = p;
			p++;
		}
		else{
			p++;
		}
	}
	
	var str = [];
	for(k=0;k<i;k++){
		str[k] = "";
		str[k] = data.substring(arr[k],arr2[k]+1);
	}
	for(k=0;k<i;k++)
		arr3[k]=str[k].indexOf('video_url');
	
	var str2 = [];
	for(k = 0;k<i;k++){
		str2[flag++] = str[k].substring(19,arr3[k]-2);
		str2[flag++] = str[k].substring(arr3[k]+10,str[k].length-1);
	}
	
	
	return str2;
}
//动态填充列表
function rcOpen(rc,data){
	var rc = document.getElementById('rcmdTip');
	if(data.length<10){//若为空则不给推荐显示
		var div1=document.createElement("div");
		div1.innerHTML="你看的视频太少了，我不知道你需要什么，再多看几课吧~"
		div1.className="rcBlock";
		div1.style.marginTop="10px";
		div1.style.whiteSpace = "normal";
		div1.style.width = "100%";
		
		rc.parentNode.appendChild(div1);
	}
	else{//如果非空，则显示
		var str = splitData(data);
		
		
		var limit = str.length;	
		for(var i = 0;i<limit;i+=2){
			var a=document.createElement("a");
			a.href=str[i+1];
			a.innerHTML=str[i];
			
			a.onclick = function(){//点击超链接调用记录函数
				sendRecommendBehavePostRequest(this);
			}
			
			var div=document.createElement("div");
			div.className="rcBlock";
			a.title = str[i];
			div.title = str[i];
			div.appendChild(a);
			rc.parentNode.appendChild(div);
		}
	}

}
