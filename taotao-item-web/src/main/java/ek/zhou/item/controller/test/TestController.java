package ek.zhou.item.controller.test;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Controller
public class TestController {
	//从spring容器中获得FreeMarkerConfigurer对象
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	@RequestMapping("/genhtml")
	@ResponseBody
	public String genHtml()throws Exception{
		//获取Configuration对象
		Configuration configuration = freeMarkerConfigurer.createConfiguration();
		//获取模板对象
		Template template = configuration.getTemplate("hello.ftl");
		//模板数据
		Map model = new HashMap();
		model.put("hello", "hello world");
		//创建流对象
		Writer writer = new FileWriter(new File("G:/freemark/hello.html"));
		
		//输出
		template.process(model, writer);
		//关闭流对象
		writer.close();
		return "ok";
	}
}
