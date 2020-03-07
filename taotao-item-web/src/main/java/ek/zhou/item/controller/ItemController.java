package ek.zhou.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.item.pojo.Item;
import ek.zhou.pojo.TbItem;
import ek.zhou.pojo.TbItemDesc;
import ek.zhou.service.ItemDescService;
import ek.zhou.service.ItemService;

/**
 * 商品详情controller
 * @author Administrator
 *
 */
@Controller
public class ItemController {
	//引用服务
	
	//注入服务
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemDescService itemDescService;
	/**
	 * 请求的url：/item/{itemId}
		参数：商品id
		返回值：String 逻辑视图
	 * @return
	 */
	@RequestMapping("/item/{itemId}")
	public String showItemInfo(@PathVariable Long itemId,Model model){
		//调用service根据id查询数据
		TbItem tbItem = itemService.getItemById(itemId);
		TaotaoResult taotaoResult = itemDescService.getItemDescById(itemId);
		Item item = new Item(tbItem);
		if(taotaoResult.getData() instanceof TbItemDesc){
			TbItemDesc itemDesc = (TbItemDesc) taotaoResult.getData();
			//把数据传递给页面
			model.addAttribute("itemDesc", itemDesc);
		}
		model.addAttribute("item", item);
		return "item";
	}
	
}
