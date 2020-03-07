package ek.zhou.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.search.service.SearchService;

@Controller
public class SearchController {
	//引用服务
	
	//注入service
	@Autowired
	private SearchService searchService;
	
	/**
	 * 导入所有商品到索引库中
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/index/importAll")
	public TaotaoResult importAllSearchItems(){
		System.out.println("正在导入语");
		try {
			TaotaoResult result =  searchService.importAllSearchItems();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return TaotaoResult.build(500, "导入数据失败");
	}
}
