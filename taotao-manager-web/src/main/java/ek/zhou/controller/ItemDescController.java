package ek.zhou.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.service.ItemDescService;

@Controller
public class ItemDescController {
	//引用服务
	
	//注入服务
	@Autowired
	ItemDescService itemDescServiceImp;
	@ResponseBody
	@RequestMapping("/rest/item/query/item/desc/{id}")
	public TaotaoResult getItemDescById(@PathVariable Long id){
		return itemDescServiceImp.getItemDescById(id);
	}
}
