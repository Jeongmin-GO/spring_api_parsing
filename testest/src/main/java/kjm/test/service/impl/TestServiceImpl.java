package kjm.test.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kjm.test.mapper.TestMapper;
import kjm.test.service.TestService;
import kjm.test.vo.AddressVo;
import kjm.test.vo.TestVo;
import kjm.test.vo.WeatherVo;

@Service("TestService")
public class TestServiceImpl extends EgovAbstractServiceImpl implements TestService{
	
	@Resource(name="TestMapper")
	private TestMapper mapper;
	
	/*@Override
	public List<TestVo> testDB() throws Exception{
		return mapper.testDB();
	}
	*/
	@Override
	public void SubmitAddr(AddressVo addrvo) throws Exception {
		mapper.SubmitAddr(addrvo);
	}

	@Override
	public void insertWeather(WeatherVo wv) throws Exception {
		mapper.insertWeather(wv);
		
	}

	@Override
	public List<WeatherVo> selectOutput() throws Exception {
		return mapper.selectOutput();
	}
	
	
}
