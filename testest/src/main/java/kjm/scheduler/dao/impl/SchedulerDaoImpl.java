package kjm.scheduler.dao.impl;

import java.sql.SQLException;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kjm.scheduler.dao.SchedulerDao;
import kjm.test.mapper.TestMapper;

@Repository
public class SchedulerDaoImpl implements SchedulerDao {
	
	@Resource(name="TestMapper")
	private TestMapper mapper;
	
	public String test(){
		return mapper.test();
	}
}
