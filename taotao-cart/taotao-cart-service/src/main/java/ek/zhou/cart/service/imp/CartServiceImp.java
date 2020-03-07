package ek.zhou.cart.service.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ek.zhou.cart.jedis.JedisClient;
import ek.zhou.cart.service.CartService;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.common.util.JsonUtils;
import ek.zhou.mapper.TbItemCatMapper;
import ek.zhou.pojo.TbItem;
/**
 * 购物车实现类
 * 将购物车存入redis中
 * key:购物车redis前缀+用户id
 * field:商品id
 * value:商品json信息
 * @author Administrator
 *
 */
@Service
public class CartServiceImp implements CartService {
	//发布服务
	//注入mapper
	@Autowired
	private TbItemCatMapper tbItemCatMapper;
	//注入jedisClient
	@Autowired
	private JedisClient jedisClient;
	//注入购物车存入redis中的key前缀
	@Value("${CART_REDIS_PRE_KEY}")
	private String CART_REDIS_PRE_KEY;
	
	/**
	 * 添加商品到购物车中
	 * @param userId 	用户id
	 * @param item		商品信息
	 * @param num		购买数量
	 * @return
	 */
	@Override
	public TaotaoResult addCart(Long userId, TbItem item, Integer num) {
		//调用服务查看redis中是否有该商品数据
		TbItem exitItem = getTbItemByUserIdAndItemId(userId, item.getId());
		if(null==exitItem){
			//如果redis中不存在数据
			//将商品图片设置成一张
			String image = item.getImage();
			if(StringUtils.isNotBlank(image)){
				item.setImage(image.split(",")[0]);
			}
			//设置购买数量
			item.setNum(num);
			//将数据转换成json存在redis中
			jedisClient.hset(CART_REDIS_PRE_KEY+userId,item.getId()+"",JsonUtils.objectToJson(item));
		}else{
			//redis已经有数据了
			//将原有的数量增加上新增的数量
			exitItem.setNum(exitItem.getNum()+num);
			//将数据转换成json存在redis中
			jedisClient.hset(CART_REDIS_PRE_KEY+userId,item.getId()+"",JsonUtils.objectToJson(exitItem));
			
		}
		return null;
	}
	/**
	 * 根据用户id和商品id查询redis中的商品信息
	 * @param userId	用户id
	 * @param itemId	商品id
	 * @return
	 */
	
	@Override
	public TbItem getTbItemByUserIdAndItemId(Long userId, Long itemId) {
		String json = jedisClient.hget(CART_REDIS_PRE_KEY+userId, itemId+"");
		if(StringUtils.isNotBlank(json)){
			//不为空则存在商品,将商品json转换成商品返回
			return JsonUtils.jsonToPojo(json, TbItem.class);
		}else{
			//不存在返回null
			return null;
		}
	}
	/**
	 * 根据用户id查询用户在redis中的商品信息列表
	 * @param userId	用户id
	 * @return
	 */
	@Override
	public List<TbItem> getCartListByUserId(Long userId) {
		//根据用户id获取redis中的商品信息列表
		Map<String, String> map = jedisClient.hgetAll(CART_REDIS_PRE_KEY+userId);
		//判断是否为空
		if(map.isEmpty()){
			return null;
		}else{
			//遍历map将商品信息存入list中返回
			List<TbItem> list = new ArrayList<TbItem>();
			//遍历map
			for(String key:map.keySet()){
				//获取值
				String value = map.get(key);
				//将值转换成商品对象存入list中
				list.add(JsonUtils.jsonToPojo(value, TbItem.class));
			}
			//返回list
			return list;
		}
	}
	/**
	 * 根据用户id和商品id 更新redis中对应商品的数量
	 * @param id
	 * @param itemId
	 * @param num
	 * @return
	 */
	
	@Override
	public TaotaoResult updateCartNum(Long userId, Long itemId, Integer num) {
		//根据用户id和商品id查询redis中的商品信息
		TbItem item = getTbItemByUserIdAndItemId(userId, itemId);
		//不为空则存在
		if(null!=item){
			//设置新的商品数量
			item.setNum(num);
			//将数据转换成json存在redis中
			jedisClient.hset(CART_REDIS_PRE_KEY+userId,item.getId()+"",JsonUtils.objectToJson(item));
			//返回成功信息
			return TaotaoResult.ok();
		}
		//不存在则返回失败信息
		return TaotaoResult.build(400, "购物车中不存在该商品!");
	}
	/**
	 * 根据用户id和商品id 删除redis中对应商品的购物项
	 * @param id
	 * @param itemId
	 * @return
	 */
	
	@Override
	public TaotaoResult deleteCartItem(Long userId, Long itemId) {
		//根据用户id和商品id查询redis中的商品信息
		TbItem item = getTbItemByUserIdAndItemId(userId, itemId);
		//不为空则存在
		if(null!=item){
			//将redis中的数据删除
			jedisClient.hdel(CART_REDIS_PRE_KEY+userId,item.getId()+"");
			//返回成功信息
			return TaotaoResult.ok();
		}
		//不存在则返回失败信息
		return TaotaoResult.build(400, "购物车中不存在该商品!");
	}
	@Override
	public TaotaoResult deleteCart(Long userId) {
		jedisClient.del(CART_REDIS_PRE_KEY+userId);
		return TaotaoResult.ok();
	}
}
