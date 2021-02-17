package kjm.test.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import kjm.test.service.TestService;
import kjm.test.vo.AddressVo;
import kjm.test.vo.WeatherVo;

@Controller
// @RequestMapping("/test")
public class TestController {
	
	@Resource(name = "TestService")
	private TestService testService;
	
	protected final static String authKey = "rlfDiciScap8pTdtbIlkN9%2FX%2BTe0pLftDTjzoneo4rhGN6DwrNHLQ%2Bq9iXWxO0PwR9NZi0Zj%2BbREw74M%2BEuEvA%3D%3D";
	
	private Element send(String url) throws JDOMException, IOException {
		return new SAXBuilder().build(url).getRootElement();
	}
	
	/*기상청 OPEN API JSON 파싱하는 함수 */
	@RequestMapping(value="/testapijson.do", method=RequestMethod.GET)
	public void callapijson(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception{
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
//        System.out.println("response : " + j_response);
        JSONObject j_body = (JSONObject)j_response.get("body");
//        System.out.println("body : " + j_body);
        JSONObject j_items = (JSONObject)j_body.get("items");
//        System.out.println("items : " + j_items);
        JSONArray item = (JSONArray)j_items.get("item");
//        System.out.println("item : "+ item);
        
        for(int i=0; i<item.size(); i++){
        	JSONObject row = (JSONObject)item.get(i);
//        	String category = (String)row.get("category");
        	wv.category = row.get("category").toString();
        	wv.baseDate = row.get("baseDate").toString();
        	wv.baseTime = row.get("baseTime").toString();
        	wv.fcstDate = row.get("fcstDate").toString();
        	wv.fcstTime = row.get("fcstTime").toString();
        	wv.fcstValue = row.get("fcstValue").toString();
        	wv.nx = row.get("nx").toString();
        	wv.ny = row.get("ny").toString();
        	
        	/*test 콘솔 출력용*/
        	System.out.println("************** wv.basedate "+ i +" : "+ wv.getBaseDate());
        	System.out.println("************** wv.baseTime "+ i +" : "+wv.getBaseTime());
        	System.out.println("************** wv.category "+ i +" : "+ wv.getCategory());
        	System.out.println("************** wv.fcstDate "+ i +" : "+wv.getFcstDate());
        	System.out.println("************** wv.fcstValue "+ i +" : "+wv.getFcstTime());
        	System.out.println("************** wv.fcstTime "+ i +" : "+wv.getFcstValue());
        	System.out.println("************** wv.nx "+ i +" : "+wv.getNx());
        	System.out.println("************** wv.ny "+ i +" : "+wv.getNy());
        	System.out.println("----------------------------------------------------------");
        	
        	/*testService.insertWeather(wv);*/
        }

	}
	
	/*기상청 OPEN API XML 파싱하는 함수 */
	@RequestMapping(value="/testapixml.do", method=RequestMethod.GET)
	public void callapixml(String baseDate, String baseTime, String nx, String ny) throws JDOMException, IOException,Exception{
		baseDate = "20210215";
		baseTime = "1400";
		nx = "61"; 
		ny= "126"; 
		
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey="
		+ authKey
		+ "&numOfRows=11"
		+ "&pageNo=1"
		+ "&base_date=" + baseDate //발표일자
		+ "&base_time=" + baseTime //발표시각
		+ "&nx=" + nx 
		+ "&ny=" + ny
		+ "&dataType=XML";
		//System.out.println("########### resultMsg : " + send(url).getChild("header").getChildren("resultMsg").get(0).getText());
		WeatherVo wv = new WeatherVo();
		
		for (Element data : send(url).getChild("body").getChild("items").getChildren("item")) {
			HashMap<String, Object> vo = new HashMap<String, Object>();
			
			vo.put("baseDate", data.getChildren("baseDate").get(0).getText()); //발표일자
			vo.put("baseTime", data.getChildren("baseTime").get(0).getText()); //발표시각
			vo.put("category", data.getChildren("category").get(0).getText()); //자료구분코드
			vo.put("fcstDate", data.getChildren("fcstDate").get(0).getText()); //예보일자
			vo.put("fcstTime", data.getChildren("fcstTime").get(0).getText()); //예보시각
			vo.put("fcstValue", data.getChildren("fcstValue").get(0).getText()); //예보 값
			vo.put("nx", data.getChildren("nx").get(0).getText()); //예보지점 x좌표
			vo.put("ny", data.getChildren("ny").get(0).getText()); //예보지점 y좌표

			
			result.add(vo);
			System.out.println("###########vo " + vo);
			//System.out.println("########## result "+result.size()+" : "+ result);
		}
		
		for(HashMap<String,Object> vo : result){
			wv.baseDate = vo.get("baseDate").toString();
			wv.baseTime = vo.get("baseTime").toString();
			wv.category = vo.get("category").toString();
			wv.fcstDate = vo.get("fcstDate").toString();
			wv.fcstTime = vo.get("fcstTime").toString();
			wv.fcstValue = vo.get("fcstValue").toString();
			wv.nx = vo.get("nx").toString();
			wv.ny = vo.get("ny").toString();
			
			System.out.println("################# vo " + result.size() + ":" + vo);
//			testService.insertWeather(wv);
		}
		//testService.insertWeather(wv);
//		return wv;
		
		
	}
	
	/*이 아래는 egovframework가 잘 작동되는지 확인하기 위한 개인 test용입니다.*/

	@RequestMapping("/hello.do")
	public @ResponseBody String HelloWorldTest() {

		return "Hello World!!";
	}

	@RequestMapping("/testDB.do")
	public String testDB(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
		/*List<TestVo> list = testService.testDB();
		System.out.println("##################### list : " + list);
		for (int i = 0; i < 2; i++) {
			System.out.println("##################### list.get(" + i + ") :: " + list.get(i).getName());
		}

		model.addAttribute("list", list);*/
		return "egovframework/example/sample/indexTest";
	}
	
	@RequestMapping(value="/usersignup.do", method=RequestMethod.POST)
	public void SubmitDB(AddressVo addrvo) throws Exception {
		System.out.println("########## 주소 : "+addrvo.getSample6_address());
		System.out.println("########## 상세주소 : "+addrvo.getSample6_detailAddress());
		System.out.println("########## 우편번호 : "+addrvo.getSample6_postcode());
		testService.SubmitAddr(addrvo);
	}
}
