package kjm.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kjm.scheduler.dao.SchedulerDao;

@Component
public class Scheduler {
	
	@Autowired
	private SchedulerDao dao;
	
	//					초  분  시  일 월 요일 
	@Scheduled(cron = " 10 * * * * *")
	public void autoUpdate() {
		try {
			String value= dao.test();
			System.out.println("value : " + value);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
