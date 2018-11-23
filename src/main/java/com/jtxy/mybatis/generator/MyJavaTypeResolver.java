package com.jtxy.mybatis.generator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

public class MyJavaTypeResolver extends JavaTypeResolverDefaultImpl {

	@Override
	protected FullyQualifiedJavaType calculateBigDecimalReplacement(
			IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
		FullyQualifiedJavaType answer;
		if (column.getScale() > 0 || column.getLength() > 18
				|| forceBigDecimals) {
			answer = defaultType;
		} else if (column.getLength() > 9) {
			answer = new FullyQualifiedJavaType(Long.class.getName());
		} else {
			answer = new FullyQualifiedJavaType(Integer.class.getName());
		}
		return answer;
	}

}
