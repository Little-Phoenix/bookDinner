package chen.lang;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MyJob implements Job {
	private static final Logger logger = Logger.getLogger("myLogger");
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY年MM月dd日");
		logger.info("\n----------" + dateFormat.format(new Date()) + "----------");
		logger.info("----------orderInfo begin----------");
		Map<String, Set<String>> orderMap = BookDinner.getOrderMap();
		Map<String, String> dishMap = BookDinner.getDishMap();
		for (String key : orderMap.keySet()) {
			for (String dishId : orderMap.get(key)) {
				logger.info("{name:" + key + ",dish:" + dishMap.get(dishId) + "}");
			}
		}
		logger.info("----------orderInfo over----------");

		logger.info("----------Menu begin----------");
		for (String dishId : dishMap.keySet()) {
			logger.info("{dishId:" + dishId + ",dishName:" + dishMap.get(dishId) + "}");
		}
		logger.info("----------Menu end----------");
		BookDinner.setDishMap(new HashMap<String, String>());
		BookDinner.setOrderMap(new HashMap<String, Set<String>>());
	}
}