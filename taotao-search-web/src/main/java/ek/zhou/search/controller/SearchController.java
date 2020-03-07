package ek.zhou.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import ek.zhou.common.pojo.SearchResult;
import ek.zhou.search.service.SearchService;

@Controller
public class SearchController {
	//引入服务
	//注入service
	@Autowired
	private SearchService searchService;
	//获取配置文件属性
	@Value("${ITEM_ROWS}")
	private Integer ITEM_ROWS;
	/**
	 * 根据条件搜索商品列表
	 * @param queryString
	 * @param page
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/search")
	public String search(@RequestParam(value="q")String queryString,@RequestParam(defaultValue="1")Integer page,Model model) throws Exception{
		//解决乱码
		queryString = new String(queryString.getBytes("iso-8859-1"),"UTF-8");
		//调用service
		SearchResult result = searchService.search(queryString, page, ITEM_ROWS);
		//设置数据传递到jsp
		model.addAttribute("query",queryString);//查询关键字
		model.addAttribute("totalPages", result.getPageCount());//总页数
		model.addAttribute("itemList", result.getItemList());//查询出的数据
		model.addAttribute("page", page);
		return "search";
	}
}
