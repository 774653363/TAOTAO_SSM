package ek.zhou.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 显示页面
 * @author Administrator
 *
 */
@Controller
public class PageController {
@RequestMapping("/")
public String showIndex(){
	return "index";
}

@RequestMapping("/{page}")
public String showPage(@PathVariable String page){
	return page;
}
}
