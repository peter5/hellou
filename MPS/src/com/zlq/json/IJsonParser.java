package com.zlq.json;

import java.util.ArrayList;
import java.util.HashMap;

public interface IJsonParser {

	/*
	 * X module 数据解析
	 */
	public HashMap<Object, Object> xParser(String url);
	
	
	/*
	 * Y module 数据解析
	 */
	public ArrayList<HashMap<Object, Object>> yParser(String url);
	
	/*
	 * Z module 数据解析
	 */
	public HashMap<Object, Object> zParser(String url);
	
	/*
	 * Y_X module 
	 */
	public HashMap<Object, Object> yParserX(String url);
	
	/*
	 * Y_Z module
	 */
	public HashMap<Object, Object> yParserZ(String url);
	
	/*
	 * 联系我们
	 */
	public HashMap<String, String> ContactUs(String url);
	
	/*
	 * 地图信息
	 */
	public ArrayList<String> baiduMap(String url);
	
	/*
	 * 
	 */
	public ArrayList<HashMap<String, String>> Recruitment(String url);
	
	/*
	 * 
	 */
	public HashMap<String, String> Partners(String url);
}
