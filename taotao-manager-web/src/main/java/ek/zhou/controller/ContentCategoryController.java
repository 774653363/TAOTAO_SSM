package ek.zhou.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ek.zhou.common.pojo.EasyUITreeNode;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.content.service.ContentCategoryService;

@Controller
public class ContentCategoryController {
	//引用服务
	
	//注入service
	@Autowired
	private ContentCategoryService contentCategoryService;
	/**
	 * 请求的url：/content/category/list
		请求的参数：id，当前节点的id。第一次请求是没有参数，需要给默认值“0”
		响应数据：List<EasyUITreeNode>（@ResponseBody）
		Json数据。
	 */
	@RequestMapping(value="/content/category/list",method=RequestMethod.GET)
	@ResponseBody
	public List<EasyUITreeNode> getContentCategoryList(@RequestParam(value="id",defaultValue="0")Long parentId){
		return contentCategoryService.getContentCategoryList(parentId);
	}
	/**
	 * 请求的url：/content/category/create
		请求的参数：
		Long parentId
		String name
		响应的结果：
		json数据，TaotaoResult
	 * @param parentId
	 * @param name
	 * @return
	 */
	@RequestMapping("/content/category/create")
	@ResponseBody
	public TaotaoResult createContentCategory(long parentId,String name){
		return contentCategoryService.createContentCategory(parentId, name);
	}
	
	
	/**
	 * 请求的url：/content/category/update
		参数：id，当前节点id。name，重命名后的名称。
		业务逻辑：根据id更新记录。
		返回值：返回TaotaoResult.ok()
	 * @param id
	 * @param name
	 * @return
	 */
	@RequestMapping("/content/category/update")
	@ResponseBody
	public TaotaoResult updateContentCategory(long id,String name){
		return contentCategoryService.updateContentCategory(id, name);
	}
	/**
	 * 请求的url：/content/category/delete/
		参数：id，当前节点的id。
		响应的数据：json。TaotaoResult。
	 * @param id
	 * @return
	 */
	@RequestMapping("/content/category/delete")
	@ResponseBody
	public TaotaoResult deleteContentCategory(long id){
		return contentCategoryService.deleteContentCategory(id);
	}
	
}
