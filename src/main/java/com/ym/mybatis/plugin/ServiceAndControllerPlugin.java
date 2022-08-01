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

    // 分页查询工具类
    private String pageUtilClass = "com.ym.mybatis.util.PageUtil";

    public boolean validate(List<String> list) {
        servicePackage = StringUtility.stringHasValue(properties.getProperty("servicePackage")) ? properties.getProperty("servicePackage") : servicePackage;
        serviceImplPackage = StringUtility.stringHasValue(properties.getProperty("serviceImplPackage")) ? properties.getProperty("serviceImplPackage") : serviceImplPackage;
        controllerPackage = StringUtility.stringHasValue(properties.getProperty("controllerPackage")) ? properties.getProperty("controllerPackage") : controllerPackage;
        superServiceInterface = properties.getProperty("superServiceInterface") != null ? properties.getProperty("superServiceInterface") : superServiceInterface;
        superServiceImpl = properties.getProperty("superServiceImpl") != null ? properties.getProperty("superServiceImpl") : superServiceImpl;
        superController = properties.getProperty("superController") != null ? properties.getProperty("superController") : superController;
        pageUtilClass = properties.getProperty("pageUtilClass") != null ? properties.getProperty("pageUtilClass") : pageUtilClass;
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
        serviceInterface.addImportedType(new FullyQualifiedJavaType(recordType));
        if (StringUtility.stringHasValue(superServiceInterface)) {
            String superServiceInterfaceName = superServiceInterface.substring(superServiceInterface.lastIndexOf(".") + 1);
            serviceInterface.addImportedType(new FullyQualifiedJavaType(superServiceInterface));
            serviceInterface.addSuperInterface(new FullyQualifiedJavaType(superServiceInterfaceName + "<" + modelName + ">"));
        }
        // import
        serviceInterface.addImportedType(new FullyQualifiedJavaType("java.util.List"));
        serviceInterface.addImportedType(new FullyQualifiedJavaType(exampleType));
        // Method selectAll
        Method selectAll = new Method("selectAll");
        selectAll.setReturnType(new FullyQualifiedJavaType("List<" + modelName + ">"));
        selectAll.addParameter(new Parameter(new FullyQualifiedJavaType(exampleName), "example"));
        serviceInterface.addMethod(selectAll);
        // Method selectByPages
        Method selectByPages = new Method("selectByPages");
        selectByPages.setReturnType(new FullyQualifiedJavaType("List<" + modelName + ">"));
        selectByPages.addParameter(new Parameter(new FullyQualifiedJavaType(exampleName), "example"));
        serviceInterface.addMethod(selectByPages);
        // Method deleteByIDs
        Method deleteByIDs = new Method("deleteByIDs");
        deleteByIDs.setReturnType(new FullyQualifiedJavaType("int"));
        deleteByIDs.addParameter(new Parameter(new FullyQualifiedJavaType("Integer[]"), "ids"));
        serviceInterface.addMethod(deleteByIDs);
        return new GeneratedJavaFile(serviceInterface, targetProject, context.getJavaFormatter());
    }

    private GeneratedJavaFile generateServiceImpl(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType service = new FullyQualifiedJavaType(serviceName);
        FullyQualifiedJavaType serviceImpl = new FullyQualifiedJavaType(serviceImplName);
        TopLevelClass clazz = new TopLevelClass(serviceImpl);
        clazz.setVisibility(JavaVisibility.PUBLIC);
        clazz.addImportedType(new FullyQualifiedJavaType(recordType));
        clazz.addImportedType(service);
        clazz.addSuperInterface(service);
        if (StringUtility.stringHasValue(superServiceImpl)) {
            String superServiceImplName = superServiceImpl.substring(superServiceImpl.lastIndexOf(".") + 1);
            clazz.addImportedType(new FullyQualifiedJavaType(superServiceImpl));
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
        // import
        clazz.addImportedType(new FullyQualifiedJavaType("java.util.List"));
        clazz.addImportedType(new FullyQualifiedJavaType("java.util.Arrays"));
        clazz.addImportedType(new FullyQualifiedJavaType(exampleType));
        clazz.addImportedType(new FullyQualifiedJavaType("com.github.pagehelper.PageHelper"));
        clazz.addImportedType(new FullyQualifiedJavaType(pageUtilClass));
        // Method selectAll
        Method selectAll = new Method("selectAll");
        selectAll.addAnnotation("@Override");
        selectAll.setVisibility(JavaVisibility.PUBLIC);
        selectAll.setReturnType(new FullyQualifiedJavaType("List<" + modelName + ">"));
        selectAll.addParameter(new Parameter(new FullyQualifiedJavaType(exampleName), "example"));
        selectAll.addBodyLine("return " + daoFieldName + ".selectByExample(example);");
        clazz.addMethod(selectAll);
        // Method selectByPages
        Method selectByPages = new Method("selectByPages");
        selectByPages.addAnnotation("@Override");
        selectByPages.setVisibility(JavaVisibility.PUBLIC);
        selectByPages.setReturnType(new FullyQualifiedJavaType("List<" + modelName + ">"));
        selectByPages.addParameter(new Parameter(new FullyQualifiedJavaType(exampleName), "example"));
        selectByPages.addBodyLine("PageHelper.startPage(PageUtil.getPageNum(), PageUtil.getPageSize());");
        selectByPages.addBodyLine("return selectAll(example);");
        clazz.addMethod(selectByPages);
        // Method deleteByIDs
        Method deleteByIDs = new Method("deleteByIDs");
        deleteByIDs.addAnnotation("@Override");
        deleteByIDs.setVisibility(JavaVisibility.PUBLIC);
        deleteByIDs.setReturnType(new FullyQualifiedJavaType("int"));
        deleteByIDs.addParameter(new Parameter(new FullyQualifiedJavaType("Integer[]"), "ids"));
        deleteByIDs.addBodyLine(exampleName + " example = new " + exampleName + "();");
        deleteByIDs.addBodyLine("example.createCriteria().andIdIn(Arrays.asList(ids));");
        deleteByIDs.addBodyLine("return " + daoFieldName + ".deleteByExample(example);");
        clazz.addMethod(deleteByIDs);
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
