package com.ym.mybatis.util;

public class PageUtil {

    private static ThreadLocal<Integer> pageNum = new ThreadLocal<Integer>();

    private static ThreadLocal<Integer> pageSize = new ThreadLocal<Integer>();

    public static int getPageNum() {
        Integer pn = pageNum.get();
        if (pn == null)
            return 1;
        return pn;
    }

    public static void setPageNum(int pn) {
        pageNum.set(pn);
    }

    public static void removePageNum() {
        pageNum.remove();
    }

    public static int getPageSize() {
        Integer ps = pageSize.get();
        if (ps == null)
            return 1;
        return ps;
    }

    public static void setPageSize(int ps) {
        pageNum.set(ps);
    }

    public static void removePageSize() {
        pageNum.remove();
    }

}
