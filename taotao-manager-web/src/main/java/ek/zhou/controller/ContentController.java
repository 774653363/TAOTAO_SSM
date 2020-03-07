package ek.zhou.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ek.zhou.common.pojo.EasyUIDataGridResult;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.content.service.ContentService;
import ek.zhou.pojo.TbContent;

@Controller
public class ContentController {
	//引入服务
	
	//注入Service
	@Autowired
	private ContentService contentService;
	
	
	
	/**
	 * 
	 *  内容列表查询
		请求的url：/content/query/list
		参数：categoryId 分类id
		响应的数据：json数据
		{total:查询结果总数量,rows[{id:1,title:aaa,subtitle:bb,...}]}
		EasyUIDataGridResult
		描述商品数据List<TbContent>
		查询的表：tb_content
	 * @param categoryId
	 * @param page
	 * @param rows
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/content/query/list")
	public EasyUIDataGridResult listContent(long categoryId, Integer page, Integer rows){
		EasyUIDataGridResult dataGridResult = contentService.listContent(categoryId, page, rows);
		return dataGridResult;
	}
	/**
	 * 提交表单请求的url：/content/save
		参数：表单的数据。使用pojo接收TbContent
		返回值：TaotaoResult（json数据）
	 * @param tbContent
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/content/save")
	public TaotaoResult addContent(TbContent tbContent){
		return contentService.addContent(tbContent);
	}
	/**
		URL: /rest/item/update
		参数：表单数据（tbContent 来接收）
		返回值：taotaoResult 
	 * @param tbContent
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/content/update")
	public TaotaoResult updateContent(TbContent tbContent){
		return contentService.updateContent(tbContent);
	}
	/**
	 * URL: /content/delete
		参数: ids    (一个内容id拼成的字符串)
		返回值:taotaoresult 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/content/delete")
	public TaotaoResult deleteContent(String ids){
		return contentService.deleteContent(ids);
	}
}
