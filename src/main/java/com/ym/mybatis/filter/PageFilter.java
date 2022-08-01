package com.ym.mybatis.filter;

import com.ym.mybatis.util.PageUtil;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class PageFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        PageUtil.setPageNum(getPageNum(httpServletRequest));
        PageUtil.setPageSize(getPageSize(httpServletRequest));
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            PageUtil.removePageNum();
            PageUtil.removePageSize();
        }
    }

    private int getPageNum(HttpServletRequest request) {
        int pageNum = 1;
        try {
            String pageNums = request.getParameter("pageNum");
            if (pageNums != null && pageNums.length() > 0) {
                pageNum = Integer.parseInt(pageNums);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return pageNum;
    }

    private int getPageSize(HttpServletRequest request) {
        int pageSize = 1;
        try {
            String pageSizes = request.getParameter("pageSize");
            if (pageSizes != null && pageSizes.length() > 0) {
                pageSize = Integer.parseInt(pageSizes);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return pageSize;
    }

}
