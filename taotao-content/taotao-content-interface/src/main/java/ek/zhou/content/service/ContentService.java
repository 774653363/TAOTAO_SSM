package ek.zhou.content.service;

import java.util.List;

import ek.zhou.common.pojo.EasyUIDataGridResult;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.pojo.TbContent;

/**
 * 内容表service接口
 * @author Administrator
 *
 */
public interface ContentService {
	/**
	 * 添加内容方法
	 * @param tbContent
	 * @return
	 */
	public TaotaoResult addContent(TbContent tbContent);
	/**
	 * 分页查询数据
	 * @param categoryId
	 * @param page
	 * @param rows
	 * @return
	 */
	public EasyUIDataGridResult listContent(long categoryId,Integer page, Integer rows);
	/**
	 * 更新数据
	 * @param tbContent
	 * @return
	 */
	public TaotaoResult updateContent(TbContent tbContent);
	/**
	 * 根据id删除数据
	 * @param ids
	 * @return
	 */
	public TaotaoResult deleteContent(String ids);
	/**
	 * 根据categoryId返回content列表
	 * 
	 * @param categoryId
	 * @return
	 */
	public List<TbContent> getContentListByCategoryId(long categoryId);
}
