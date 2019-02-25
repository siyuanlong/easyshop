package com;

import com.alibaba.fastjson.JSON;
import com.shop.pojo.Json2Object;

import java.util.List;

public class Json2ObjectUtils {

	public static String json2Object(String json){
		//JSON工具类型:
		List<Json2Object> list = JSON.parseArray(json, Json2Object.class);
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			Json2Object o = list.get(i);
			if(i!=list.size()-1){
				sb.append(o.getText()).append(","); //如果 㐊最后欧一个就不要拼逗号
			}else{
				sb.append(o.getText());
			}
		}
		return sb.toString();
	}
	
	public static String object2Json(List<Json2Object> list){
		return JSON.toJSONString(list);
	}
	
}
