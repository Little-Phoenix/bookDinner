package chen.lang;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 订餐系统
 * 
 * @author Lang
 *
 */
@Controller
public class BookDinner {

	private static final Logger logger2 = Logger.getLogger(BookDinner.class);

	private static Map<String, Set<String>> orderMap = new HashMap<String, Set<String>>();
	private static Map<String, String> dishMap = new HashMap<String, String>();

	private static final String msg ="订餐停止，有需要请联系…………。";
	private static boolean theEnd = false;
	static{
	   new 	TimerOper().init();
	}
	/**
	 * 下单
	 * 
	 * @param name
	 * @param dishId
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "do", method = RequestMethod.POST)
	public synchronized void order(String name, String dishId, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger2.info("[order][name="+name+"][dishId="+dishId+"]");
		if (theEnd) {
			outJson(request, response, msg);
			return;
		}
		dishId = dishId.substring(1);

		Set<String> one = new HashSet<String>();
		for (String oneDish : dishId.split(",")) {
			if(!dishMap.keySet().contains(oneDish)){
				outJson(request, response, "有不存在的菜，请重试");
				return;
			}
			one.add(oneDish);
		}
		orderMap.put(name, one);

		outJson(request, response, "succ");
	}

	/**
	 * 查订单
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "listOrders", method = RequestMethod.GET)
	public void orders(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Order> list = new ArrayList<Order>();
		for (String s : orderMap.keySet()) {
			Set<String> foodIds = orderMap.get(s);
			Set<String> foodNames = new HashSet<String>();
			for (String id : foodIds) {
				foodNames.add(dishMap.get(id));
			}
			list.add(new Order().setPeopleName(s).setFood(foodNames.toString()));
		}
		outJson(request, response, list);
	}

	/**
	 * 查菜单
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "listDishes", method = RequestMethod.GET)
	public void dishes(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Dish> list = new ArrayList<Dish>();
		for (String key : dishMap.keySet()) {
			list.add(new Dish().setFoodName(dishMap.get(key)).setId(key));
		}
		outJson(request, response, list);
	}

	/**
	 * 删订单
	 * 
	 * @param name
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public void delete(String name, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (theEnd) {
			outJson(request, response,msg);
			return;
		}
		orderMap.remove(name);
		outJson(request, response, "succ");
	}

	/**
	 * 删菜单
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "deleteOneDish", method = RequestMethod.POST)
	public void deleteOneDish(String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		for (String key : orderMap.keySet()) {
			if (orderMap.get(key).contains(id)) {
				outJson(request, response, "有人定了这个餐，不能删除");
				return;
			}
		}
		dishMap.remove(id);
		outJson(request, response, "succ");
	}

	/**
	 * 列出订单数量
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "listOrderCount", method = RequestMethod.GET)
	public synchronized void  orderCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<OrderCount> l = new ArrayList<OrderCount>();
		Map<String,Integer> orderCountMap = new HashMap<String,Integer>();
		for (String s : orderMap.keySet()) {
			Set<String> ss = orderMap.get(s);
			for(String dishId : ss){
				if(orderCountMap.keySet().contains(dishId)){
					Integer integer = orderCountMap.get(dishId);
					integer++;
					orderCountMap.put(dishId, integer);
				}else{
					orderCountMap.put(dishId, 1);
				}
			}
		}
		
		for(String key :orderCountMap.keySet() ){
			OrderCount oc = new OrderCount();
			oc.setFoodCount(orderCountMap.get(key));
			oc.setFoodName(dishMap.get(key));
			l.add(oc);
		}
		outJson(request, response, l);
	}

	/**
	 * 加一个菜
	 * 
	 * @param name
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "addOnedish", method = RequestMethod.POST)
	public void addOnedish(String name, HttpServletRequest request, HttpServletResponse response) throws Exception {
		for (String s : dishMap.keySet()) {
			if (dishMap.get(s).equals(name)) {
				outJson(request, response, "存在同名餐名");
				return;
			}
		}
		dishMap.put(new Date().getTime()+"", name);
		outJson(request, response, "succ");
	}
	
	/**
	 * 设置截止状态
	 * 
	 * @param name
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "setEndLine", method = RequestMethod.POST)
	public synchronized void setEndLine(Integer end, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(end==null||(!end.equals(1)&&!end.equals(0))){
			outJson(request, response, "参数错误");
			return;
		}
		if(end.equals(1)){
			theEnd = true;
		}else{
			theEnd = false;
		}
		outJson(request, response, "succ");
	}
	
	/**
	 * 设置截止状态
	 * 
	 * @param name
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "getEndSts", method = RequestMethod.GET)
	public synchronized void getEndSts( HttpServletRequest request, HttpServletResponse response) throws Exception {
		outJson(request, response, theEnd?"1":"0");
	}

	/**
	 * 输出JSON
	 * 
	 * @param response
	 * @param result
	 */
	protected void outJson(HttpServletRequest request, HttpServletResponse response, Object result) {
		Map<String, Object> returnResult = new HashMap<String, Object>();
		returnResult.put("msg", result);
		out(request, response, JSONObject.toJSONString(returnResult), 200);
	}

	protected void out(HttpServletRequest request, HttpServletResponse response, String result, int code) {
		try {
			// response.setContentType("text/html;charset=utf-8");
			response.setContentType("application/json;charset=utf-8");
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Headers", "X-Requested-With");
			response.setHeader("Access-Control-Allow-Methods", "*");

			response.setStatus(code);

			PrintWriter out = response.getWriter();
			out.print(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class Order {
		private String peopleName;
		private String food;

		public String getPeopleName() {
			return peopleName;
		}

		public Order setPeopleName(String peopleName) {
			this.peopleName = peopleName;
			return this;
		}

		public String getFood() {
			return food;
		}

		public Order setFood(String food) {
			this.food = food;
			return this;
		}
	}

	private static class Dish {
		private String id;
		private String foodName;

		public String getId() {
			return id;
		}

		public Dish setId(String id) {
			this.id = id;
			return this;
		}

		public String getFoodName() {
			return foodName;
		}

		public Dish setFoodName(String foodName) {
			this.foodName = foodName;
			return this;
		}

	}

	private static class OrderCount {
		private String foodName;
		private int foodCount;

		public String getFoodName() {
			return foodName;
		}

		public void setFoodName(String foodName) {
			this.foodName = foodName;
		}

		public int getFoodCount() {
			return foodCount;
		}

		public void setFoodCount(int foodCount) {
			this.foodCount = foodCount;
		}
	}

	public static Map<String, Set<String>> getOrderMap() {
		return orderMap;
	}

	public static void setOrderMap(Map<String, Set<String>> orderMap) {
		BookDinner.orderMap = orderMap;
	}

	public static Map<String, String> getDishMap() {
		return dishMap;
	}

	public static void setDishMap(Map<String, String> dishMap) {
		BookDinner.dishMap = dishMap;
	}
	
	
	static class Test{
		int code;
		String userId;
		String token;
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		
	}
	
}
