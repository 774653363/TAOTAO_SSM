package ek.zhou.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import ek.zhou.cart.service.CartService;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.common.util.CookieUtils;
import ek.zhou.order.service.OrderService;
import ek.zhou.pojo.TbUser;
import ek.zhou.sso.service.UserLoginService;
/**
 * 拦截确认订单,创建订单的拦截器
 * 
 * @author Administrator
 *
 */
public class LoginInterceptor implements HandlerInterceptor {

	//注入服务
	@Autowired
	private UserLoginService userLoginService;
	@Autowired 
	private CartService cartService;
	
	
	//引入配置文件属性
	//用户信息存放到cookie中的key
	@Value("${USER_SESSION_COOKIE_KEY}")
	private String USER_SESSION_COOKIE_KEY;
	//sso服务用户登录的url
	@Value("${SSO_LOGIN_URL}")
	private String SSO_LOGIN_URL;
	
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
		// 返回ModelAndView之后，执行。异常处理。
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// 执行Handler之后返回ModelAndView之前

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		//这里进行用户身份的认证，在进入目标方法之前执行该方法。如果返回为false表示拦截，不让访问，如果是true，表示放行。
		//创建用户对象,获取用户信息
		TbUser tbUser = null;
		//获取cookie中的token
		String token = CookieUtils.getCookieValue(request, USER_SESSION_COOKIE_KEY);
		//判断token是否为空
		if(StringUtils.isNotBlank(token)){
			//使用sso服务判断用户信息是否过期
			TaotaoResult result = userLoginService.getUserByToken(token);
			//返回成功
			if(result.getStatus()==200){
				//创建用户对象,获取用户信息
				tbUser = (TbUser)result.getData();
				//将用户信息存入request中
				request.setAttribute("user", tbUser);
				//放行
				return true;
			}
		}
		//用户不存在重定向到登录页面
		response.sendRedirect(SSO_LOGIN_URL+"?redirect="+request.getRequestURL());
		//拦截
		return false;
	}

}
