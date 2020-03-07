package ek.zhou.order.service.imp;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.mapper.TbOrderItemMapper;
import ek.zhou.mapper.TbOrderMapper;
import ek.zhou.mapper.TbOrderShippingMapper;
import ek.zhou.order.jedis.JedisClient;
import ek.zhou.order.pojo.OrderInfo;
import ek.zhou.order.service.OrderService;
import ek.zhou.pojo.TbOrderItem;
import ek.zhou.pojo.TbOrderShipping;
/**
 * 订单service实现类
 * @author Administrator
 *
 */
@Service
public class OrderServiceImp implements OrderService {
	//注入jedis
	@Autowired
	private JedisClient jedisClient;
	//注入mapper
	@Autowired
	private TbOrderMapper tbOrderMapper;
	@Autowired
	private TbOrderItemMapper tbOrderItemMapper;
	@Autowired
	private TbOrderShippingMapper tbOrderShippingMapper;
	
	//注入配置文件属性
	//订单id存放在redis中的增长key
	@Value("${ORDER_ID_GEN_KEY}")
	private String ORDER_ID_GEN_KEY;
	//订单明细id存放在redis中的增长key
	@Value("${ORDER_ITEM_ID_GEN_KEY}")
	private String ORDER_ITEM_ID_GEN_KEY;
	//订单id存放在redis中的的初始值
	@Value("${ORDER_ID_INIT}")
	private String ORDER_ID_INIT;
	/**
	 * 创建订单方法
	 */
	@Override
	public TaotaoResult createOrder(OrderInfo info) {
		//判断id是否存在
		if(!jedisClient.exists(ORDER_ID_GEN_KEY)){
			//不存在就初始化
			jedisClient.set(ORDER_ID_GEN_KEY, ORDER_ID_INIT);
		}
		//生成订单id
		String orderId = jedisClient.incr(ORDER_ID_GEN_KEY).toString(); 
		//设置订单信息
		info.setOrderId(orderId);
		info.setPostFee("0");
		//1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		info.setStatus(1);
		info.setCreateTime(new Date());
		info.setUpdateTime(info.getCreateTime());
		//向订单表中插入数据
		tbOrderMapper.insert(info);
		//向订单明细表插入数据
		List<TbOrderItem> list = info.getOrderItems();
		for (TbOrderItem tbOrderItem : list) {
			//生成明细id
			String orderItemId = jedisClient.incr(ORDER_ITEM_ID_GEN_KEY).toString();
			tbOrderItem.setId(orderItemId);
			tbOrderItem.setOrderId(orderId);
			//插入数据
			tbOrderItemMapper.insert(tbOrderItem);
		}
		//向订单物流表插入数据
		TbOrderShipping orderShipping = info.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(info.getCreateTime());
		orderShipping.setUpdated(info.getCreateTime());
		//插入数据
		tbOrderShippingMapper.insert(orderShipping);
		return TaotaoResult.ok(orderId);
	}

}
