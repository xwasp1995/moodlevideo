package com.moodlevideo.server.util;

/**
 * REST 返回JSON 标准对象
 * 
 * @author wxb
 * 
 */
public class RestModel  {
	
	
	public RestModel(){
		this.server_status = 0;
		this.server_error = null;
		this.data= null;
	}

	/**
	 * 响应码
	 */
	private int server_status;
	/**
	 * 响应描述
	 */
	private String server_error;
	/**
	 * 响应数据
	 */
	private Object data;
	
	
	
	public int getServer_status() {
		return server_status;
	}
	public void setServer_status(int server_status) {
		this.server_status = server_status;
	}
	public String getServer_error() {
		return server_error;
	}
	public void setServer_error(String server_error) {
		this.server_error = server_error;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	public static RestModel getRestModel(int code,String desc){
		RestModel m=new RestModel();
		m.setServer_status(code);
		m.setServer_error(desc);
		return m;
	}
	public static RestModel getRestModel(BllConstantEnum be){
		RestModel m=new RestModel();
		m.setServer_status(be.getCode());
		m.setServer_error(be.getDesc());
		return m;
	}
	
	public static RestModel getRestModel(int code,String desc,Object data){
		RestModel rm=new RestModel();
		rm.setServer_status(code);
		rm.setServer_error(desc);
		rm.setData(data);
		return rm;
	}
	public static RestModel getRestModel(BllConstantEnum e,Object data){
		RestModel rm=new RestModel();
		rm.setServer_status(e.getCode());
		rm.setServer_error(e.getDesc());
		rm.setData(data);
		return rm;
	}
	
	public static String strToJson(String key,String val){
		
		return "{\""+key+"\":\""+val+"\"}";
	}
	@Override
	public String toString(){
		return "{\"server_status\":"+this.server_status+",\"server_error\":\""+this.server_error+"\",\"data\":\""+this.data+"\"}";
	}
	
	public String toSingleValue(){
		return "{\"server_status\":"+this.server_status+",\"server_error\":\""+this.server_error+"\",\"data\":"+this.data+"}";
	}
	
}
