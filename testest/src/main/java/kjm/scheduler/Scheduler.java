package kjm.scheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kjm.scheduler.dao.SchedulerDao;
import kjm.test.service.TestService;
import kjm.test.vo.WeatherVo;

@Component
public class Scheduler {
	@Autowired
	private SchedulerDao dao;
	
	@Resource(name = "TestService")
	private TestService testService;
	
	protected final static String authKey = "rlfDiciScap8pTdtbIlkN9%2FX%2BTe0pLftDTjzoneo4rhGN6DwrNHLQ%2Bq9iXWxO0PwR9NZi0Zj%2BbREw74M%2BEuEvA%3D%3D";
	
	//				       초 분 시 일 월 요일 
	@Scheduled(cron = "0/30 * * * * *")
	public void autoUpdate() {
		try {
			callapijson();
			List<WeatherVo> value= dao.test();
			for(int i=0;i<value.size(); i++) {
				System.out.println("#############fcstValue "+i+": " + value.get(i).fcstValue);
				System.out.println("#############fcstTime "+i+": " + value.get(i).fcstTime);
				System.out.println("-------------------------------------------------------");
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/*기상청 OPEN API JSON 파싱하는 메서드 */
	@RequestMapping(value="/testapijson.do", method=RequestMethod.GET)
	public void callapijson() throws Exception{
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeformat = new SimpleDateFormat("HHmm");

		Date datetime = new Date();
		
		String date = dateformat.format(datetime);
		String time = timeformat.format(datetime);
		int i_time = Integer.parseInt(time);
		
		String baseDate = date;
		String baseTime = null; /*0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)*/
		String nx = "62"; 
		String ny= "125"; 
		
		if(i_time>=210 && i_time<510) {
			baseTime = "0200";
		}else if(i_time>=510 && i_time<810) {
			baseTime="0500";
		}else if(i_time>=810 && i_time<1100) {
			baseTime="0800";
		}else if(i_time>=1110 && i_time<1410) {
			baseTime="1100";
		}else if(i_time>=1410 && i_time<1710) {
			baseTime="1400";
		}else if(i_time>=1710 && i_time<2010) {
			baseTime="1700";
		}else if(i_time>=2010 && i_time<2310) {
			baseTime="2000";
		}else if(i_time>=2310 && i_time<0210) {
			baseTime="2300";
		}
		
		StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + authKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + URLEncoder.encode("-", "UTF-8")); /*공공데이터포털에서 받은 인증키*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON)Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(baseDate , "UTF-8")); /*15년 12월 1일발표*/
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(baseTime , "UTF-8")); /*05시 발표*/
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); /*예보지점 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); /*예보지점의 Y 좌표값*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        conn.disconnect();
        
        String result = sb.toString();
        System.out.println(result); //json 결과값 확인
        
        //Json처럼 생긴 String을 Json으로 만들기
        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject)parser.parse(result); //리턴값이 오브젝트이기 때문에 json오브젝트로 다운캐스팅
        
        WeatherVo wv = new WeatherVo();
        
        //response
        JSONObject j_response = (JSONObject)jsonObj.get("response");
        JSONObject j_body = (JSONObject)j_response.get("body");
        JSONObject j_items = (JSONObject)j_body.get("items");
        JSONArray item = (JSONArray)j_items.get("item");
        
        for(int i=0; i<item.size(); i++){
        	JSONObject row = (JSONObject)item.get(i);
        	wv.category = row.get("category").toString();
        	wv.baseDate = row.get("baseDate").toString();
        	wv.baseTime = row.get("baseTime").toString();
        	wv.fcstDate = row.get("fcstDate").toString();
        	wv.fcstTime = row.get("fcstTime").toString();
        	wv.fcstValue = row.get("fcstValue").toString();
        	wv.nx = row.get("nx").toString();
        	wv.ny = row.get("ny").toString();    	
        	
        	testService.insertWeather(wv);
        }
	}	
}
