package ek.zhou.content.service;

import java.util.List;

import ek.zhou.common.pojo.EasyUITreeNode;
import ek.zhou.common.pojo.TaotaoResult;
public interface ContentCategoryService {
	/**
	 * 根据父节点查询父节点下的子节点列表
	 * @return
	 */
	public List<EasyUITreeNode> getContentCategoryList(long parentId);
	/**
	 * 新增节点
	 * @param parentId
	 * @param name
	 * @return
	 */
	public TaotaoResult createContentCategory(long parentId,String name);
	/**
	 * 重命名节点
	 * @param id
	 * @param name
	 * @return
	 */
	public TaotaoResult updateContentCategory(long id,String name);
	/**
	 * 删除节点
	 * @param id
	 * @return
	 */
	public TaotaoResult deleteContentCategory(long id);
}
