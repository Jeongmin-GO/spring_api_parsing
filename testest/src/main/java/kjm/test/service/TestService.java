package kjm.test.service;

import java.util.List;

import kjm.test.vo.AddressVo;
import kjm.test.vo.TestVo;
import kjm.test.vo.WeatherVo;

public interface TestService {

	//List<TestVo> testDB() throws Exception;

	public void SubmitAddr(AddressVo addrvo) throws Exception;

	public void insertWeather(WeatherVo wv) throws Exception;
	
}
