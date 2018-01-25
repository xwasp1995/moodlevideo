<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <title>视频观看情况</title>
    <script src="/moodlevideo/js/echarts.min.js"></script>
    <script src="/moodlevideo/js/jquery-1.9.0.min.js"></script>

    <style type="text/css">
        body{ background-color: #D8EFFD;} 
    </style>
</head>
<body>
    <!-- 为ECharts准备一个具备大小（宽高）的Dom -->

    <div id="main1" style="width: 700px;height:700px;float:left;"></div>
    <div id="main2" style="width: 700px;height:700px;float:left;"></div>
    <div id="main3" style="width: 1000px;height:900px;float:left;"></div>
    
    <!-- ECharts单文件引入 -->
    <script src="http://echarts.baidu.com/build/dist/echarts.js"></script>

    <script type="text/javascript">

        var userId;
        if(getCookie("MOODLE_VIDEO_USERID") != null){
            userId = getCookie("MOODLE_VIDEO_USERID");
        }

        // 路径配置
        require.config({
            paths: {
                echarts: 'http://echarts.baidu.com/build/dist'
            }
        });

        var myChart1 = echarts.init(document.getElementById('main1'));
        var myChart2 = echarts.init(document.getElementById('main2'));
        var myChart3 = echarts.init(document.getElementById('main3'));
        // 显示标题，图例和空的坐标轴
       
        // 使用
        require(
            [
                'echarts',
                'echarts/chart/treemap'
            ],
            function (ec) {
                // 热力图
                $.ajax(
                    {
                        type:'post',
                        url : '/moodlevideo/videojob/getvideoheatmapdata?type=1&userid=' + userId,
                        dataType : 'jsonp',
                        jsonp:"jsoncallback",
                        success  : function(data) {
                            //alert("success");
                            // 解析json串
                            var data2=[
                                [0,0,0],
                                [1,0,0],
                                [2,0,0],
                                [3,0,0],

                                [0,1,0],
                                [1,1,0],
                                [2,1,0],
                                [3,1,0],
                                [4,1,0],
                                [5,1,0],

                                [0,2,0],
                                [1,2,0],
                                [2,2,0],
                                [3,2,0],
                                [4,2,0],
                                [5,2,0],
                                [6,2,0],
                                [7,2,0],

                                [0,3,0],
                                [1,3,0],
                                [2,3,0],
                                [3,3,0],

                                [0,4,0],
                                [1,4,0],
                                [2,4,0],
                                [3,4,0],
                                [4,4,0],

                                [0,5,0],
                                [1,5,0],
                                [2,5,0],
                                [3,5,0],
                                [4,5,0],

                                [0,6,0],
                                [1,6,0],
                                [2,6,0],
                                [3,6,0],
                                [4,6,0],
                                [5,6,0],
                                [6,6,0],
                                [7,6,0],
                                [8,6,0],

                                [0,7,0],
                                [1,7,0],
                                [2,7,0],
                                [3,7,0],
                                [4,7,0],
                                [5,7,0],

                                [0,8,0],
                                [1,8,0],
                                [2,8,0],
                                [3,8,0],
                                [4,8,0],
                                [5,8,0],
                                [6,8,0],

                                [0,9,0],
                                [1,9,0],
                                [2,9,0],
                                [3,9,0],
                                [4,9,0],

                                [0,10,0],
                                [1,10,0],
                                [2,10,0],
                                [3,10,0],
                                [4,10,0],
                                [5,10,0],
                                [6,10,0],
                                [7,10,0],
                                [8,10,0],
                                [9,10,0],
                                [10,10,0],

                                [0,11,0],
                                [1,11,0],
                                [2,11,0],
                                [3,11,0],
                            ];
                            data1 = data.data;
                            for(var i=0;i<data1.length;i++){
                                x=data1[i][1];
                                y=data1[i][0];
                                for(var j=0;j<data2.length;j++){
                                    if (data2[j][0]==x && data2[j][1]==y){
                                        data2[j][2]=data1[i][2];
                                    }
                                }
                            }
                            var hours = ['第1节', '第2节', '第3节', '第4节', '第5节', '第6节', '第7节', '第8节', '第9节','第10节','第11节'];
                            var days = ['第1章 软件工程学概述','第2章 可行性研究','第3章 需求分析','第4章 形式化说明技术','第5章 总体设计','第6章 详细设计','第7章 实现','第8章 维护','第9章 面向对象方法学引论',
                            '第10章 面向对象分析','第11章 面向对象设计','第12章 面向对象实现'];
                        
                            myChart1.setOption({
                                tooltip: {
                                    position: 'top',
                                    formatter: function (params) {
                                        return  ''+days[params.value[1]]+'<br/>'+ ''+hours[params.value[0]]+'<br/>'+' 观看视频： ' +params.value[2] ;
                                    }
                                },
                                 title : {
                                    text: '视频观看热力图'
                                },
                                animation: false,
                                grid: {
                                    left: '3%',       
                                    right: '8%',
                                    bottom: '8%',
                                    containLabel: true
                                },
                                xAxis: {
                                    type: 'category',
                                    data: hours,
                                    splitArea: {
                                        show: true
                                    },
                                    name:'小节'
                                },
                                yAxis: {
                                    type: 'category',
                                    data: days,
                                    splitArea: {
                                        show: true
                                    },
                                    name:'章'
                                },
                                visualMap: {
                                    min: 0,
                                    max: 100,
                                    calculable: true,
                                    orient: 'horizontal',
                                    left: 'center',
                                    bottom: '1%',
                                    inRange: {
                                        //color: ['#50a3ba','#eac736','#d94e5d'].reverse()
                                        color: ['#CC3300','#eac736','#FFFFB9'].reverse()
                                    },
                                    textStyle: {
                                        color: '#000'
                                    }
                                },
                                series: [{
                                    name: 'Punch Card',
                                    type: 'heatmap',
                                    data: data2,
                                    label: {
                                        normal: {
                                            show: true,
                                            textStyle: {
                                                color: '#000'
                                            },
                                        }
                                    },
                                    itemStyle: {
                                        emphasis: {
                                            shadowBlur: 10,
                                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                                        }
                                    }
                                }]
                            });
                        },
                        error : function() {
                            alert('fail-1');
                        }
                    }
                );
                // 和弦图
                $.ajax(
                    {
                        type:'post',
                        url : '/moodlevideo/videojob/getvideoheatmapdata?type=3&userid=' + userId,
                        dataType : 'jsonp',
                        jsonp:"jsoncallback",
                        success  : function(data) {
                            data1 = data.data;
                            var zhangs = ['1章','2章','3章','4章','5章','6章','7章','8章','9章',
                                '10章','11章','12章','13章'];
                            var node=[];
                            var link=[];
                            var x,y;
                            node.push({name:'软工',symbolSize:70});
                            for(var i=0;i<data1.length;i++){
                                x=data1[i][0]+1;
                                y=data1[i][1]+1;
                                var zhang = zhangs[data1[i][0]];
                                var n=zhang+y+'节';
                                node.push({name:n,symbolSize:data1[i][2]/6000,label:n,label1:data1[i][2]});
                                link.push({source:'软工',target:n,weight: 1,name:data1[i][2]} );
                            }
                            
                            myChart3.setOption({
                                 title: {
                                    text: '（所有用户）知识点观看和弦图'
                                },
                                tooltip : {
                                    trigger: 'item',
                                    formatter: function (params) {
                                        if (params.dataType=='edge'){
                                            return params.data.target +':'+params.data.name
                                        }else if (params.dataType=='node'){
                                            return params.data.name +':'+params.data.label1
                                        }
                                    }
                                },
                                animationDurationUpdate: 1500,
                                animationEasingUpdate: 'quinticInOut',
                                
                                series : [
                                    {
                                        name: '视频观看',
                                        type: 'graph',
                                        layout: 'circular',
                                        circular: {
                                            rotateLabel: true
                                        },
                                        data: node,
                                        links: link,
                                        label: {
                                            show:true,
                                            normal: {
                                                position: 'bottom',
                                                show:true,
                                                formatter: '{b}'
                                            }
                                        },
                                         lineStyle: {
                                            normal: {
                                                color: 'source',
                                                curveness: 0.3
                                            }
                                        }
                                        
                                    }
                                ]
                            });
                            
                        },
                        error : function() {
                            alert('fail-2');
                        }
                    }
                );
                // 矩形树图
                $.ajax(
                    {
                        type:'post',
                        url : '/moodlevideo/videojob/getvideoheatmapdata?type=2&userid=' + userId,
                        dataType : 'jsonp',
                        jsonp:"jsoncallback",
                        success  : function(data) {
                            //alert("success");
                            // 解析json串
                            data1 = data.data;
                            var hours = ['第1节', '第2节', '第3节', '第4节', '第5节', '第6节', '第7节', '第8节', '第9节'];
                            var days = ['第1章 软件工程学概述','第2章 可行性研究','第3章 需求分析','第4章 形式化说明技术','第5章 总体设计','第6章 详细设计','第7章 实现','第8章 维护','第9章 面向对象方法学引论',
                            '第10章 面向对象分析','第11章 面向对象设计','第12章 面向对象实现','第13章 软件项目管理'];
                            var data2=[];
                            var x,y;
                            for(var i=0;i<data1.length;i++){
                                x=data1[i][0]+1;
                                y=data1[i][1]+1;
                                var zhang = days[data1[i][0]]
                                data2.push({name:zhang+'-第'+y+'节',
                                            value:data1[i][2]});
                            }
                            
                            myChart2.setOption({
                                title : {
                                    text: '（个人）视频观看矩形树图'
                                },
                                tooltip : {
                                    trigger: 'item',
                                    formatter: "{b}: {c}"
                                },
                                series : [
                                    {
                                        name:'矩形树图',
                                        type:'treemap',
                                        roam:false,
                                        itemStyle: {
                                            normal: {
                                                label: {
                                                    show: true,
                                                    formatter: "{b}"
                                                },
                                                borderWidth: 1
                                            },
                                            emphasis: {
                                                label: {
                                                    show: true
                                                }
                                            }
                                        },
                                        data:data2
                                    }
                                ]
                            });
                        },
                        error : function() {
                            alert('fail-3');
                        }
                    }
                );
                
                
            }
        );


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
</body>