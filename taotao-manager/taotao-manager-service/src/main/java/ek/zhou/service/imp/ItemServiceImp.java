package ek.zhou.service.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import ek.zhou.common.pojo.EasyUIDataGridResult;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.common.util.IDUtils;
import ek.zhou.common.util.JsonUtils;
import ek.zhou.mapper.TbItemDescMapper;
import ek.zhou.mapper.TbItemMapper;
import ek.zhou.pojo.TbItem;
import ek.zhou.pojo.TbItemDesc;
import ek.zhou.pojo.TbItemExample;
import ek.zhou.service.ItemService;
import ek.zhou.service.jedis.JedisClient;
@Service
public class ItemServiceImp implements ItemService {
	//注入Mapper
	@Autowired
	private TbItemMapper tbItemMapper;
	@Autowired
	private TbItemDescMapper tbItemDescMapper;
	
	//注入jsmTemplate
	@Autowired
	private JmsTemplate jmsTempalate;
	//注入MQ目的地
	@Resource(name="topicDestination")
	private Destination distination;
	
	
	//注入redis服务
	@Autowired
	private JedisClient jedisClient;
	//从配置文件中获取redis缓存页面详情参数配置
	//存入redis中的商品基础信息key前缀
	@Value("${ITEM_INFO_BASE}")
	private String ITEM_INFO_BASE;
	//设置key过期时间
	@Value("${ITEM_INFO_EXPIRE}")
	private Integer ITEM_INFO_EXPIRE;
	//存入redis中的商品描述信息key前缀
	@Value("${ITEM_INFO_DESC}")
	private String ITEM_INFO_DESC;
	
	
	
