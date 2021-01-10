package com.newgen.am.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.newgen.am.common.LocalServiceConnection;

@Service
public class RedisTestService {
	@Autowired
	private RedisTemplate template;
	
	public void testSetIfAbsent(String key, String value) {
		long beginDate = System.currentTimeMillis();
		boolean result = template.opsForValue().setIfAbsent(key, value, Duration.ofMillis(100));
		long endDate = System.currentTimeMillis();
		System.out.println("Result=" + result + ", Duration=" + (endDate - beginDate));
		System.out.println("Value=" + template.opsForValue().get(key));
	}
	
	public void testApproveMagrinTrans() throws Exception {
		String url1 = "https://api.newgen.dev/am/admin/accountTransApprovals/5fd33003160152707cd771cf";
		String url2 = "https://api.newgen.dev/am/admin/accountTransApprovals/5fd3302d160152707cd771d0";
		String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuaHVuZ3RyYW4iLCJhdXRoIjpbeyJhdXRob3JpdHkiOiJBRE1JTiJ9XSwiaWF0IjoxNjA3NjcxNjE4LCJleHAiOjE2MDc3MTQ4MTh9.n0KTWeIem0FCksEbY322mpgVm4aSZOQg28Dg-2IxJVk";
		
		for (int i = 1; i <= 20; i++) {
			LocalServiceConnection conn1 = new LocalServiceConnection();
			conn1.sendPostRequest(url1, "", accessToken);
		}
		for (int i = 1; i <= 20; i++) {
			LocalServiceConnection conn2 = new LocalServiceConnection();
			conn2.sendPostRequest(url2, "", accessToken);
		}
	}
}
