package ek.zhou.sso.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * 用户表现层
 * 处理用户注册,登录,退出等业务
 * @author Administrator
 *
 */
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.common.util.CookieUtils;
import ek.zhou.common.util.JsonUtils;
import ek.zhou.pojo.TbUser;
import ek.zhou.sso.service.UserLoginService;
import ek.zhou.sso.service.UserRegisterService;
@Controller
public class UserController {
	//引用服务
	
	//注入service
	@Autowired
	private UserRegisterService userRegisterService;
	@Autowired
	private UserLoginService userLoginService;
	
	//从配置文件中获取用户信息存放到cookie中的key
	@Value("${USER_SESSION_COOKIE_KEY}")
	private String USER_SESSION_COOKIE_KEY;
	//获取应用一级域名
	@Value("${APP_DOMAIN}")
	private String APP_DOMAIN;
	
	/**
	 * 数据校验方法
	 *  请求的url：/user/check/{param}/{type}
		参数：从url中取参数1、String param（要校验的数据）2、Integer type（校验的数据类型）
		响应的数据：json数据。TaotaoResult，封装的数据校验的结果true：成功false：失败。
	 * @param param
	 * @param type
	 * @return
	 */
	@RequestMapping(value="/user/check/{param}/{type}",method=RequestMethod.GET)
	@ResponseBody
	public TaotaoResult checkData(@PathVariable String param,@PathVariable Integer type){
		return userRegisterService.checkData(param, type);
	}
	
	/**注册方法
	 *  请求的url：/user/register
		参数：表单的数据：username、password、phone、email
		返回值：json数据。TaotaoResult
		接收参数：使用TbUser对象接收。
		请求的方法：post
	 * @param tbUser
	 * @return
	 */
	@RequestMapping(value="/user/register",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult register(TbUser tbUser){
		return userRegisterService.register(tbUser);
	}
	/**
	 * 登录方法
	 * 请求的url：/user/login
		请求的方法：POST
		参数：username、password，表单提交的数据。可以使用方法的形参接收。
		HttpServletRequest、HttpServletResponse
		返回值：json数据，使用TaotaoResult包含一个token。
	 * @param username
	 * @param password
	 * @return
	 */
	@RequestMapping(value="/user/login",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult login(HttpServletRequest request,HttpServletResponse response,String username,String password){
		//调用mapper
		TaotaoResult result = userLoginService.login(username, password);
		if(result.getStatus()==200){
			//登录成功后需要将传回来的token放入cookie中反给用户
			String token = (String)result.getData();
			//设置cookie的domain,访问路径,时效等
			CookieUtils.setCookie(request, response, USER_SESSION_COOKIE_KEY, token);
//			Cookie cookie = new Cookie(USER_SESSION_COOKIE_KEY, token);
//			cookie.setDomain("localhost");
//			cookie.setPath("/");
//			response.addCookie(cookie);
		}
		return result;
	}
	
	/**
	 * 根据token获取用户信息方法
	 * 请求的url：/user/token/{token}
		参数：String token需要从url中取。
		接收callback参数，取回调的js的方法名。
		返回值：json数据。使用TaotaoResult包装Tbuser对象。
	 * @param token
	 * @param callback
	 * @return
	 */
	@RequestMapping(value="/user/token/{token}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_UTF8_VALUE+";charset=utf-8")
	@ResponseBody
	public String getUserByToken(@PathVariable String token,String callback){
		//调用Service获取数据
		String result = JsonUtils.objectToJson(userLoginService.getUserByToken(token));
		if(StringUtils.isBlank(callback)){
			//如果callback为空即不是jsonp
			return result;
		}else{
			//返回jsonp格式数据,需要拼接成js格式
			return callback+"("+result+")";
		}
	}
	/**
	 * 安全退出方法
	 * 根据token删除cookie
	 * 调用服务层取消redis中的缓存
	 * url:http://sso.taotao.com/user/logout/{token}
	 * 参数:token  用户登录凭证
	 * 	    callback:jsonp回调方法
	 * @param token
	 * @return
	 */
	@RequestMapping(value="/user/logout/{token}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_UTF8_VALUE+";charset=utf-8")
	@ResponseBody
	public String logout(HttpServletRequest request,HttpServletResponse response,@PathVariable String token,String callback){
		
		//删除cookie
		CookieUtils.deleteCookie(request, response, USER_SESSION_COOKIE_KEY);
		//调用Service服务删除redis缓存
		TaotaoResult taotaoResult = userLoginService.logout(token);
		//将数据转换成json格式
		String result = JsonUtils.objectToJson(taotaoResult);
		if(taotaoResult.getStatus()==200){
			//判断是否是jsonp
			if(StringUtils.isBlank(callback)){
				//不是jsonp返回json格式
				return result;
			}else{
				//返回jsonp格式数据,需要拼接成js格式
				return callback+"("+result+")"; 
			}
		}
		return result;
		
	}
}
