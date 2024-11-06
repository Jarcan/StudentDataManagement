package org.psd.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 统一字符编码, 过滤除静态资源外的所有请求
 *
 * @author pengshidun
 */
@WebFilter(filterName = "encodingFilter", urlPatterns = "/*")
public class EncodingFilter implements Filter {

    /**
     * 初始化过滤器
     *
     * @param filterConfig 过滤器配置对象
     */
    @Override
    public void init(FilterConfig filterConfig) {
        // 进行初始化操作
    }

    /**
     * <p>检验request和response是否符合Http规范，不符合则抛出异常，
     * 1. 如果请求url和"^/static/.*"匹配则直接放行，
     * 2. 设置请求和响应字符编码为'UTF-8'，放行</p>
     *
     * @param servletRequest  请求
     * @param servletResponse 响应
     * @param filterChain     过滤器链
     * @throws IOException      doFilter和getWriter抛出IO异常
     * @throws ServletException 请求和响应不符合Http要求抛出异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 检查请求和响应是否符合Http规范
        if (!(servletRequest instanceof HttpServletRequest)) {
            throw new ServletException(servletRequest + " not HttpServletRequest");
        } else if (!(servletResponse instanceof HttpServletResponse)) {
            throw new ServletException(servletResponse + " not HttpServletResponse");
        }

        // 转换请求和响应为Http类型
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        // 获取请求的URI
        String uri = req.getRequestURI();

        // 定义静态资源的正则表达式
        String urlRegx = "^/static/.*";

        // 如果请求的URI不匹配静态资源正则表达式，则设置请求和响应的字符编码为UTF-8
        if (!uri.matches(urlRegx)) {
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");
        }

        // 继续过滤器链的执行
        filterChain.doFilter(req, resp);
    }

    /**
     * 销毁过滤器
     */
    @Override
    public void destroy() {
        // 进行销毁操作
    }
}
