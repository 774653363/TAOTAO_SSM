package ek.zhou.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ek.zhou.common.util.JsonUtils;
import ek.zhou.manager.web.util.FastDFSClient;

@Controller
public class PictureController {
	
	@Value("${TAOTAO_IMAGE_SERVER_URL}")
	private String TAOTAO_IMAGE_SERVER_URL;
	@Value("${CONFIG_URL}")
	private String CONFIG_URL;
	
	
	/**
	 * 上传图片
	 * 
	 * Content-Type:application/json;charset=UTF-8  responseBody默认的返回类型
	 * 可以通过produces 设置返回类型
	返回JSON格式字符串时：
	Content-Type:text/plain;charset=UTF-8  火狐浏览器也支持

	 * @param uploadFile
	 * @return
	 */
	@RequestMapping(value="/pic/upload",produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
	@ResponseBody
	public String fileUpload(MultipartFile uploadFile){
		try{
			//1.取文件扩展名
			String originalFileName = uploadFile.getOriginalFilename();
			String extName = originalFileName.substring(originalFileName.lastIndexOf(".")+1);
			//2.创建一个FastDFS的客户端
			FastDFSClient client = new FastDFSClient(CONFIG_URL);
			//3.执行上传处理
			String path = client.uploadFile(uploadFile.getBytes(),extName);
			//4.拼接返回的url和ip地址,形成完整的url
			String url = TAOTAO_IMAGE_SERVER_URL+path;
			//5.返回map
			Map result = new HashMap<>();
			result.put("error", 0);
			result.put("url", url);
			return JsonUtils.objectToJson(result);
		}catch(Exception e){
			e.printStackTrace();
			//5.返回map
			Map result = new HashMap<>();
			result.put("error", 1);
			result.put("message", "图片上传失败");
			return JsonUtils.objectToJson(result);
		}
	}
}
