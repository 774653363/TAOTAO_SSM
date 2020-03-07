package ek.zhou.sso.service.imp;


import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.common.util.JsonUtils;
import ek.zhou.mapper.TbUserMapper;
import ek.zhou.pojo.TbUser;
import ek.zhou.pojo.TbUserExample;
import ek.zhou.pojo.TbUserExample.Criteria;
import ek.zhou.sso.jedis.JedisClient;
import ek.zhou.sso.service.UserLoginService;
/**
 * 用户登录实现类
 * @author Administrator
 *
 */
@Service
public class UserLoginServiceImp implements UserLoginService {
	//发布服务
	
	//注入mapper
	@Autowired
	private TbUserMapper tbUserMapper;
	
	//注入jedisClient
	@Autowired
	private JedisClient jedisClient;
	
	//获取user的session在redis中存放的key的前缀
	@Value("${USER_SESSION_INFO}")
	private String USER_SESSION_INFO;
	//获取user的session在redis中存放的key的过期时间
	@Value("${USER_SESSION_INFO_EXPIRE}")
	private Integer USER_SESSION_INFO_EXPIRE;
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
	@Override
	public TaotaoResult login(String username, String password) {
		//数据校验
		//校验用户名和密码是否为空
		if(StringUtils.isBlank(username)||StringUtils.isBlank(password)){
			return TaotaoResult.build(400, "用户名或密码不能为空");
		}
		//创建example
		TbUserExample example = new TbUserExample();
		//设置条件
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		criteria.andPasswordEqualTo(DigestUtils.md5DigestAsHex(password.getBytes()));
		//调用mapper查询
		List<TbUser> list = tbUserMapper.selectByExample(example);
		if(list==null||list.size()<=0){
			//登录失败
			return TaotaoResult.build(400, "用户名或密码错误,请重试!");
		}
		//登录成功
		//获取用户信息
		TbUser tbUser = list.get(0);
		
		//生成token,token使用UUID随机生成
		String token = UUID.randomUUID().toString();
		//将数据存入redis 
		//key:前缀+token
		String key = USER_SESSION_INFO+token;
		//value:tbUser转换成json
		//将tbUser的密码设置为空
		tbUser.setPassword(null);
		String value = JsonUtils.objectToJson(tbUser);
		//存入redis
		jedisClient.set(key, value);
		//设置超时时间
		jedisClient.expire(key, USER_SESSION_INFO_EXPIRE);
		//将token放入taotaoresult中返回
		return TaotaoResult.ok(token);
	}
	@Override
	public TaotaoResult getUserByToken(String token) {
		//redis中的key:前缀+token
		String key = USER_SESSION_INFO+token;
		//调用jedisClient查询数据
		String userJson = jedisClient.get(key);
		if(StringUtils.isBlank(userJson)){
			//查询不到user信息,即已经过期了
			return TaotaoResult.build(400, "用户信息已过期,请重新登录!");
		}
		//重置超时时间
		jedisClient.expire(key, USER_SESSION_INFO_EXPIRE);
		//将数据转成tbUser对象
		TbUser tbUser = JsonUtils.jsonToPojo(userJson, TbUser.class);
		return TaotaoResult.ok(tbUser);
	}
	/**
	 * 根据token删除redis中对应的user信息
	 * @param token
	 * @return
	 */
	
	@Override
	public TaotaoResult logout(String token) {
		//获取key
		String key = USER_SESSION_INFO+token;
		//删除redis中数据
		jedisClient.set(key, "");
		//返回结果
		return TaotaoResult.ok();
	}

}
