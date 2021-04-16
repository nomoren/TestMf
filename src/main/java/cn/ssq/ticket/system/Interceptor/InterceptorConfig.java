package cn.ssq.ticket.system.Interceptor;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import cn.stylefeng.guns.core.shiro.ShiroKit;

public class InterceptorConfig implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		try {
			ShiroKit.getSubject().getPrincipal().toString();
			return true;
		} catch (Exception e) {
			 PrintWriter writer = null;
		        response.setCharacterEncoding("UTF-8");
		        response.setContentType("text/html; charset=utf-8");
		        try {
		            writer = response.getWriter();
		            writer.print("{\"code\":\"-1\",\"msg\":\"token过期,刷新下页面！\"}");
		        } catch (Exception e1) {
		        	
		        } finally {
		            if (writer != null)
		                writer.close();
		        }
			
			return false;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}

	
}
