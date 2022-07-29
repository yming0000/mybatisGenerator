package com.ym.mybatis.base;

public interface BaseService<T> {

    T selectByPrimaryKey(Integer id);

    int deleteByPrimaryKey(Integer id);

    int insert(T record);

    int insertSelective(T record);

    int updateByPrimaryKey(T record);

    int updateByPrimaryKeySelective(T record);

}
