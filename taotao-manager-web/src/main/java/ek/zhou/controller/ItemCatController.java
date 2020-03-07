package ek.zhou.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ek.zhou.common.pojo.EasyUITreeNode;
import ek.zhou.service.ItemCatService;

@Controller
public class ItemCatController {
	@Autowired 
	ItemCatService itemCatService;
	
//	初始化tree请求的url：/item/cat/list
//	参数：
//	long id（父节点id）
//	返回值：json。数据格式
//	 List<EasyUITreeNode> 
	@RequestMapping("/item/cat/list")
	@ResponseBody
	public List<EasyUITreeNode> getItemCatList(@RequestParam(value="id",defaultValue="0")Long parentId){
		return itemCatService.getItemCatList(parentId);
	}
}
