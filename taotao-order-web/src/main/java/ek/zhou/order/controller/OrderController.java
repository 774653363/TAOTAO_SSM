package ek.zhou.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ek.zhou.cart.service.CartService;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.common.util.CookieUtils;
import ek.zhou.common.util.JsonUtils;
import ek.zhou.order.pojo.OrderInfo;
import ek.zhou.order.service.OrderService;
import ek.zhou.pojo.TbItem;
import ek.zhou.pojo.TbUser;
import ek.zhou.sso.service.UserLoginService;

@Controller
public class OrderController {
	//引入服务
	
	//注入服务
	@Autowired
	private UserLoginService userLoginService;
	@Autowired 
	private CartService cartService;
	@Autowired
	private OrderService orderService;
	//引入配置文件属性
	//用户信息存放到cookie中的key
	@Value("${USER_SESSION_COOKIE_KEY}")
	private String USER_SESSION_COOKIE_KEY;
	//购物车存放在cookie中的key
	@Value("${CART_COOKIE_KEY}")
	private String CART_COOKIE_KEY;
	/**
	 * 
	 * 确认订单方法
	 * 
	 * 请求的url：/order/order-cart
		参数：没有参数。cookie获取用户
		返回值：逻辑视图String，展示订单确认页面。
	 * @return
	 */
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request,HttpServletResponse response){
		//存储redis中的购物车列表
		List<TbItem> redisList = null;
		//存储cookie中的购物车列表
		List<TbItem> cookieList = null;
		//创建用户对象,从Request中获取用户信息
		TbUser tbUser = (TbUser) request.getAttribute("user");
//		//获取cookie中的token
//		String token = CookieUtils.getCookieValue(request, USER_SESSION_COOKIE_KEY);
//		//判断token是否为空
//		if(StringUtils.isNotBlank(token)){
//			//使用sso服务判断用户信息是否过期
//			TaotaoResult result = userLoginService.getUserByToken(token);
//			//返回成功
//			if(result.getStatus()==200){
//				//创建用户对象,获取用户信息
//				tbUser = (TbUser)result.getData();
//				
//			}
//		}
		//获取cookie中的购物车信息
		String cookieListJson = CookieUtils.getCookieValue(request, CART_COOKIE_KEY,true);
		//判断是否为空
		if(StringUtils.isNotBlank(cookieListJson)){
			//不为空将json转换成list
			cookieList = JsonUtils.jsonToList(cookieListJson, TbItem.class);
		}
		//如果用户已经登录,并且cookie中存有购物车则将cookie中的购物车合并到redis中.并清除cookie中的购物车
		if(null!=tbUser&&null!=cookieList&&cookieList.size()>0){
			//合并购物车
			for (TbItem tbItem : cookieList) {
				cartService.addCart(tbUser.getId(), tbItem, tbItem.getNum());
			}
			//删除cookie中购物车
			CookieUtils.deleteCookie(request, response, CART_COOKIE_KEY);
		}
		
		//判断用户是否登录
		if(null!=tbUser){
			//用户已登录
			//调用cart服务获取用户在redis中的购物车信息
			redisList = cartService.getCartListByUserId(tbUser.getId());
			request.setAttribute("cartList", redisList);
		}
		if(null==tbUser&&null!=cookieList&&cookieList.size()>0){
			//用户未登录,但cookie中有购物车
			request.setAttribute("cartList", cookieList);
		}
		
		return "order-cart";
	}
	/**
	 * 创建订单方法
	 * 请求的url：/order/create
		参数：使用OrderInfo接收
		返回值：逻辑视图。（页面应该显示订单号）
	 * @return
	 */
	@RequestMapping("/order/create")
	public String createOrder(OrderInfo info,HttpServletRequest request){
		//从Request中获取用户信息
		TbUser tbUser = (TbUser) request.getAttribute("user");
		//补全用户信息
		//设置用户昵称
		info.setBuyerNick(tbUser.getUsername());
		//设置用户id
		info.setUserId(tbUser.getId());
		//调用service创建订单
		TaotaoResult result = orderService.createOrder(info);
		//获取订单id
		String orderId = result.getData().toString();
		//需要返回订单id，总金额和预计到达时间
		request.setAttribute("orderId", orderId);
		request.setAttribute("payment", info.getPayment());
		//预计到达时间为当前日期加上三天
		DateTime dateTime = new DateTime();
		dateTime.plusDays(3);
		request.setAttribute("date", dateTime.toString("yyyy-MM-dd"));
		//调用cartService删除用户在redis中的购物车
		cartService.deleteCart(tbUser.getId());
		//返回逻辑视图，展示成功页面
		return "success";
	}
}
