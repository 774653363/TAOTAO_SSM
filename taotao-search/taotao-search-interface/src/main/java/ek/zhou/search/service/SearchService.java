package ek.zhou.search.service;

import ek.zhou.common.pojo.SearchResult;
import ek.zhou.common.pojo.TaotaoResult;

public interface SearchService {
	//导入所有商品到索引库中
	public TaotaoResult importAllSearchItems() throws Exception;
	//根据搜索的条件查询搜索结果
	/**
	 * 
	 * @param queryString	查询主条件
	 * @param page			查询页码
	 * @param rows			查询行数
	 * @return
	 * @throws Exception
	 */
	public SearchResult search(String queryString,Integer page,Integer rows)throws Exception;
	
	/**
	 * 根据id列表删除索引库数据
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public TaotaoResult deleteSearchItemById(long itemId)throws Exception;
	
	/**
	 * 根据id查询数据库中的商品信息并且更新索引库中的信息
	 * @return
	 */
	public TaotaoResult updateSearchItemById(long itemId)throws Exception;
}
