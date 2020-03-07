package ek.zhou.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.SourceLocator;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ek.zhou.common.pojo.SearchItem;
import ek.zhou.common.pojo.SearchResult;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.search.mapper.SearchItemMapper;

/**对索引库操作的dao
 * 从索引库中搜索商品的dao
 * @author Administrator
 *
 */
@Repository
public class SearchDao {
	
	//注入solrServer
	@Autowired
	private SolrServer server;
	//注入mapper
	@Autowired
	private SearchItemMapper searchItemMapper;
	
	/**
	 * 根据查询条件查询商品结果集
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public SearchResult search(SolrQuery query)throws Exception{
		//创建返回对象
		SearchResult searchResult = new SearchResult();
		//创建SearchItem的list集合
		List<SearchItem> itemList = new ArrayList<>();
		//执行查询
		QueryResponse response = server.query(query);
		//获取结果集
		SolrDocumentList documentList = response.getResults();
		//取高亮
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		//遍历结果集
		for(SolrDocument document:documentList){
			 //将solrdocument中的属性设置到searchItem中
			SearchItem item = new SearchItem();
			item.setId( Long.parseLong(document.get("id").toString()));
			item.setCategory_name((String) document.get("item_category_name"));
			item.setImage((String) document.get("item_image"));
			item.setPrice((long) document.get("item_price"));
			item.setSell_point((String) document.get("item_sell_point"));
			//取高亮
			List<String> list = highlighting.get(document.get("id")).get("item_title");
			//判断是否为空
			String highLightStr="";
			if(list!=null&&list.size()>0){
				//有高亮
				highLightStr=list.get(0);
			}else{
				//没有高亮
				highLightStr=document.get("item_title").toString();
			}
			item.setTitle(highLightStr);
			//将searchItem封装的ItemList中
			itemList.add(item);
		}
		//设置searchResult属性
		searchResult.setPageCount(documentList.getNumFound());
		searchResult.setItemList(itemList);
		return searchResult;
	}
	/**
	 * 将数据库中商品的数据导入索引库
	 * @return
	 * @throws Exception
	 */
	public TaotaoResult importAllSearchItems() throws Exception{
		//调用mapper方法,查询所有商品数据
				List<SearchItem> searchItemList = searchItemMapper.getSearchItemList();
				//通过solrj将数据写入到索引库中
				//注入solrserver
				//创建solrinputdocument 将列表中的元素一个个放入索引库中
				for(SearchItem searchItem:searchItemList){
					SolrInputDocument document = new SolrInputDocument();
					// 4、为文档添加域
					document.addField("id", searchItem.getId());
					document.addField("item_title", searchItem.getTitle());
					document.addField("item_sell_point", searchItem.getSell_point());
					document.addField("item_price", searchItem.getPrice());
					document.addField("item_image", searchItem.getImage());
					document.addField("item_category_name", searchItem.getCategory_name());
					document.addField("item_desc", searchItem.getItem_desc());
					// 5、向索引库中添加文档。
					server.add(document);
				}
				//提交
				server.commit();
				//返回结果
				return TaotaoResult.ok();
	}
	
	/**
	 * 根据id查询数据库中的商品信息并且更新索引库中的信息
	 * @return
	 */
	public TaotaoResult updateSearchItemById(long itemId)throws Exception{
		//调用mapper查询数据库中的商品信息
		SearchItem searchItem = searchItemMapper.getSearchItemById(itemId);
		SolrInputDocument document = new SolrInputDocument();
		// 4、为文档添加域
		document.addField("id", searchItem.getId());
		document.addField("item_title", searchItem.getTitle());
		document.addField("item_sell_point", searchItem.getSell_point());
		document.addField("item_price", searchItem.getPrice());
		document.addField("item_image", searchItem.getImage());
		document.addField("item_category_name", searchItem.getCategory_name());
		document.addField("item_desc", searchItem.getItem_desc());
		// 5、向索引库中添加文档。
		server.add(document);
		//提交
		server.commit();
		//返回结果
		return TaotaoResult.ok();
	}
	/**
	 * 根据id列表删除索引库数据
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public TaotaoResult deleteSearchItemById(long itemId)throws Exception{
		//调用solrserver根据id删除索引库数据
		server.deleteById(String.valueOf(itemId));
		//提交
		server.commit();
		//返回结果
		return TaotaoResult.ok();
	}
	
}
