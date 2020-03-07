package ek.zhou.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
/**
 * 搜索系统的全局异常处理器类
 * @author Administrator
 *
 */
public class GlobalExceptionReslover implements HandlerExceptionResolver {
	
	@Override
	public ModelAndView resolveException(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2,
			Exception ex) {
		//写日志文件
		System.out.println("搜索系统出现异常:"+ex.getMessage());
		//发邮件,发短信通知维护人员
		//展示错误页面
		ModelAndView mav = new ModelAndView();
		mav.setViewName("error/exception");
		mav.addObject("message", "系统发生异常,请稍后重试!");
		return mav;
	}

}
