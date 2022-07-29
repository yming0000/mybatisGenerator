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
    private final String targetProject = "./src/main/java";

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

    private String recordType;

    private String modelPackage;

    private String modelName;

    private String exampleType;

    private String examplePackage;

    private String exampleName;

    private String serviceName;

    private String serviceImplName;

    private String controllerName;

    public boolean validate(List<String> list) {
        servicePackage = StringUtility.stringHasValue(properties.getProperty("servicePackage")) ? properties.getProperty("servicePackage") : servicePackage;
        serviceImplPackage = StringUtility.stringHasValue(properties.getProperty("serviceImplPackage")) ? properties.getProperty("serviceImplPackage") : serviceImplPackage;
        controllerPackage = StringUtility.stringHasValue(properties.getProperty("controllerPackage")) ? properties.getProperty("controllerPackage") : controllerPackage;
        superServiceInterface = properties.getProperty("superServiceInterface") != null ? properties.getProperty("superServiceInterface") : superServiceInterface;
        superServiceImpl = properties.getProperty("superServiceImpl") != null ? properties.getProperty("superServiceImpl") : superServiceImpl;
        superController = properties.getProperty("superController") != null ? properties.getProperty("superController") : superController;
        return true;
    }

    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        recordType = introspectedTable.getBaseRecordType();
        modelPackage = recordType.substring(0, recordType.lastIndexOf("."));
        modelName = recordType.substring(recordType.lastIndexOf(".") + 1);
        exampleType = introspectedTable.getExampleType();
        examplePackage = exampleType.substring(0, exampleType.lastIndexOf("."));
        exampleName = exampleType.substring(exampleType.lastIndexOf(".") + 1);
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
        if (StringUtility.stringHasValue(superController)) {
            clazz.addImportedType(new FullyQualifiedJavaType(superController));
            clazz.setSuperClass(new FullyQualifiedJavaType(superController));
        }
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
