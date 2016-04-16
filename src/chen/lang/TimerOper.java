package chen.lang;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class TimerOper {


	public void init() {
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler sched = sf.getScheduler();
			JobDetail job = newJob(MyJob.class).withIdentity("job1", "group1").build();
	        
	        CronTrigger trigger = newTrigger()
	            .withIdentity("trigger1", "group1")
	            .withSchedule(cronSchedule("0 0 19 * * ?"))
	            .build();
			
			sched.scheduleJob(job, trigger);
			sched.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}

