package ek.zhou.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;

import ek.zhou.common.pojo.EasyUIDataGridResult;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.pojo.TbItem;
import ek.zhou.service.ItemService;

@Controller
public class ItemController {
	//引入服务
	
	//注入服务
	@Autowired
	private ItemService itemService;
	
	///url:item/list
	//method:get
	//参数:page,rows
	//返回值:json
	@ResponseBody
	@RequestMapping(value="/item/list",method=RequestMethod.GET)
	public EasyUIDataGridResult getItemList(Integer page,Integer rows){
		//调用service服务
		EasyUIDataGridResult dataGridResult = itemService.getItemList(page, rows);
		return dataGridResult;
	}
	/**
	 * 保存商品方法
	 * @param item
	 * @param desc
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/item/save")
	public TaotaoResult saveItem(TbItem item,String desc){
		return itemService.saveItemAndSendMessage(item, desc);
	}
	/**
	 * 跳转到编辑页面方法
	 * @return
	 */
	@RequestMapping("/rest/page/item-edit")
	public String editItem(){
		return "item-edit";
	}
	
	/**
	 * 更新商品方法
	 * @param item
	 * @param desc
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/rest/item/update")
	public TaotaoResult updateItem(TbItem item,String desc){
//		System.out.println(item);
//		System.out.println("desc:"+desc);
		return itemService.updateItemAndSendMessage(item,desc);
	}
	/**
	 * 删除商品方法
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/rest/item/delete")
	public TaotaoResult deleteItem(String ids){
		return itemService.updateItemsAndSendMessage(ids, 3);
	}
	/**
	 * 下架商品方法
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/rest/item/instock")
	public TaotaoResult instockItem(String ids){
		return itemService.updateItemsAndSendMessage(ids,2);
	}
	/**
	 * 上架商品方法
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/rest/item/reshelf")
	public TaotaoResult reshelfItem(String ids){
		return itemService.updateItemsAndSendMessage(ids,1);
	}
}
