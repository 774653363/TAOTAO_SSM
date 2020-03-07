package ek.zhou.search.test;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class Testsolrj {
	@Test
	public void add() throws SolrServerException, IOException{
		//创建solrserver,建立连接需要指定地址
		SolrServer server = new HttpSolrServer("http://192.168.25.128:8080/solr");
		//创建solrinputdocument
		SolrInputDocument document = new SolrInputDocument();
		
		//向文档中添加域
		document.addField("id", "test01");
		document.addField("item_title", "这是一个test");
		//将文档提交到索引库
		server.add(document);
		//提交
		server.commit();
	}
	@Test
	public void testquery() throws SolrServerException{
		//创建solrserver,建立连接需要指定地址
		SolrServer server = new HttpSolrServer("http://192.168.25.128:8080/solr");
		//创建solrquery
		SolrQuery query = new SolrQuery();
		//设置查询条件
		query.setQuery("阿尔卡特");
		query.addFilterQuery("item_price:[0 TO 3000000]");
		query.set("df", "item_title");
		//执行查询
		QueryResponse response = server.query(query);
		//获取结果集
		SolrDocumentList results = response.getResults();
		System.out.println("查询的总记录数:"+results.getNumFound());
		//遍历结果集
		for(SolrDocument document:results){
			System.out.println(document.get("id"));
			System.out.println(document.get("item_title"));
		}
				
			
		
		
	}
}
