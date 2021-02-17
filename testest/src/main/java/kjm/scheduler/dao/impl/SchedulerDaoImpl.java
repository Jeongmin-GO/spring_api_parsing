package kjm.scheduler.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kjm.scheduler.dao.SchedulerDao;
import kjm.test.mapper.TestMapper;
import kjm.test.vo.WeatherVo;

@Repository
public class SchedulerDaoImpl implements SchedulerDao {
	
	@Resource(name="TestMapper")
	private TestMapper mapper;
	
	@Override
	public List<WeatherVo> test() throws Exception{
		return mapper.test();
	}
}
