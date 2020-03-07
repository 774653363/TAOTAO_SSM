package ek.zhou.sso.service.imp;


import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.mapper.TbUserMapper;
import ek.zhou.pojo.TbUser;
import ek.zhou.pojo.TbUserExample;
import ek.zhou.pojo.TbUserExample.Criteria;
import ek.zhou.sso.service.UserRegisterService;
/**
 * 用户注册接口实现类
 * @author Administrator
 *
 */
@Service
public class UserRegisterServiceImp implements UserRegisterService {
	//发布服务
	
	//注入mapper
	@Autowired
	private TbUserMapper tbUserMapper;
	/**
	 *  请求的url：/user/check/{param}/{type}
		参数：从url中取参数1、String param（要校验的数据）2、Integer type（校验的数据类型）
		响应的数据：json数据。TaotaoResult，封装的数据校验的结果为true：表示成功，数据可用，false：失败，数据不可用。
	 * @param param	zhangsan是校验的数据
	 * @param type	1、2、3分别代表username、phone、email
	 * @return
	 */
	@Override
	public TaotaoResult checkData(String param, Integer type) {
		//创建example
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		//根据type动态设置条件
		//type	1、2、3分别代表username、phone、email
		if(type==1){
			//如果用户名为空,返回非法参数
			if(StringUtils.isBlank(param)){
				return TaotaoResult.build(400, "非法参数");
			}
			criteria.andUsernameEqualTo(param);
		}else if(type==2){
			criteria.andPhoneEqualTo(param);
		}else if(type==3){
			criteria.andEmailEqualTo(param);
		}else{
			return TaotaoResult.build(400, "非法参数");
		}
		//调用mapper查询
		List<TbUser> list = tbUserMapper.selectByExample(example);
		//查询出数据,证明已经存在数据,返回带false参数的TaotaoResult
		if(list!=null&&list.size()>0){
			return TaotaoResult.ok(false);
		}
		return TaotaoResult.ok(true);
	}
	/**
	 *  请求的url：/user/register
		参数：表单的数据：username、password、phone、email
		返回值：json数据。TaotaoResult
		接收参数：使用TbUser对象接收。
		请求的方法：post
		username 不能为空   并且 唯一

		password 不能为空  可以重复  （加密存储）
		
		phone    可以为空  不能重复 （如果不为空，就不能重复）
		
		email    可以为空   不能重复 （如果不为空，就不能重复）

	 * @param tbUser
	 * @return
	 */
	
	@Override
	public TaotaoResult register(TbUser tbUser) {
		//校验username和password看是否为空
		if(null==tbUser.getUsername()||null==tbUser.getPassword()){
			return TaotaoResult.build(400, "用户名或密码不能为空!");
		}
		//校验username,phone,email看是否重复
		//为false证明已经存在,不可用
		//校验用户名是否可用
		if(!(boolean)checkData(tbUser.getUsername(), 1).getData()){
			return TaotaoResult.build(400, "用户名已存在!");
		}
		//校验手机号是否可用
		if(StringUtils.isNotBlank(tbUser.getPhone())){
			if(!(boolean)checkData(tbUser.getPhone(), 2).getData()){
				return TaotaoResult.build(400, "手机号已使用!");
			}
		}
		//校验邮箱是否可用
		if(StringUtils.isNotBlank(tbUser.getEmail())){
			if(!(boolean)checkData(tbUser.getEmail(), 3).getData()){
				return TaotaoResult.build(400, "邮箱已使用!");
			}
		}
		//补全tbUser属性
		tbUser.setCreated(new Date());
		tbUser.setUpdated(tbUser.getCreated());
		//密码加密
		String p = DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes());
		tbUser.setPassword(p);
		//调用mapper插入数据
		tbUserMapper.insertSelective(tbUser);
		//返回成功
		return TaotaoResult.ok();
	}

}
