package ek.zhou.content.service.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import ek.zhou.common.pojo.EasyUIDataGridResult;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.common.util.JsonUtils;
import ek.zhou.content.jedis.JedisClient;
import ek.zhou.content.service.ContentService;
import ek.zhou.mapper.TbContentMapper;
import ek.zhou.pojo.TbContent;
import ek.zhou.pojo.TbContentExample;
import ek.zhou.pojo.TbItem;
import ek.zhou.pojo.TbItemExample;
@Service
public class ContentServiceImp implements ContentService {
	//注册服务
	
	//注入mapper
	@Autowired
	private TbContentMapper tbContentMapper;
	//注入jedis
	@Autowired
	private JedisClient jedisClient;
	//redis key中使用的关键字
	@Value("${CONTENT_KEY}")
	private String CONTENT_KEY;
	@Override
	public TaotaoResult addContent(TbContent tbContent) {
		//设置属性
		tbContent.setCreated(new Date());
		tbContent.setCreated(tbContent.getCreated());
		//调用mapper插入数据
		tbContentMapper.insert(tbContent);
		//缓存同步
		jedisClient.hdel(CONTENT_KEY, tbContent.getCategoryId().toString());
		return TaotaoResult.ok();
	}
	@Override
	public EasyUIDataGridResult listContent(long categoryId, Integer page, Integer rows) {
		//1.设置分页信息
		if(page==null||page<=0)
			page=1;
		if(rows==null||rows<=0)
			rows=30;
		PageHelper.startPage(page, rows);
		//2.创建example对象
		TbContentExample example = new TbContentExample();
		example.createCriteria().andCategoryIdEqualTo(categoryId);
		//3.根据Mapper调用查询所有数据方法
		List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);
		//4.获取分页信息
		PageInfo<TbContent> info = new PageInfo<>(list);
		//5.封装到EasyUIDataGridResult
		EasyUIDataGridResult dataGridResult = new EasyUIDataGridResult();
		dataGridResult.setRows(info.getList());
		dataGridResult.setTotal((int)info.getTotal());
		//6.返回
		
		//7.缓存同步
		jedisClient.hdel(CONTENT_KEY, categoryId+"");
		
		return dataGridResult;
		
				
	}
	@Override
	public TaotaoResult updateContent(TbContent tbContent) {
		//设置数据
		tbContent.setUpdated(new Date());
		//调用mapper更新数据
		tbContentMapper.updateByPrimaryKeySelective(tbContent);
		//缓存同步
		jedisClient.hdel(CONTENT_KEY, tbContent.getCategoryId().toString());
		
		//返回数据
		return TaotaoResult.ok();
	}
	@Override
	public TaotaoResult deleteContent(String ids) {
		//获取需要删除的id列表
		List<Long> idsList = new ArrayList<>();
		String[]idList=ids.split(",");
		for(String id:idList){
			idsList.add(Long.parseLong(id));
		}
		//根据id获取一个数据
		if(!idsList.isEmpty()){
			TbContent tbContent = tbContentMapper.selectByPrimaryKey(idsList.get(0));
			//根据该数据的categoryId同步缓存
			jedisClient.hdel(CONTENT_KEY, tbContent.getCategoryId().toString());
			
		}
		//设置example
		TbContentExample example = new TbContentExample();
		example.createCriteria().andIdIn(idsList);
		//调用mapper删除数据
		tbContentMapper.deleteByExample(example);
		//返回数据
		return TaotaoResult.ok();
	}
	@Override
	public List<TbContent> getContentListByCategoryId(long categoryId) {
		//使用redis缓存大广告数据
		//添加缓存不能影响原有功能
		try {
			//先从redis中取出json数据
			String json = jedisClient.hget(CONTENT_KEY, categoryId+"");
			//判断json是否为空
			if(StringUtils.isNoneBlank(json)){
				//将json转换成list对象
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				System.out.println("在缓存中取的数据");
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//创建example,设置条件
		TbContentExample contentExample = new TbContentExample();
		contentExample.createCriteria().andCategoryIdEqualTo(categoryId);
		//根据条件查询
		List<TbContent> list = tbContentMapper.selectByExample(contentExample);
		//向缓存中添加数据
		try {
			jedisClient.hset(CONTENT_KEY, categoryId+"", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//返回数据
		return list;
				
	}

}
