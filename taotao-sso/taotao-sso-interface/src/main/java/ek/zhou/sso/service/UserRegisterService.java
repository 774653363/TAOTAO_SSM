package ek.zhou.sso.service;

import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.pojo.TbUser;

/**
 * 用户注册接口
 * 
 * @author Administrator
 *
 */
public interface UserRegisterService {
	/**
	 *  请求的url：/user/check/{param}/{type}
		参数：从url中取参数1、String param（要校验的数据）2、Integer type（校验的数据类型）
		响应的数据：json数据。TaotaoResult，封装的数据校验的结果为true：表示成功，数据可用，false：失败，数据不可用。
	 * @param param	zhangsan是校验的数据
	 * @param type	1、2、3分别代表username、phone、email
	 * @return
	 */
	public TaotaoResult checkData(String param,Integer type);
	/**
	 *  请求的url：/user/register
		参数：表单的数据：username、password、phone、email
		返回值：json数据。TaotaoResult
		接收参数：使用TbUser对象接收。
		请求的方法：post
	 * @param tbUser
	 * @return
	 */
	public TaotaoResult register(TbUser tbUser);
}
