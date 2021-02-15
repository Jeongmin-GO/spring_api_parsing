package kjm.test.mapper;

import java.util.List;

import egovframework.rte.psl.dataaccess.mapper.Mapper;
import kjm.test.vo.AddressVo;
import kjm.test.vo.TestVo;
import kjm.test.vo.WeatherVo;

@Mapper("TestMapper")
public interface TestMapper {

//	public List<TestVo> testDB() throws Exception;

	public void SubmitAddr(AddressVo addrvo) throws Exception;

	public void insertWeather(WeatherVo wv) throws Exception;

	public List<WeatherVo> selectOutput() throws Exception;
}
