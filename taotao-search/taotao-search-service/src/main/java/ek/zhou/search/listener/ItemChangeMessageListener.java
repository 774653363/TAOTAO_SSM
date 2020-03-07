package ek.zhou.search.listener;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;

import ek.zhou.search.service.SearchService;
/**
 * 接受商品改变发送过来的message并作出相应操作
 * 更新索引或删除索引的操作
 * @author Administrator
 *
 */
public class ItemChangeMessageListener implements MessageListener {
	//注入service
	@Autowired
	private SearchService searchService;
	@Override
	public void onMessage(Message message) {
		try {
			//判断消息类型是否是TextMessage
			if(message instanceof TextMessage){
				//获取消息
				String messageStr = ((TextMessage) message).getText();
				//分析消息
				String[] strs = messageStr.split(",");
				if(strs.length==1){
					//插入或者更新商品操作发来的消息
					//调用service服务,根据id查询数据库后更新索引库
					searchService.updateSearchItemById(Long.parseLong(strs[0]));
				}else{
					//删除,上下架商品操作发来的消息
					//获取操作类型
					String method = strs[0];
					if(method.equals("reshelf")){
						//上架操作
						for(int i=1;i<strs.length;i++){
							//调用service服务,根据id查询数据库后更新索引库
							searchService.updateSearchItemById(Long.parseLong(strs[i]));
						}
					}else{
						//下架或者删除操作
						for(int i=1;i<strs.length;i++){
							//调用service服务,根据id删除索引库
							searchService.deleteSearchItemById(Long.parseLong(strs[i]));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
