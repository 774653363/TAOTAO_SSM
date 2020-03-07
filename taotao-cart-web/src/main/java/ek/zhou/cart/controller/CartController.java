package ek.zhou.cart.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ek.zhou.cart.service.CartService;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.common.util.CookieUtils;
import ek.zhou.common.util.JsonUtils;
import ek.zhou.pojo.TbItem;
import ek.zhou.pojo.TbUser;
import ek.zhou.service.ItemService;
import ek.zhou.sso.service.UserLoginService;

@Controller
public class CartController {
	//引用商品服务
	//引用sso服务
	//引入cart服务
	//注入服务
	@Autowired
	private ItemService itemService;
	@Autowired
	private UserLoginService userLoginService;
	@Autowired
	private CartService cartService;
	//注入配置文件中用户信息token存放在cookie中的key
	@Value("${USER_SESSION_COOKIE_KEY}")
	private String USER_SESSION_COOKIE_KEY;
	//注入配置文件中购物车存放在cookie中的key
	@Value("${CART_COOKIE_KEY}")
	private String CART_COOKIE_KEY;
	//注入配置文件中购物车存放在cookie中的过期时间
	@Value("${CART_COOKIE_KEY_EXPIRE}")
	private Integer CART_COOKIE_KEY_EXPIRE;
	
	/**
	 * 根据cookie中的用户token查询用户信息,
		用户已登录,调用cartservice服务将购物项添加到redis中
		用户未登录,调用cookie服务将购物项添加到cookie中
	 * url: /cart/add/{itemId}
		参数：itemId,num
		返回值：添加购物车成功页面。
	 * @return
	 */
	
	@RequestMapping("/cart/add/{itemId}")
	public String addCart(@PathVariable Long itemId,Integer num,HttpServletRequest request,HttpServletResponse response){
		//从cookie中获取token信息
		String token = CookieUtils.getCookieValue(request, USER_SESSION_COOKIE_KEY);
		//调用用户service服务
		TaotaoResult result = userLoginService.getUserByToken(token);
		//结果状态为200,就是用户已经登录了
		if(result.getStatus()==200){
			//获取用户信息
			TbUser tbUser = (TbUser) result.getData();
			//调用商品service获取商品信息
			TbItem item = itemService.getItemById(itemId);
			//调用购物车服务将数据添加到redis中
			cartService.addCart(tbUser.getId(),item,num);
			
		}else{
			//用户还没登录
			//调用cookie服务将购物项添加到cookie中
			addCartToCookie(request,response,itemId,num);
		}
		
		return "cartSuccess";
	}


	/**
	 *  url: /cart/cart
		参数：无
		返回值：购物车展示列表的页面
	 * @return
	 */
	@RequestMapping("/cart/cart")
	public String showCart(HttpServletRequest request,HttpServletResponse response){
		//获取cookie中的用户token
		String token = CookieUtils.getCookieValue(request, USER_SESSION_COOKIE_KEY);
		//调用sso服务,获取用户信息
		TaotaoResult result = userLoginService.getUserByToken(token);
		if(result.getStatus()==200){
			//用户已登录
			//获取用户信息
			TbUser user = (TbUser)result.getData();
			//合并cookie中的购物车到redis中
			addCartFromCookieToRedis(request,response,user.getId());
			//调用购物车service获取对应用户在redis中的购物车中的商品列表
			List<TbItem> list = cartService.getCartListByUserId(user.getId());
			request.setAttribute("cartList", list);
		}else{
			//用户未登录,从cookie服务中获取购物车信息
			List<TbItem> list = getCartListFromCookie(request);
			//将list存入Request中
			if(null!=list){
				request.setAttribute("cartList", list);
			}
		}
		return "cart";
	}



