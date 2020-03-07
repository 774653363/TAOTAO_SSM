package ek.zhou.sso.service;

import ek.zhou.common.pojo.TaotaoResult;

/**
 * 用户登录接口
 * @author Administrator
 *
 */
public interface UserLoginService {
	/**
	 * 	用户登录方法,
	 * 校验数据后
	 * 查询数据库中账号和密码是否一致
	 * 登录成功后
	 * 生成token
	 * 将token作为key,将用户信息转换成json存入redis(模仿Session)
	 * 设置超时时间
	 * 将token放入taotaoresult中返回
	 *  请求的url：/user/login
		请求的方法：POST
		参数：username、password，表单提交的数据。可以使用方法的形参接收。
		返回值：json数据，使用TaotaoResult包含一个token。
	 * @param username
	 * @param password
	 * @return
	 */
	public TaotaoResult login(String username,String password);
	/**
	 * 根据token查询redis中对应的user信息
	 * @param token
	 * @return
	 */
	public TaotaoResult getUserByToken(String token);
	
	/**
	 * 根据token删除redis中对应的user信息
	 * @param token
	 * @return
	 */
	public TaotaoResult logout(String token);
	
}
