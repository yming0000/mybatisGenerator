package com.ym.mybatis.plugin;

import com.ym.mybatis.util.StringUtil;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.List;

public class ServiceAndControllerPlugin extends PluginAdapter {

    // 项目目录，一般为：src/main/java
    private String targetProject = "./src/main/java";

    // service包名，如：com.ym.service
    private String servicePackage = "com.ym.service";

    // service实现类包名，如：com.ym.service.impl
    private String serviceImplPackage = "com.ym.service.impl";

    // controller类包名，如：com.ym.controller
    private String controllerPackage = "com.ym.controller";

    // service接口的父接口
    private String superServiceInterface = "com.ym.mybatis.base.BaseService";

    // service实现类的父类
    private String superServiceImpl = "com.ym.mybatis.base.BaseServiceImpl";

    // controller类的父类
    private String superController = "com.ym.mybatis.base.BaseController";

    // model类的包名
    private String modelPackage = "com.ym.model";

    // example类的包名
    private String examplePackage = "com.ym.model";

    private String recordType;

    private String modelName;

    private FullyQualifiedJavaType model;

    private String serviceName;

    private String serviceImplName;

    private String controllerName;

    public boolean validate(List<String> list) {
        targetProject = StringUtility.stringHasValue(properties.getProperty("targetProject")) ? properties.getProperty("targetProject") : targetProject;
        servicePackage = StringUtility.stringHasValue(properties.getProperty("servicePackage")) ? properties.getProperty("servicePackage") : servicePackage;
        serviceImplPackage = StringUtility.stringHasValue(properties.getProperty("serviceImplPackage")) ? properties.getProperty("serviceImplPackage") : serviceImplPackage;
        controllerPackage = StringUtility.stringHasValue(properties.getProperty("serviceImplPackage")) ? properties.getProperty("serviceImplPackage") : controllerPackage;
        superServiceInterface = StringUtility.stringHasValue(properties.getProperty("superServiceInterface")) ? properties.getProperty("superServiceInterface") : superServiceInterface;
        superServiceImpl = StringUtility.stringHasValue(properties.getProperty("superServiceImpl")) ? properties.getProperty("superServiceImpl") : superServiceImpl;
        superController = StringUtility.stringHasValue(properties.getProperty("superController")) ? properties.getProperty("superController") : superController;
        modelPackage = StringUtility.stringHasValue(properties.getProperty("modelPackage")) ? properties.getProperty("modelPackage") : modelPackage;
        examplePackage = StringUtility.stringHasValue(properties.getProperty("examplePackage")) ? properties.getProperty("examplePackage") : examplePackage;
        return true;
    }

    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        recordType = introspectedTable.getBaseRecordType();
        modelName = recordType.substring(recordType.lastIndexOf(".") + 1);
        model = new FullyQualifiedJavaType(recordType);
        serviceName = servicePackage + "." + modelName + "Service";
        serviceImplName = serviceImplPackage + "." + modelName + "ServiceImpl";
        controllerName = controllerPackage + "." + modelName + "Controller";
        List<GeneratedJavaFile> file = new ArrayList<>();
        file.add(generateServiceInterface(introspectedTable));
        file.add(generateServiceImpl(introspectedTable));
        file.add(generateController(introspectedTable));
        return file;
    }

    private GeneratedJavaFile generateServiceInterface(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType service = new FullyQualifiedJavaType(serviceName);
        Interface serviceInterface = new Interface(service);
        serviceInterface.setVisibility(JavaVisibility.PUBLIC);
        if (StringUtility.stringHasValue(superServiceInterface)) {
            String superServiceInterfaceName = superServiceInterface.substring(superServiceInterface.lastIndexOf(".") + 1);
            serviceInterface.addImportedType(new FullyQualifiedJavaType(superServiceInterface));
            serviceInterface.addImportedType(new FullyQualifiedJavaType(recordType));
            serviceInterface.addSuperInterface(new FullyQualifiedJavaType(superServiceInterfaceName + "<" + modelName + ">"));
        }
        return new GeneratedJavaFile(serviceInterface, targetProject, context.getJavaFormatter());
    }

    private GeneratedJavaFile generateServiceImpl(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType service = new FullyQualifiedJavaType(serviceName);
        FullyQualifiedJavaType serviceImpl = new FullyQualifiedJavaType(serviceImplName);
        TopLevelClass clazz = new TopLevelClass(serviceImpl);
        clazz.setVisibility(JavaVisibility.PUBLIC);
        clazz.addImportedType(service);
        clazz.addSuperInterface(service);
        if (StringUtility.stringHasValue(superServiceImpl)) {
            String superServiceImplName = superServiceImpl.substring(superServiceImpl.lastIndexOf(".") + 1);
            clazz.addImportedType(new FullyQualifiedJavaType(superServiceImpl));
            clazz.addImportedType(new FullyQualifiedJavaType(recordType));
            clazz.setSuperClass(new FullyQualifiedJavaType(superServiceImplName + "<" + modelName + ">"));
        }
        clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
        clazz.addAnnotation("@Service");
        String daoFieldType = introspectedTable.getMyBatis3JavaMapperType();
        String daoFieldName = StringUtil.toLowerCase(daoFieldType.substring(daoFieldType.lastIndexOf(".") + 1));
        Field daoField = new Field(daoFieldName, new FullyQualifiedJavaType(daoFieldType));
        clazz.addImportedType(new FullyQualifiedJavaType(daoFieldType));
        clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
        daoField.addAnnotation("@Autowired");
        daoField.setVisibility(JavaVisibility.PRIVATE);
        clazz.addField(daoField);
        return new GeneratedJavaFile(clazz, targetProject, context.getJavaFormatter());
    }

    private GeneratedJavaFile generateController(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType controller = new FullyQualifiedJavaType(controllerName);
        TopLevelClass clazz = new TopLevelClass(controller);
        clazz.setVisibility(JavaVisibility.PUBLIC);
        clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RestController"));
        clazz.addAnnotation("@RestController");
        String serviceFieldName = StringUtil.toLowerCase(serviceName.substring(serviceName.lastIndexOf(".") + 1));
        Field serviceField = new Field(serviceFieldName, new FullyQualifiedJavaType(serviceName));
        clazz.addImportedType(new FullyQualifiedJavaType(serviceName));
        clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
        serviceField.addAnnotation("@Autowired");
        serviceField.setVisibility(JavaVisibility.PRIVATE);
        clazz.addField(serviceField);
        return new GeneratedJavaFile(clazz, targetProject, context.getJavaFormatter());
    }

}
