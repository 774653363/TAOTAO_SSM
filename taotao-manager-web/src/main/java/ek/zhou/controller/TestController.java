package ek.zhou.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ek.zhou.service.TestService;

/**
 * 测试使用的Controller查询当前时间
 * @author Administrator
 *
 */
@Controller
public class TestController {
	//在springmvc引入服务
	//注入服务
	@Autowired
	TestService testService;
	@ResponseBody
	@RequestMapping("/test/queryNow")
	public String queryNow(){ 
		return testService.queryNow();
	}
}
