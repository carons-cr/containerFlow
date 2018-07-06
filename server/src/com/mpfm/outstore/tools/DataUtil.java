package com.mpfm.outstore.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataUtil {
	// 接收数据处理
	public String getBodyData(HttpServletRequest request) throws IOException {
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "utf-8"));
		StringBuffer sb = new StringBuffer("");
		String temp;
		while ((temp = br.readLine()) != null) {
			sb.append(temp);
		}
		br.close();
		return sb.toString();
	}

	// 将数据封装，准备发送出去
	public String encapsulation(String data) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("state", 1);
		map2.put("users", data);
		String jsonStr = mapper.writeValueAsString(map2);
		return jsonStr;
	}
}
