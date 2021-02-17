package kjm.scheduler.dao;

import java.sql.SQLException;
import java.util.List;

import kjm.test.vo.WeatherVo;

public interface SchedulerDao {

	public List<WeatherVo> test() throws SQLException, Exception;

}
