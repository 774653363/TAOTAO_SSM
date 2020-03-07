package ek.zhou.portal.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import ek.zhou.common.util.JsonUtils;
import ek.zhou.content.service.ContentService;
import ek.zhou.pojo.TbContent;
import ek.zhou.portal.pojo.Ad1Node;

/**
 * 展示首页
 * @author Administrator
 *
 */
@Controller
public class PageController {
	//引用服务
	//注入service
	@Autowired
	private ContentService contentService;
	
	/**
	 * 从配置文件中获取大广告的静态信息
	 */
	@Value("${AD1_CATEGORY_ID}")
	private Long AD1_CATEGORY_ID;
	@Value("${AD1_HEIGHT}")
	private String AD1_HEIGHT;
	@Value("${AD1_HEIGHT_B}")
	private String AD1_HEIGHT_B;
	@Value("${AD1_WIDTH}")
	private String AD1_WIDTH;
	@Value("${AD1_WIDTH_B}")
	private String AD1_WIDTH_B;

	
	
	
	/**
	 * 展示首页
	 * @return
	 */
	@RequestMapping("/index")
	public String showIndex(Model model){
		//调用service获取TbContent列表
		List<TbContent> contentList = contentService.getContentListByCategoryId(AD1_CATEGORY_ID);
		//封装到AD1Node列表中
		List<Ad1Node> nodes = new ArrayList<Ad1Node>();
		for(TbContent content:contentList){
			Ad1Node node = new Ad1Node();
			node.setAlt(content.getSubTitle());
			node.setHeight(AD1_HEIGHT);
			node.setHeightB(AD1_HEIGHT_B);
			node.setHref(content.getUrl());
			node.setSrc(content.getPic());
			node.setSrcB(content.getPic2());
			node.setWidth(AD1_WIDTH);
			node.setWidthB(AD1_HEIGHT_B);
			nodes.add(node);
		}
		//转换成json格式
		String json = JsonUtils.objectToJson(nodes);
		//放入model中
		model.addAttribute("ad1", json);
		//跳转
		return "index";
	}
}
