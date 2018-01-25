package com.moodlevideo.server.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * @author wxb
 * json工具
 */
public class JsonUtilCallMe {
	private static Logger logger = LoggerFactory.getLogger(JsonUtilCallMe.class);
	private static ObjectMapper mapperByToJackson = null;
	private static ObjectMapper mapperByToObj = null;
	private static ObjectMapper mapperByToMap = null;
	static {
		mapperByToJackson = new ObjectMapper();
		mapperByToJackson.setDateFormat(new SimpleDateFormat("yyyyMMddHHmmss"));
		mapperByToObj = new ObjectMapper();
		mapperByToObj.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapperByToObj.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapperByToMap = new ObjectMapper();
		mapperByToMap.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	/**
	 * 
	 * @param json
	 * @param cls
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static String objectToJackson(Object json, Class cls)
			throws JsonGenerationException, JsonMappingException, IOException {
		mapperByToJackson.registerSubtypes(cls);
		String reqJson = mapperByToJackson.writeValueAsString(json);
		return reqJson;
	}

	/**
	 * 
	 * @param json
	 * @param cls
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static Object jsongToObject(String json, Class cls)
			throws JsonGenerationException, JsonMappingException, IOException {
		Object obj = null;
		//ObjectMapper mapper = new ObjectMapper();
		//mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		obj = mapperByToObj.readValue(json, cls);
		return obj;
	}

	public static Map<String, Object> toMap(String json) throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> map = null;
		//ObjectMapper mapper = new ObjectMapper();
		//mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		map = mapperByToMap.readValue(json, Map.class);
		return map;
	}

	public static String toJson(Map<String, String> map) {
		Set<String> keys = map.keySet();
		String key = "";
		String value = "";
//		StringBuffer jsonBuffer = new StringBuffer();
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{");
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			key = it.next();
			value = map.get(key);
			//jsonBuilder.append(key + ":" + value);
			jsonBuilder.append(key);
			jsonBuilder.append(":");
			jsonBuilder.append(value);
			if (it.hasNext()) {
				jsonBuilder.append(",");
			}
		}
		jsonBuilder.append("}");
		return jsonBuilder.toString();
	}

	/**
	 * 
	 * @param json
	 * @param c
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	public static Object jsonToObject(String json) throws JsonGenerationException, JsonMappingException, IOException {
		//ObjectMapper mapper = new ObjectMapper();
		Object object = null;
		//mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		//mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		object = JsonUtilCallMe.jsongToObject(json, Object.class);
		logger.debug("object{}", object);

		return object;
	}

	/**
	 * 将json字符串转换为map对象
	 * 
	 * @param url
	 * @param object
	 * @return
	 */
	public static HashMap<String, String> jsonToMap(String jsonString) {
		HashMap<String, String> retMap = null;
		if (jsonString == null) {
			return null;
		} else {
			retMap = JSON.parseObject(jsonString, HashMap.class);
		}
		return retMap;
	}

	public static void main(String[] s) throws JsonGenerationException,
			JsonMappingException, IOException {
		// String json="[{\"userId\":'1'},{'userId':'2'}]";
		// List t5=(List)JascksonUtil.jsonToObject(json);
		// Map user1=(Map)t5.get(0);
		// logger.info("user1:{}",user1.get("userId"));
		String jsonStr = "{'ATTACHAMOUNT':'0','PRODUCTAMOUNT':'5000','UPTRANSEQ':'20120731661159','RETNCODE':'0000','ORDERAMOUNT':'5000','ORDERSEQ':'600102310100003720120924003383','TRANDATE':'20120731','CURTYPE':'RMB','RETNINFO':'0000','ORDERREQTRANSEQ':'201207311022090000000000888942'}";
		System.out.println(JsonUtilCallMe.jsonToMap(jsonStr));
		String json = "{\"age\":0,\"name\":\"测试\",\"childTests\":[{\"age\":1,\"name\":\"测试姓名1\"},{\"age\":2,\"name\":\"测试姓名2\"}]}";
//		com.iwhere.health.bo.buistest.Test t = (com.iwhere.health.bo.buistest.Test) jsongToObject(json, com.iwhere.health.bo.buistest.Test.class);
		System.out.println();
	}
}

class Test {
	String t1 = "t1";
	List<Test2> t2List = new ArrayList<Test2>();

	public List<Test2> getT2List() {
		return t2List;
	}

	public void setT2List(List<Test2> list) {
		t2List = list;
	}

	public String getT1() {
		return t1;
	}

	public void setT1(String t1) {
		this.t1 = t1;
	}
}

class Test2 {
	String t2 = "t2";

	public String getT2() {
		return t2;
	}

	public void setT2(String t2) {
		this.t2 = t2;
	}

}