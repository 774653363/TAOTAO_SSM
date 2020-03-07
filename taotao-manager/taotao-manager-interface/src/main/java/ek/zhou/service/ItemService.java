package ek.zhou.service;

import ek.zhou.common.pojo.EasyUIDataGridResult;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.pojo.TbItem;

/**
 * 商品相关处理的service接口
 * @author Administrator
 *
 */

public interface ItemService{
	/**
	 * 根据当前页码和每页行数进行分页显示
	 * @param page
	 * @param rows
	 * @return
	 */
	public EasyUIDataGridResult getItemList(Integer page,Integer rows);
	/**
	 * 根据ItemId查询商品详情
	 * @param itemId
	 * @return
	 */
	public TbItem getItemById(long itemId);
	/**
	 * 根据商品的基础数据和商品的描述信息插入商品(插入商品基础表和商品描述表)
	 * @param item
	 * @param desc
	 * @return
	 */
	public TaotaoResult saveItem(TbItem item,String desc);
	/**
	 * 更新商品基础数据和商品描述信息
	 * @param item
	 * @param desc
	 * @return
	 */
	public TaotaoResult updateItem(TbItem item, String desc);
	/**
	 * 根据ids和status删除商品,上下架商品
	 * @param ids
	 * @return
	 */
	public TaotaoResult updateItems(String ids,int status);
	/**
	 * 根据商品的基础数据和商品的描述信息插入商品(插入商品基础表和商品描述表)
	 * 并且发送商品改变的信息到消息队列中
	 * @param item
	 * @param desc
	 * @return
	 */
	public TaotaoResult saveItemAndSendMessage(TbItem item,String desc);
	/**
	 * 更新商品基础数据和商品描述信息
	 * 并且发送商品改变的信息到消息队列中
	 * @param item
	 * @param desc
	 * @return
	 */
	public TaotaoResult updateItemAndSendMessage(TbItem item, String desc);
	/**
	 * 根据ids和status删除商品,上下架商品
	 * 并且发送商品改变的信息到消息队列中
	 * @param ids
	 * @return
	 */
	public TaotaoResult updateItemsAndSendMessage(String ids,int status);
	
	
}