	/**
	 * 更新指定用户的购物车中指定商品的数量
	 * 
	 *  url: /cart/update/num/{itemId}/{num} 
		参数：商品的id 和更新后的商品数量
		返回值：taotaoResult(只要有值)
	 * @return
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public TaotaoResult updateCartNum(HttpServletRequest request,HttpServletResponse response,@PathVariable Long itemId,@PathVariable Integer num ){
		//获取cookie中的用户token
		String token = CookieUtils.getCookieValue(request, USER_SESSION_COOKIE_KEY);
		//调用sso服务,获取用户信息
		TaotaoResult result = userLoginService.getUserByToken(token);
		if(result.getStatus()==200){
			//用户已登录
			//获取用户信息
			TbUser user = (TbUser)result.getData();
			//调用购物车service更新对应用户在redis中的购物车中的商品的数量
			return cartService.updateCartNum(user.getId(),itemId,num);
		}else{
			//未登录,调用cookie服务更新商品数量
			return updateCartNumFromCookie(request, response, itemId, num);
		}
	}
	
	/**
	 * 删除指定用户的购物车中指定商品
	 * 
	 *  url: /cart/delete/143771131488369.html
		参数：
		根据cookie中的用户token查询用户信息,
		用户已登录根据商品id删除redis中的购物项,
		用户未登录根据商品id删除cookie中的购物项
		返回值：taotaoResult(只要有值)
	 * @return
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartItem(HttpServletRequest request,HttpServletResponse response,@PathVariable Long itemId ){
		//获取cookie中的用户token
		String token = CookieUtils.getCookieValue(request, USER_SESSION_COOKIE_KEY);
		//调用sso服务,获取用户信息
		TaotaoResult result = userLoginService.getUserByToken(token);
		if(result.getStatus()==200){
			//用户已登录
			//获取用户信息
			TbUser user = (TbUser)result.getData();
			//调用购物车service删除对应用户在redis中的购物车中的商品
			cartService.deleteCartItem(user.getId(), itemId);
			
			return "redirect:/cart/cart.html";
		}else{
			//未登录
			deleteCartItemFromCookie(request,response,itemId);
			return "redirect:/cart/cart.html";
		}
	}
	
	



	//----------------------------------私有方法分割线--------------------------
	
	/**
	 * 将购物项存入cookie中
	 * @param request
	 * @param response
	 * @param itemId
	 * @param num
	 */
	private void addCartToCookie(HttpServletRequest request, HttpServletResponse response, Long itemId, Integer num) {
		//查询cookie中购物车的购物项列表
		List<TbItem> list = getCartListFromCookie(request);
		//判断列表是否为空
		//设置标签
		boolean flag = false;
		if(null!=list){
			//不为空,判断对应商品是否在列表中
			for (TbItem tbItem : list) {
				//如果存在
				if(tbItem.getId().longValue()==itemId.longValue()){
					//修改list中商品信息
					tbItem.setNum(num+tbItem.getNum());
					//修改标签
					flag=true;
					break;
				}
			}
		}else{
			//如果列表不存在
			//创建列表
			list = new ArrayList<>();
		}
		//如果商品不存在
		if(!flag){
			//调用商品service服务查询商品信息
			TbItem item = itemService.getItemById(itemId);
			//将商品图片设置成一张
			String image = item.getImage();
			if(StringUtils.isNotBlank(image)){
				item.setImage(image.split(",")[0]);
			}
			//设置购买数量
			item.setNum(num);
			//将购物项放入列表中
			list.add(item);
		}
		
		//将列表转换成json放入cookie中,并设置生存时间
		CookieUtils.setCookie(request, response, CART_COOKIE_KEY, JsonUtils.objectToJson(list), CART_COOKIE_KEY_EXPIRE,true);
	}
	/**
	 * 查询cookie中购物车的购物项列表
	 * @param request
	 * @param response
	 * @return
	 */
	private List<TbItem> getCartListFromCookie(HttpServletRequest request){
		//查询cookie中购物车的购物项列表json
		String listJson = CookieUtils.getCookieValue(request, CART_COOKIE_KEY,true);
		if(StringUtils.isNoneBlank(listJson)){
			//listJson不为空则转换成list格式返回
			return JsonUtils.jsonToList(listJson, TbItem.class);
		}else{
			//为空返回null
			return null;
		}
	}
	
	/**
	 * 更新cookie中商品的数量
	 * @param request
	 * @param response
	 * @param itemId
	 * @param num
	 * @return
	 */
	private TaotaoResult updateCartNumFromCookie(HttpServletRequest request, HttpServletResponse response, Long itemId, Integer num){
				//查询cookie中购物车的购物项列表
				List<TbItem> list = getCartListFromCookie(request);
				//判断列表是否为空
				//设置标签
				if(null!=list){
					//不为空,判断对应商品是否在列表中
					for (TbItem tbItem : list) {
						//如果存在
						if(tbItem.getId().longValue()==itemId.longValue()){
							//修改list中商品信息
							tbItem.setNum(num);
							//将列表转换成json放入cookie中,并设置生存时间
							CookieUtils.setCookie(request, response, CART_COOKIE_KEY, JsonUtils.objectToJson(list), CART_COOKIE_KEY_EXPIRE,true);
							return TaotaoResult.ok();
						}
					}
				}else{
					//如果列表不存在
					return TaotaoResult.build(400, "购物车不存在该商品!");
				}
				
				return null;
	}
	/**
	 * 删除cookie中对应购物项
	 * @param request
	 * @param response
	 * @param itemId
	 */
	private void deleteCartItemFromCookie(HttpServletRequest request, HttpServletResponse response, Long itemId) {
		//查询cookie中购物车的购物项列表
		List<TbItem> list = getCartListFromCookie(request);
		//判断列表是否为空
		//设置标签
		if(null!=list){
			//不为空,判断对应商品是否在列表中
			for (TbItem tbItem : list) {
				//如果存在
				if(tbItem.getId().longValue()==itemId.longValue()){
					//删除list中商品信息
					list.remove(tbItem);
					//将列表转换成json放入cookie中,并设置生存时间
					CookieUtils.setCookie(request, response, CART_COOKIE_KEY, JsonUtils.objectToJson(list), CART_COOKIE_KEY_EXPIRE,true);
					return ;
				}
			}
		}else{
			//如果列表不存在
			return ;
		}
	}
	
	/**
	 * 合并cookie中的购物车到redis中
	 * @param request
	 * @param userId 
	 */
	private void addCartFromCookieToRedis(HttpServletRequest request, HttpServletResponse response, Long userId) {
		//查询cookie中购物车的购物项列表
		List<TbItem> list = getCartListFromCookie(request);
		//判断列表是否为空
		if(null!=list&&list.size()>0){
			//遍历list,调用Cartservice将cookie中的购物项存入redis中
			for (TbItem tbItem : list) {
				//cookie中的购物项存入redis中
				cartService.addCart(userId, tbItem, tbItem.getNum());
			}
			//将cookie删除
			CookieUtils.deleteCookie(request, response, CART_COOKIE_KEY);
		}
	}
	
}