	@Override
	public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
		//1.设置分页信息
		if(page==null||page<=0)
			page=1;
		if(rows==null||rows<=0)
			rows=30;
		PageHelper.startPage(page, rows);
		//2.创建example对象
		TbItemExample example = new TbItemExample();
		//3.根据Mapper调用查询所有数据方法
		List<TbItem> list = tbItemMapper.selectByExample(example);
		//4.获取分页信息
		PageInfo<TbItem> info = new PageInfo<>(list);
		//5.封装到EasyUIDataGridResult
		EasyUIDataGridResult dataGridResult = new EasyUIDataGridResult();
		dataGridResult.setRows(info.getList());
		dataGridResult.setTotal((int)info.getTotal());
		//6.返回
		return dataGridResult;
	}
	@Override
	public TaotaoResult saveItem(TbItem item, String desc) {
		//1.生成商品id
		long itemID = IDUtils.genItemId();
		
		//2.补全TbItem对象属性
		item.setId(itemID);
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte)1);
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		//3.向商品表插入数
		tbItemMapper.insert(item);
		//4.创建一个TbItemDesc对象
		TbItemDesc itemDesc = new TbItemDesc();
		//5.补全TbItemDesc对象属性
		itemDesc.setItemId(itemID);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		//6.向商品描述表插入数据
		tbItemDescMapper.insert(itemDesc);
		
		
		
		//7.返回TaotaoResult.ok()
		return TaotaoResult.ok();
	}
	
	@Override
	public TaotaoResult updateItem(TbItem item, String desc) {
		
		//1.设置更新时间
		item.setUpdated(new Date());
		//2.调用mapper更新商品
		tbItemMapper.updateByPrimaryKeySelective(item);
		//3.建立商品详情并设置信息
		TbItemDesc itemDesc = new TbItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(item.getUpdated());
		//调用mapper更新信息
		tbItemDescMapper.updateByPrimaryKeySelective(itemDesc);
		
		//清除redis中的商品基本信息和商品描述信息缓存
		try {
			String baseKey = ITEM_INFO_BASE+item.getId();
			String descKey = ITEM_INFO_DESC+item.getId();
			if(StringUtils.isNotBlank(jedisClient.get(baseKey)))
				jedisClient.set(baseKey, "");
			if(StringUtils.isNotBlank(jedisClient.get(descKey)))
				jedisClient.set(descKey, "");
//			System.out.println("从redis中清除了商品详情缓存");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return TaotaoResult.ok();
	}
	@Override
	public TaotaoResult updateItems(String ids,int status) {
		List<Long> idsList = new ArrayList<>();
		String[]idList=ids.split(",");
		for(String id:idList){
			idsList.add(Long.parseLong(id));
		}
		//声明条件
		TbItemExample example = new TbItemExample();
		example.createCriteria().andIdIn(idsList);
		//设置status
		TbItem item = new TbItem();
		item.setStatus((byte)status);
		//调用mapper更新数据
		tbItemMapper.updateByExampleSelective(item, example);
		
		//清除redis中的商品基本信息和商品描述信息缓存
		try {
			for(String id:idList){
			String baseKey = ITEM_INFO_BASE+id;
			String descKey = ITEM_INFO_DESC+id;
			if(StringUtils.isNotBlank(jedisClient.get(baseKey)))
				jedisClient.set(baseKey, "");
			if(StringUtils.isNotBlank(jedisClient.get(descKey)))
				jedisClient.set(descKey, "");
			
			}
			
//			System.out.println("从redis中清除了商品详情缓存");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return TaotaoResult.ok();
	}
	@Override
	public TaotaoResult saveItemAndSendMessage(TbItem item, String desc) {
		//调用service
		TaotaoResult taotaoResult = this.saveItem(item, desc);
		
		//生成商品id
		final long itemID = item.getId();
		//添加发送消息的业务逻辑
				jmsTempalate.send(distination,new MessageCreator() {
					
					@Override
					public Message createMessage(Session session) throws JMSException {
						//发送的消息
						return session.createTextMessage(itemID+"");
					}
		});
				
		return taotaoResult;
	}
	@Override
	public TaotaoResult updateItemAndSendMessage(TbItem item, String desc) {
		//调用service服务
		TaotaoResult taotaoResult = this.updateItem(item, desc);
		//添加发送消息的业务逻辑
				final long itemID = item.getId();
				jmsTempalate.send(distination,new MessageCreator() {
					
					@Override
					public Message createMessage(Session session) throws JMSException {
						//发送的消息
						return session.createTextMessage(itemID+"");
					}
				});
				
		return taotaoResult;
	}
	@Override
	public TaotaoResult updateItemsAndSendMessage(String ids, int status) {
		//status :1:reshelf ,2:instock  ,3:delete
		String statusStr = "";
		if(status==1){
			statusStr = "reshelf";
		}else if(status==2){
			statusStr = "instock";
		}else if(status==3){
			statusStr = "delete";
		}
		//调用service服务
		TaotaoResult taotaoResult =  this.updateItems(ids, status);
		//添加发送消息的业务逻辑
				final String idsStr = statusStr+","+ids;
				jmsTempalate.send(distination,new MessageCreator() {
					
					@Override
					public Message createMessage(Session session) throws JMSException {
						//发送的消息
						return session.createTextMessage(idsStr);
					}
				});
				
		return taotaoResult;
	}
	/**
	 * 根据商品id查询数据库中商品中的商品基本信息
	 * 添加redis缓存基本信息
	 * 
	 */
	@Override
	public TbItem getItemById(long itemId) {
		//添加缓存不能影响正常业务
		String key = ITEM_INFO_BASE+itemId;
		try {
			//先从redis中取数据
			String jsonStr = jedisClient.get(key);
			if(StringUtils.isNotBlank(jsonStr)){
				//如果不为空则更新超时时间后将json转换成对象后返回
				//如果为空则从数据库中查询数据并存入缓存中
				//设置超时时间
				jedisClient.expire(key, ITEM_INFO_EXPIRE);
				//返回数据
//				System.out.println("从redis中取到了商品详情缓存");
				return JsonUtils.jsonToPojo(jsonStr, TbItem.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//调用mapper方法根据id查询数据
		TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
		try {
			if(item!=null){
				//将从数据库中查询的数据存入redis
				jedisClient.set(key, JsonUtils.objectToJson(item));
				//设置超时时间
				jedisClient.expire(key, ITEM_INFO_EXPIRE);
//				System.out.println("添加商品详情缓存到redis中了");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}

}
