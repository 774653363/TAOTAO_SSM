package ek.zhou.content.service.imp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ek.zhou.common.pojo.EasyUITreeNode;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.content.service.ContentCategoryService;
import ek.zhou.mapper.TbContentCategoryMapper;
import ek.zhou.mapper.TbContentMapper;
import ek.zhou.pojo.TbContentCategory;
import ek.zhou.pojo.TbContentCategoryExample;
import ek.zhou.pojo.TbContentExample;
@Service
public class ContentCategoryServiceImp implements ContentCategoryService {
	//发布服务
	
	//注入mapper
	@Autowired
	private TbContentCategoryMapper tbContentCategoryMapper;
	@Autowired
	private TbContentMapper tbContentMapper;
	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		//创建example
		TbContentCategoryExample example = new TbContentCategoryExample();
		//设置条件
		example.createCriteria().andParentIdEqualTo(parentId);
		//查询列表
		List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
		//存入List中
		List<EasyUITreeNode> nodes = new ArrayList<>();
		for(TbContentCategory l:list){
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(l.getId());
			node.setText(l.getName());
			node.setState(l.getIsParent()?"closed":"open");
			//添加到列表
			nodes.add(node);
		}
		//返回数据
		return nodes;
	}
	@Override
	public TaotaoResult createContentCategory(long parentId, String name) {
		//1.创建对象,并设置属性
		TbContentCategory tbContentCategory = new TbContentCategory();
		tbContentCategory.setCreated(new Date());
		tbContentCategory.setIsParent(false);
		tbContentCategory.setName(name);
		tbContentCategory.setParentId(parentId);
		//排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
		tbContentCategory.setSortOrder(1);
		//状态。可选值:1(正常),2(删除)
		tbContentCategory.setStatus(1);
		tbContentCategory.setUpdated(tbContentCategory.getCreated());
		//2.调用mapper插入数据并获取返回的id	需要主键返回
		tbContentCategoryMapper.insert(tbContentCategory);
		//3.判断父节点是否是叶子节点,如果是则改为非叶子节点
		TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()){
			parent.setIsParent(true);
			tbContentCategoryMapper.updateByPrimaryKey(parent);
		}
		//4.返回数据
		return TaotaoResult.ok(tbContentCategory);
	}
	@Override
	public TaotaoResult updateContentCategory(long id, String name) {
		//根据id获取数据
		TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(id);
		//更新数据内容
		tbContentCategory.setName(name);
		tbContentCategory.setUpdated(new Date());
		//调用mapper更新
		tbContentCategoryMapper.updateByPrimaryKey(tbContentCategory);
		return TaotaoResult.ok();
	}
	@Override
	public TaotaoResult deleteContentCategory(long id) {
		//根据id获取数据
		TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(id);
		if(tbContentCategory.getIsParent()){
			TaotaoResult result = new TaotaoResult();
			result.setStatus(404);
			return result;
		}
		//获取父节点id
		long parentId = tbContentCategory.getParentId();
		//根据父节点id查询父节点下的子节点
		TbContentCategoryExample example = new TbContentCategoryExample();
		example.createCriteria().andParentIdEqualTo(parentId);
		List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
		//如果只有一个子节点，将父节点设置为叶子节点
		if(list.size()==1){
			//根据id获取父节点数据
			TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(parentId);
			//设置父节点
			parent.setIsParent(false);
			//更新父节点
			tbContentCategoryMapper.updateByPrimaryKey(parent);
		}
		//删除该节点对应的内容,设置条件
		TbContentExample contentExample = new TbContentExample();
		contentExample.createCriteria().andCategoryIdEqualTo(id);
		//调用mapper删除对应内容
		tbContentMapper.deleteByExample(contentExample);
		//调用mapper删除节点
		tbContentCategoryMapper.deleteByPrimaryKey(id);
		return TaotaoResult.ok();
	}

}
