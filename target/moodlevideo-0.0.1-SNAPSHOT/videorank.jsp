<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <title>视频观看时间排行榜</title>
    <script src="/moodlevideo/js/echarts.min.js"></script>
    <script src="/moodlevideo/js/jquery-1.9.0.min.js"></script>

    <style type="text/css">
        body{ background-color: #D8EFFD;} 
    </style>

</head>
<body>
    <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
    <div id="main" style="width: 1500px;height:2200px;"></div>
    <!-- ECharts单文件引入 -->
    <script src="http://echarts.baidu.com/build/dist/echarts.js"></script>

    <script type="text/javascript">
        // 路径配置
        require.config({
            paths: {
                echarts: 'http://echarts.baidu.com/build/dist'
            }
        });

        var myChart = echarts.init(document.getElementById('main'));
        // 显示标题，图例和空的坐标轴
        var yData=[];
        var xData=[];
        var tData=[];
        var time;
        var t;
        
        // 使用
        require(
            [
                'echarts',
                'echarts/chart/bar' // 使用柱状图就加载bar模块，按需加载
            ],

            function (ec) {
                // 基于准备好的dom，初始化echarts图表
                $.ajax(
                    {
                        type:'post',
                        url : '/moodlevideo/videojob/getvideorankdata',
                        dataType : 'jsonp',
                        jsonp:"jsoncallback",
                        success  : function(data) {

                            userRank = data.data.userRank;
                            for (var i = 70; i >= 0; i--) {
                                if(userRank[i] != null){
                                    yData.push(userRank[i].name);
                                    tData.push(userRank[i].totaltime);
                                    time=userRank[i].totaltime.split(":");
                                    t=parseInt(time[0])*3600+parseInt(time[1])*60+parseInt(time[2]);
                                    xData.push(t);
                                }
                            }
                            myChart.setOption({
                                title: {
                                    text: '观看视频时间排名',
                                    subtext:'更新时间：' +data.data.rankUpdateTime,
                                    textStyle: {
                                        fontSize: 18,
                                        fontWeight: 'bolder',
                                        color: '#00'          // 主标题文字颜色
                                    },
                                     subtextStyle: {
                                        color: '#000'          // 副标题文字颜色
                                    }
                                },
                                tooltip: {
                                    trigger: 'item',
                                    show: true,
                                    //formatter: '{b0}: {c0}'+'秒'
                                    formatter: function(params) {  
                                          var res = params.value;  
                                          var h=Math.floor(res/3600);
                                          var m=Math.floor((res-h*3600)/60);
                                          var s=res-h*3600-m*60;
                                          res=h + "小时" + m + "分" + s +'秒';
                                          return res;  
                                    }  
                                },
                                
                                xAxis: {
                                    axisLine:{  
                                        lineStyle:{  
                                            color:'#000',  
                                            width:1,//这里是为了突出显示加上的  
                                        }  
                                    } ,
                                    axisLabel : {
                                        formatter: '{value} 秒'
                                    } ,
                                },
                                yAxis: {
                                       
                                   axisLine:{  
                                        lineStyle:{  
                                            color:'#000',  
                                            width:1,//这里是为了突出显示加上的  
                                        }  
                                    }  ,
                                    data:yData     
                                },
                                series: [
                                    {
                                        name: '时间',
                                        type: 'bar',
                                        data:xData,
                                        itemStyle: {normal: {
                                            label : {show: true}
                                        }}
                                    }
                                ]
                            });
                            
                        },
                        error : function() {
                            alert('数据加载出错，请联系助教，稍后再试');
                        }
                    }
                );
                
            }
        );



    </script>
</body>