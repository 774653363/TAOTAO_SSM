package ek.zhou.service.imp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.common.util.JsonUtils;
import ek.zhou.mapper.TbItemDescMapper;
import ek.zhou.pojo.TbItem;
import ek.zhou.pojo.TbItemDesc;
import ek.zhou.service.ItemDescService;
import ek.zhou.service.jedis.JedisClient;
@Service
public class ItemDescServiceImp implements ItemDescService {
	//注册服务
	
	//注入mapper
	@Autowired
	TbItemDescMapper tbItemDescMapper;
	
	
	//注入redis服务
	@Autowired
	private JedisClient jedisClient;
	//从配置文件中获取redis缓存页面详情参数配置
	//存入redis中的商品描述信息key前缀
	@Value("${ITEM_INFO_DESC}")
	private String ITEM_INFO_DESC;
	//设置key过期时间
	@Value("${ITEM_INFO_EXPIRE}")
	private Integer ITEM_INFO_EXPIRE;
	
	
	
	/**
	 * 根据商品id查询数据库中商品中的商品描述信息
	 * 添加redis缓存基本信息
	 * 
	 */
	@Override
	public TaotaoResult getItemDescById(Long itemId) {
		//添加缓存不能影响正常业务
		String key = ITEM_INFO_DESC+itemId;
		try {
			//先从redis中取数据
			String jsonStr = jedisClient.get(key);
			if(StringUtils.isNotBlank(jsonStr)){
				//如果不为空则更新超时时间后将json转换成对象后返回
				//如果为空则从数据库中查询数据并存入缓存中
				//设置超时时间
				jedisClient.expire(key, ITEM_INFO_EXPIRE);
				//返回数据
				TbItemDesc itemDesc = JsonUtils.jsonToPojo(jsonStr, TbItemDesc.class);
				TaotaoResult result = new TaotaoResult(itemDesc);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		TbItemDesc itemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
		TaotaoResult result = new TaotaoResult(itemDesc);
		try {
			if(itemDesc!=null){
				//将从数据库中查询的数据存入redis
				jedisClient.set(key, JsonUtils.objectToJson(itemDesc));
				//设置超时时间
				jedisClient.expire(key, ITEM_INFO_EXPIRE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
