package kjm.test.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import kjm.test.service.TestService;
import kjm.test.vo.WeatherVo;

@Controller
public class ChartController {
	
	@Autowired
	private TestService service;
	
	@RequestMapping("/chart.do")
	public String charTest() throws Exception{
		return "amchart/home";
	}
	
	@RequestMapping(value="/chartOutput.do", method=RequestMethod.GET)
	public @ResponseBody ModelAndView chartOutput(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception{
		System.out.println("######### 연결 성공 ###########");
		ModelAndView mav = new ModelAndView("jsonView");
		
		List<WeatherVo> output =service.selectOutput();
		System.out.println("############# outPut : " + output);
		for(int i=0; i<output.size(); i++) {
			System.out.println("############# FcstTime : " + output.get(i).getFcstTime());
			System.out.println("############# FcstValue : " + output.get(i).getFcstValue());
			System.out.println("############# FcstDate : " + output.get(i).getFcstDate());
			System.out.println("-----------------------------------------");
		}
		mav.addObject("result", output);
		mav.setViewName("jsonView");
		return mav;
	}
	
}
