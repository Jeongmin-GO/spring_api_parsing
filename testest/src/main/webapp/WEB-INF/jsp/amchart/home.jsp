<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>amchart home</title>
<!-- Styles -->
<style>
#chartdiv {
	width: 100%;
	height: 400px;
	background-color: #FFFFFF;
}
</style>

<!-- Resources -->
<script type="text/javascript" src="https://www.amcharts.com/lib/3/amcharts.js"></script>
<script type="text/javascript" src="https://www.amcharts.com/lib/3/serial.js"></script>
<script type="text/javascript" src="https://www.amcharts.com/lib/3/themes/light.js"></script>
<script src="https://cdn.amcharts.com/lib/4/core.js"></script>
<script src="https://cdn.amcharts.com/lib/4/charts.js"></script>
<script src="https://cdn.amcharts.com/lib/4/themes/animated.js"></script>
<!-- <script src="https://code.jquery.com/jquery-3.4.1.min.js"></script> -->
<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script> -->
<script src="/js/jquery.min_1.4.2.js"></script>

<!-- Chart code -->
<%-- <script type="text/javascript"><%@ include file="./chart.js"%></script>--%>
<script type="text/javascript">
	$(function(){
		$.ajax({
			type: "GET",
			url : "/chartOutput.do",
			success:function(responseData){
				//console.log(responseData);
				var data = responseData.result;
				//console.log(data);
				
				var chartData = [];
				for(var i in data){
					//console.log(data[i].fcstValue);
					//console.log(data[i].fcstTime);
	
					chartData.push({
						"column" : data[i].fcstValue,
						"date" : data[i].fcstTime
					})
				}
				//콘솔창에 잘 들어갔는지 test
				console.log(chartData);
				
				AmCharts.makeChart("chartdiv",
						{
							"type": "serial",
							"categoryField": "date",
							//"dataDateFormat": "YYYY-MM-DD HH",
							"dataDateFormat" : "HHMM",
							"theme": "light",
							"categoryAxis": {
								"minPeriod": "hh",
								"parseDates": true
							},
							"chartCursor": {
								"enabled": true,
								"categoryBalloonDateFormat": "JJ:NN"
							},
							"chartScrollbar": {
								"enabled": true
							},
							"trendLines": [],
							"graphs": [
								{
									"bullet": "round",
									"id": "AmGraph-1",
									"title": "graph 1",
									"valueField": "column"
								}
							],
							"guides": [],
							"valueAxes": [
								{
									"id": "ValueAxis-1",
									"title": "Temperatures"
								}
							],
							"allLabels": [],
							"balloon": {},
							"legend": {
								"enabled": true,
								"useGraphSettings": true
							},
							"titles": [
								{
									"id": "Title-1",
									"size": 15,
									"text": "2021-02-09 날씨"
								}
							],
							/* "dataProvider": [
								{
									"column": 8,
									"date": "2021-02-09 06"
								},
								{
									"column": 6,
									"date": "2021-02-09 09"
								},
								{
									"column": 2,
									"date": "2021-02-09 12"
								},
								{
									"column": 1,
									"date": "2021-02-09 15"
								},
								{
									"column": 2,
									"date": "2021-02-09 18"
								}
							]  */
							"dataProvider" : chartData
						});
			}/*,error:function(error){
				alert('연결실패')
			}*/
		})
	})
</script>
</head>
<body>
	<h1>Ajax 호출 TEST</h1>
	<div id="chartdiv"></div>
</body>
</html>