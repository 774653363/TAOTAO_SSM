package ek.zhou.search.mapper;

import java.util.List;

import ek.zhou.common.pojo.SearchItem;

/**
 * 对数据库操作的mapper
 * 定义mapper,关联查询3张表 查询出搜索时的数据
 * @author Administrator
 *
 */
public interface SearchItemMapper {
	/**
	 * 查询所有商品的数据
	 * @return
	 */
public List<SearchItem> getSearchItemList();
/**
 * 根据商品id查询数据库中的信息
 * @param itemId
 * @return
 */
public SearchItem getSearchItemById(Long itemId);
}
