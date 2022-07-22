package com.ym.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class BaseEntityPlugin extends PluginAdapter {

    public boolean validate(List<String> list) {
        return true;
    }

    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (introspectedTable.hasPrimaryKeyColumns()) {
            List<IntrospectedColumn> list = introspectedTable.getPrimaryKeyColumns();
            if (list != null && list.size() > 1) {
                topLevelClass.addMethod(methodBaseEntityPreInsert());
                topLevelClass.addMethod(methodBaseEntityPreUpdate());
                return true;
            }
        }
        topLevelClass.addImportedType("com.ym.mybatis.base.BaseEntity");
        topLevelClass.setSuperClass("BaseEntity");
        topLevelClass.addMethod(methodBaseEntityPreInsert());
        topLevelClass.addMethod(methodBaseEntityPreUpdate());
        return true;
    }

    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("com.ym.mybatis.base.BaseEntity");
        topLevelClass.setSuperClass("BaseEntity");
        topLevelClass.addMethod(methodBaseEntityPreInsert());
        topLevelClass.addMethod(methodBaseEntityPreUpdate());
        return true;
    }

    private Method methodBaseEntityPreInsert() {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("preInsert");
        method.addAnnotation("@Override");
        method.addBodyLine("");
        return method;
    }

    private Method methodBaseEntityPreUpdate() {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("preUpdate");
        method.addAnnotation("@Override");
        method.addBodyLine("");
        return method;
    }

}
