package ek.zhou.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.item.pojo.Item;
import ek.zhou.pojo.TbItem;
import ek.zhou.pojo.TbItemDesc;
import ek.zhou.service.ItemDescService;
import ek.zhou.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;
/**
 * MQ接受ItemChange消息的监听器
 * 监听到商品改变的信息后
 * 生成新的静态页面
 * @author Administrator
 *
 */
public class ItemChangeGenHtmlMessageListener implements MessageListener {
	//注入service
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemDescService itemDescService;
	//注入configurer
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	@Override
	public void onMessage(Message message) {
		try {
			//接受消息
			if(message instanceof TextMessage){
				//获取消息
				String messageStr = ((TextMessage) message).getText();
				//分析消息
				String[] strs = messageStr.split(",");
				if(strs.length==1){
					//更新静态文件
					//获取商品id
					Long itemId = Long.parseLong(strs[0]);
					//获取商品基本信息
					TbItem tbItem = itemService.getItemById(itemId);
					//转换成页面需要的pojo
					Item item = new Item(tbItem);
					//获取商品描述信息
					TaotaoResult taotaoResult = itemDescService.getItemDescById(itemId);
					TbItemDesc tbItemDesc=(TbItemDesc)taotaoResult.getData();
					//生成静态页面
					genHtml(item,tbItemDesc);
				}else{
					//删除,上下架商品操作发来的消息
					//获取操作类型
					String method = strs[0];
					if(method.equals("reshelf")){
						//上架操作
						for(int i=1;i<strs.length;i++){
							//更新对应静态文件
							//获取商品id
							Long itemId = Long.parseLong(strs[i]);
							//获取商品基本信息
							TbItem tbItem = itemService.getItemById(itemId);
							//转换成页面需要的pojo
							Item item = new Item(tbItem);
							//获取商品描述信息
							TaotaoResult taotaoResult = itemDescService.getItemDescById(itemId);
							TbItemDesc tbItemDesc=(TbItemDesc)taotaoResult.getData();
							//生成静态页面
							genHtml(item,tbItemDesc);
						}
					}else{
						//下架或者删除操作
						for(int i=1;i<strs.length;i++){
							//删除对应静态文件
							File file = new File("G:/freemark/"+strs[i]+".html");
							if(file.exists())
								file.delete();
						}
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 生成静态页面方法
	 * @param item
	 * @param tbItemDesc
	 */
	private void genHtml(Item item, TbItemDesc tbItemDesc) throws Exception{
		//获取Configuration对象
		Configuration configuration = freeMarkerConfigurer.createConfiguration();
		//获取模板对象
		Template template = configuration.getTemplate("item.ftl");
		//生成模板数据
		Map model = new HashMap();
		model.put("item", item);
		model.put("itemDesc", tbItemDesc);
		//创建流对象
		Writer writer = new FileWriter(new File("G:/freemark/"+item.getId()+".html"));
		//产生模板文件
		template.process(model, writer);
		//关闭流
		writer.close();
	}

}
