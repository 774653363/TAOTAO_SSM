package ek.zhou.search.service.imp;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ek.zhou.common.pojo.SearchItem;
import ek.zhou.common.pojo.SearchResult;
import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.search.dao.SearchDao;
import ek.zhou.search.mapper.SearchItemMapper;
import ek.zhou.search.service.SearchService;
@Service
public class SearchServiceImp implements SearchService {
	//发布服务
	
	//注入mapper
	@Autowired
	private SearchDao searchDao;
	@Override
	public TaotaoResult importAllSearchItems() throws Exception {
		return searchDao.importAllSearchItems();
	}
	@Override
	public SearchResult search(String queryString, Integer page, Integer rows) throws Exception {
		
		//创建solrquery对象
		SolrQuery query = new SolrQuery();
		//设置主条件
		if(StringUtils.isNotBlank(queryString)){
			query.setQuery(queryString);
		}else{
			query.setQuery("*:*");
		}
		//设置过滤条件
		//设置分页
		if(page==null)
			page=1;
		if(rows==null)
			rows=60;
		query.setStart(rows*(page-1));
		query.setRows(rows);
		//设置默认搜索域
		query.set("df", "item_keywords");
		//设置高亮
		query.setHighlight(true);
		query.addHighlightField("item_title");//设置高亮显示的域
		query.setHighlightSimplePre("<em style=\"color:red\">");
		query.setHighlightSimplePost("</em>");
		//调用dao查询
		SearchResult result = searchDao.search(query);
		//返回对象,设置searchResult的总页数
		long pageCount = 0l;
		if(result.getPageCount()%rows==0){
			pageCount = result.getPageCount()/rows;
		}else{
			pageCount = result.getPageCount()/rows+1;
		}
		result.setPageCount(pageCount);
		return result;
	}
	@Override
	public TaotaoResult deleteSearchItemById(long itemId) throws Exception {
		return searchDao.deleteSearchItemById(itemId);
	}
	@Override
	public TaotaoResult updateSearchItemById(long itemId) throws Exception {
		// TODO Auto-generated method stub
		return searchDao.updateSearchItemById(itemId);
	}

}
