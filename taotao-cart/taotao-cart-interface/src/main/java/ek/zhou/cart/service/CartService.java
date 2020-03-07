package ek.zhou.cart.service;

import java.util.List;

import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.pojo.TbItem;

/**
 * 购物车服务接口
 * @author Administrator
 *
 */
public interface CartService {
	/**
	 * 添加商品到购物车中
	 * @param userId 	用户id
	 * @param item		商品信息
	 * @param num		购买数量
	 * @return
	 */
	TaotaoResult addCart(Long userId, TbItem item, Integer num);
	/**
	 * 根据用户id和商品id查询redis中的商品信息
	 * @param userId	用户id
	 * @param itemId	商品id
	 * @return
	 */
	TbItem getTbItemByUserIdAndItemId(Long userId,Long itemId);
	/**
	 * 根据用户id查询用户在redis中的商品信息列表
	 * @param userId	用户id
	 * @return
	 */
	List<TbItem> getCartListByUserId(Long userId);
	/**
	 * 根据用户id和商品id 更新redis中对应商品的数量
	 * @param id
	 * @param itemId
	 * @param num
	 * @return
	 */
	TaotaoResult updateCartNum(Long userId, Long itemId, Integer num);
	/**
	 * 根据用户id和商品id 删除redis中对应商品的购物项
	 * @param id
	 * @param itemId
	 * @return
	 */
	TaotaoResult deleteCartItem(Long userId, Long itemId);

	/**
	 * 根据用户id删除redis购物车
	 * @param id
	 * @param itemId
	 * @return
	 */
	TaotaoResult deleteCart(Long userId);
	
}
