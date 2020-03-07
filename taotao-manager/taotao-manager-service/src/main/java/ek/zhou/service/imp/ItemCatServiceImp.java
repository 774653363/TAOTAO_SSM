package ek.zhou.service.imp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ek.zhou.common.pojo.EasyUITreeNode;
import ek.zhou.mapper.TbItemCatMapper;
import ek.zhou.pojo.TbItemCat;
import ek.zhou.pojo.TbItemCatExample;
import ek.zhou.pojo.TbItemCatExample.Criteria;
import ek.zhou.service.ItemCatService;
@Service
public class ItemCatServiceImp implements ItemCatService {
	//发布服务
	
	
	//注入mapper
	@Autowired
	TbItemCatMapper tbItemCatMapper;
	@Override
	public List<EasyUITreeNode> getItemCatList(Long parentId) {
		//1.根据parentId查询节点列表
		TbItemCatExample example = new TbItemCatExample();
		//2.设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbItemCat> list = tbItemCatMapper.selectByExample(example);
		//3.转换成EasyUITreeNode列表
		List<EasyUITreeNode> resultList = new ArrayList<>();
		for (TbItemCat itemCat:list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(itemCat.getId());
			node.setText(itemCat.getName());
			node.setState(itemCat.getIsParent()?"closed":"open");
			//添加到返回结果列表
			resultList.add(node);
		}
		//4.返回结果
		return resultList;
	}
	
	
	

}
