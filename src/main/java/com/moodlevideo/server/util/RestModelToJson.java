package com.moodlevideo.server.util;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rest model util
 * 
 * @author wxb
 * 
 */
public class RestModelToJson {

	static Logger logger = LoggerFactory.getLogger(RestModelToJson.class);

	/**
	 * REST MODEL TO JSON STR
	 * 
	 * @param model
	 * @return
	 */
	public static String getRestJosn(RestModel model) {
		String json = "";
		int code = BllConstantEnum.RESCODE_1.getCode();
		String desc = BllConstantEnum.RESCODE_1.getDesc();
		try {
			if (model == null) {
				json = RestModel.getRestModel(code, desc).toString();
			} else {
				json = JsonUtilCallMe.objectToJackson(model, RestModel.class);
			}
		} catch (JsonGenerationException e) {
			logger.error(e.getMessage(), e);
			code = BllConstantEnum.RESCODE_2.getCode();
			desc = e.getMessage();
		} catch (JsonMappingException e) {
			logger.error(e.getMessage());
			code = BllConstantEnum.RESCODE_2.getCode();
			desc = e.getMessage();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			code = BllConstantEnum.RESCODE_2.getCode();
			desc = e.getMessage();
		} finally {
			if (json == null || "".equals(json)) {
				json = RestModel.getRestModel(code, desc).toString();
			}
			logger.debug("RET JSON={}", json);
		}
		return json;
	}
	
	
}
