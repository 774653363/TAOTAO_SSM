package ek.zhou.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ek.zhou.mapper.TestMapper;
import ek.zhou.service.TestService;
@Service
public class TestServiceImp implements TestService{
	//注入Mapper
	@Autowired
	TestMapper testMapper;
	@Override
	public String queryNow() {
		//调用mapper方法
		return testMapper.queryNow();
	}

}
