package ek.zhou.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * 用于跳转页面的Controller
 * @author Administrator
 *
 */
@Controller
public class PageController {
	@RequestMapping("/page/{page}")
	public String pagr(@PathVariable String page,String redirect,Model model){
		System.out.println("redirect:"+redirect);
		model.addAttribute("redirect", redirect);
		return page;
	}
}
