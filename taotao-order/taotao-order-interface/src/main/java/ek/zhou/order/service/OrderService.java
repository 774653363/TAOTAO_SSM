package ek.zhou.order.service;

import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.order.pojo.OrderInfo;
/**
 * 订单方法接口
 * @author Administrator
 *
 */
public interface OrderService {

	/**
	 * 创建订单方法
	 * @param info
	 * @return
	 */
	TaotaoResult createOrder(OrderInfo info);

}
