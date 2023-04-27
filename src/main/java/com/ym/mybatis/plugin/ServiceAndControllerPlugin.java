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
    private String messageClass = "com.ym.mybatis.common.Message";

    public boolean validate(List<String> list) {
        servicePackage = StringUtility.stringHasValue(properties.getProperty("servicePackage")) ? properties.getProperty("servicePackage") : servicePackage;
        serviceImplPackage = StringUtility.stringHasValue(properties.getProperty("serviceImplPackage")) ? properties.getProperty("serviceImplPackage") : serviceImplPackage;
        controllerPackage = StringUtility.stringHasValue(properties.getProperty("controllerPackage")) ? properties.getProperty("controllerPackage") : controllerPackage;
        superServiceInterface = properties.getProperty("superServiceInterface") != null ? properties.getProperty("superServiceInterface") : superServiceInterface;
        superServiceImpl = properties.getProperty("superServiceImpl") != null ? properties.getProperty("superServiceImpl") : superServiceImpl;
        superController = properties.getProperty("superController") != null ? properties.getProperty("superController") : superController;
        messageClass = properties.getProperty("messageClass") != null ? properties.getProperty("messageClass") : messageClass;
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
        selectByPages.addParameter(new Parameter(new FullyQualifiedJavaType("int"), "pageNum"));
        selectByPages.addParameter(new Parameter(new FullyQualifiedJavaType("int"), "pageSize"));
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
        String daoFieldName = StringUtil.toLowerCaseFirst(daoFieldType.substring(daoFieldType.lastIndexOf(".") + 1));
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
        clazz.addImportedType(new FullyQualifiedJavaType("com.github.pagehelper.PageInfo"));
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
        selectByPages.addParameter(new Parameter(new FullyQualifiedJavaType("int"), "pageNum"));
        selectByPages.addParameter(new Parameter(new FullyQualifiedJavaType("int"), "pageSize"));
        selectByPages.addBodyLine("PageHelper.startPage(pageNum, pageSize);");
        selectByPages.addBodyLine("List<" + modelName + "> " + StringUtil.toLowerCaseFirst(modelName) + "List = selectAll(example);");
        selectByPages.addBodyLine("PageInfo<" + modelName + "> pageInfo = new PageInfo<>(" + StringUtil.toLowerCaseFirst(modelName) + "List);");
        selectByPages.addBodyLine("return pageInfo.getList();");
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
        clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RequestMapping"));
        clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RestController"));
        clazz.addAnnotation("@RestController");
        clazz.addAnnotation("@RequestMapping(\"" + StringUtil.toLowerCaseFirst(modelName) + "\")");
        if (StringUtility.stringHasValue(superController)) {
            clazz.addImportedType(new FullyQualifiedJavaType(superController));
            clazz.setSuperClass(new FullyQualifiedJavaType(superController));
        }
        String serviceFieldName = StringUtil.toLowerCaseFirst(serviceName.substring(serviceName.lastIndexOf(".") + 1));
        Field serviceField = new Field(serviceFieldName, new FullyQualifiedJavaType(serviceName));
        clazz.addImportedType(new FullyQualifiedJavaType(serviceName));
        clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
        serviceField.addAnnotation("@Autowired");
        serviceField.setVisibility(JavaVisibility.PRIVATE);
        clazz.addField(serviceField);
        // import
        clazz.addImportedType(new FullyQualifiedJavaType(recordType));
        clazz.addImportedType(new FullyQualifiedJavaType(exampleType));
        clazz.addImportedType(new FullyQualifiedJavaType(messageClass));
        clazz.addImportedType(new FullyQualifiedJavaType("java.util.List"));
        String messageFieldName = messageClass.substring(messageClass.lastIndexOf(".") + 1);
        // Method selectByPrimaryKey
        Method selectByPrimaryKey = new Method("selectByPrimaryKey");
        selectByPrimaryKey.addAnnotation("@RequestMapping(\"selectByPrimaryKey\")");
        selectByPrimaryKey.setVisibility(JavaVisibility.PUBLIC);
        selectByPrimaryKey.setReturnType(new FullyQualifiedJavaType(messageFieldName));
        selectByPrimaryKey.addParameter(new Parameter(new FullyQualifiedJavaType("Integer"), "id"));
        selectByPrimaryKey.addBodyLine(modelName + " " + StringUtil.toLowerCaseFirst(modelName) + " = " + serviceFieldName + ".selectByPrimaryKey(id);");
        selectByPrimaryKey.addBodyLine("return Message.successMessage(" + StringUtil.toLowerCaseFirst(modelName) + ");");
        clazz.addMethod(selectByPrimaryKey);
        // Method deleteByPrimaryKey
        Method deleteByPrimaryKey = new Method("deleteByPrimaryKey");
        deleteByPrimaryKey.addAnnotation("@RequestMapping(\"deleteByPrimaryKey\")");
        deleteByPrimaryKey.setVisibility(JavaVisibility.PUBLIC);
        deleteByPrimaryKey.setReturnType(new FullyQualifiedJavaType(messageFieldName));
        deleteByPrimaryKey.addParameter(new Parameter(new FullyQualifiedJavaType("Integer"), "id"));
        deleteByPrimaryKey.addBodyLine(serviceFieldName + ".deleteByPrimaryKey(id);");
        deleteByPrimaryKey.addBodyLine("return Message.successMessage();");
        clazz.addMethod(deleteByPrimaryKey);
        // Method insert
        Method insert = new Method("insert");
        insert.addAnnotation("@RequestMapping(\"insert\")");
        insert.setVisibility(JavaVisibility.PUBLIC);
        insert.setReturnType(new FullyQualifiedJavaType(messageFieldName));
        insert.addParameter(new Parameter(new FullyQualifiedJavaType(modelName), StringUtil.toLowerCaseFirst(modelName)));
        insert.addBodyLine(serviceFieldName + ".insert(" + StringUtil.toLowerCaseFirst(modelName) + ");");
        insert.addBodyLine("return Message.successMessage();");
        clazz.addMethod(insert);
        // Method insertSelective
        Method insertSelective = new Method("insertSelective");
        insertSelective.addAnnotation("@RequestMapping(\"insertSelective\")");
        insertSelective.setVisibility(JavaVisibility.PUBLIC);
        insertSelective.setReturnType(new FullyQualifiedJavaType(messageFieldName));
        insertSelective.addParameter(new Parameter(new FullyQualifiedJavaType(modelName), StringUtil.toLowerCaseFirst(modelName)));
        insertSelective.addBodyLine(serviceFieldName + ".insertSelective(" + StringUtil.toLowerCaseFirst(modelName) + ");");
        insertSelective.addBodyLine("return Message.successMessage();");
        clazz.addMethod(insertSelective);
        // Method updateByPrimaryKey
        Method updateByPrimaryKey = new Method("updateByPrimaryKey");
        updateByPrimaryKey.addAnnotation("@RequestMapping(\"updateByPrimaryKey\")");
        updateByPrimaryKey.setVisibility(JavaVisibility.PUBLIC);
        updateByPrimaryKey.setReturnType(new FullyQualifiedJavaType(messageFieldName));
        updateByPrimaryKey.addParameter(new Parameter(new FullyQualifiedJavaType(modelName), StringUtil.toLowerCaseFirst(modelName)));
        updateByPrimaryKey.addBodyLine(serviceFieldName + ".updateByPrimaryKey(" + StringUtil.toLowerCaseFirst(modelName) + ");");
        updateByPrimaryKey.addBodyLine("return Message.successMessage();");
        clazz.addMethod(updateByPrimaryKey);
        // Method updateByPrimaryKeySelective
        Method updateByPrimaryKeySelective = new Method("updateByPrimaryKeySelective");
        updateByPrimaryKeySelective.addAnnotation("@RequestMapping(\"updateByPrimaryKeySelective\")");
        updateByPrimaryKeySelective.setVisibility(JavaVisibility.PUBLIC);
        updateByPrimaryKeySelective.setReturnType(new FullyQualifiedJavaType(messageFieldName));
        updateByPrimaryKeySelective.addParameter(new Parameter(new FullyQualifiedJavaType(modelName), StringUtil.toLowerCaseFirst(modelName)));
        updateByPrimaryKeySelective.addBodyLine(serviceFieldName + ".updateByPrimaryKeySelective(" + StringUtil.toLowerCaseFirst(modelName) + ");");
        updateByPrimaryKeySelective.addBodyLine("return Message.successMessage();");
        clazz.addMethod(updateByPrimaryKeySelective);
        // Method selectAll
        Method selectAll = new Method("selectAll");
        selectAll.addAnnotation("@RequestMapping(\"selectAll\")");
        selectAll.setVisibility(JavaVisibility.PUBLIC);
        selectAll.setReturnType(new FullyQualifiedJavaType(messageFieldName));
        selectAll.addParameter(new Parameter(new FullyQualifiedJavaType(exampleName), StringUtil.toLowerCaseFirst(exampleName)));
        selectAll.addBodyLine("List<" + modelName + "> " + StringUtil.toLowerCaseFirst(modelName) + "List = " + serviceFieldName + ".selectAll(" + StringUtil.toLowerCaseFirst(exampleName) + ");");
        selectAll.addBodyLine("return Message.successMessage(" + StringUtil.toLowerCaseFirst(modelName) + "List);");
        clazz.addMethod(selectAll);
        // Method selectByPages
        Method selectByPages = new Method("selectByPages");
        selectByPages.addAnnotation("@RequestMapping(\"selectByPages\")");
        selectByPages.setVisibility(JavaVisibility.PUBLIC);
        selectByPages.setReturnType(new FullyQualifiedJavaType(messageFieldName));
        selectByPages.addParameter(new Parameter(new FullyQualifiedJavaType(exampleName), StringUtil.toLowerCaseFirst(exampleName)));
        selectByPages.addParameter(new Parameter(new FullyQualifiedJavaType("int"), "pageNum"));
        selectByPages.addParameter(new Parameter(new FullyQualifiedJavaType("int"), "pageSize"));
        selectByPages.addBodyLine("List<" + modelName + "> " + StringUtil.toLowerCaseFirst(modelName) + "List = " + serviceFieldName + ".selectByPages(" + StringUtil.toLowerCaseFirst(exampleName) + ", pageNum, pageSize);");
        selectByPages.addBodyLine("return Message.successMessage(" + StringUtil.toLowerCaseFirst(modelName) + "List);");
        clazz.addMethod(selectByPages);
        // Method deleteByIDs
        Method deleteByIDs = new Method("deleteByIDs");
        deleteByIDs.addAnnotation("@RequestMapping(\"deleteByIDs\")");
        deleteByIDs.setVisibility(JavaVisibility.PUBLIC);
        deleteByIDs.setReturnType(new FullyQualifiedJavaType(messageFieldName));
        deleteByIDs.addParameter(new Parameter(new FullyQualifiedJavaType("Integer[]"), "ids"));
        deleteByIDs.addBodyLine(serviceFieldName + ".deleteByIDs(ids);");
        deleteByIDs.addBodyLine("return Message.successMessage();");
        clazz.addMethod(deleteByIDs);
        return new GeneratedJavaFile(clazz, targetProject, context.getJavaFormatter());
    }

}
