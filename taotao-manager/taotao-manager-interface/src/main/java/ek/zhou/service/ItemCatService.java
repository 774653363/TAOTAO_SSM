package ek.zhou.service;

import java.util.List;

import ek.zhou.common.pojo.EasyUITreeNode;

/**
 * 商品类目服务接口
 * @author Administrator
 *
 */
public interface ItemCatService {
	public List<EasyUITreeNode> getItemCatList(Long parentId);
}
