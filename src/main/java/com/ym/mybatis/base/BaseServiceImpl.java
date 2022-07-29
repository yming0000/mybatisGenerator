package com.ym.mybatis.base;

import com.ym.mybatis.util.ApplicationContextProvider;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public class BaseServiceImpl<T> implements BaseService<T> {

    private Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    private Object mapperObj = null;

    private Object getMapperObj() {
        if (mapperObj == null) {
            mapperObj = ApplicationContextProvider.getBean(entityClass.getSimpleName() + "Mapper");
        }
        return mapperObj;
    }

    public T selectByPrimaryKey(Integer id) {
        try {
            Method method = getMapperObj().getClass().getMethod("selectByPrimaryKey", Integer.class);
            return (T) method.invoke(mapperObj, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int deleteByPrimaryKey(Integer id) {
        try {
            Method method = getMapperObj().getClass().getMethod("deleteByPrimaryKey", Integer.class);
            return (int) method.invoke(mapperObj, id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int insert(T record) {
        try {
            Method method = getMapperObj().getClass().getMethod("insert", entityClass);
            return (int) method.invoke(mapperObj, record);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int insertSelective(T record) {
        try {
            Method method = getMapperObj().getClass().getMethod("insertSelective", entityClass);
            return (int) method.invoke(mapperObj, record);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int updateByPrimaryKey(T record) {
        try {
            Method method = getMapperObj().getClass().getMethod("updateByPrimaryKey", entityClass);
            return (int) method.invoke(mapperObj, record);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int updateByPrimaryKeySelective(T record) {
        try {
            Method method = getMapperObj().getClass().getMethod("updateByPrimaryKeySelective", entityClass);
            return (int) method.invoke(mapperObj, record);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
