package ek.zhou.search.test;

import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

/**
 *  第一步：把solrJ相关的jar包添加到工程中。
	第二步：创建一个SolrServer对象，需要使用CloudSolrServer子类。构造方法的参数是zookeeper的地址列表。
	第三步：需要设置DefaultCollection属性。
	第四步：创建一SolrInputDocument对象。
	第五步：向文档对象中添加域
	第六步：把文档对象写入索引库。
	第七步：提交。
 * @author Administrator
 *
 */
public class TestSolrCloud {
	@Test
	public void testSolrCloudAddDocument() throws Exception {
		//第一步:把solrj相关的jar包添加到工程中
		//第二部:创建一个solrserver对象,需要使用子类CloudSolrServer子类,构造方法的参数是zookeeper的地址列表
		//参数是zookeeper地址列表,用逗号隔开
		CloudSolrServer cloudSolrServer = new CloudSolrServer("192.168.25.128:2182,192.168.25.128:2183,192.168.25.128:2184");
		//第三步:设置defaultCollection属性
		cloudSolrServer.setDefaultCollection("collection2");
		//第四步:创建一个solrinputdocument对象
		SolrInputDocument document = new SolrInputDocument();
		//第五部:向文档对象中添加域
		document.addField("item_title", "测试商品");
		document.addField("item_price", "999");
		document.addField("id", "test001");
		//第六步:把文档对象写入索引库
		cloudSolrServer.add(document);
		//提交
		cloudSolrServer.commit();
		
		
	}
}
