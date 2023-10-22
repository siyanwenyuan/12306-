package com.chen.train.generator.server;

import com.chen.train.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerGenerator {
  /*  static String toPath=" ";
    static String pomPath="generator\\pom.xml";
    static {
        new File(toPath).mkdirs();
    }

    public static void main(String[] args) throws IOException, TemplateException, DocumentException {

        getGeneratorPath();


    }

    private static String getGeneratorPath() throws DocumentException {
        SAXReader saxReader=new SAXReader();
        Map<String,String> map=new HashMap<>();
        map.put("pom","http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document=saxReader.read(pomPath);
        Node node=document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();
    }*/

    static String servicePath="[module]/src/main/java/com/chen/train/[module]/service/";
    static String pomPath="generator\\pom.xml";
    static{
        new File(servicePath).mkdirs();
    }

    public static void main(String[] args) throws IOException, TemplateException, Exception {

        String generatorPath = getGeneratorPath();

        String module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");

        System.out.println("module:"+module);

        servicePath = servicePath.replace("[module]", module);
        System.out.println("servicePath:"+servicePath);



        // 读取table节点
        Document document = new SAXReader().read("generator/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());


        String Domain = domainObjectName.getText();
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        String do_main = tableName.getText().replaceAll("_", "-");


        Map<String, Object> param = new HashMap<>();
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);
        System.out.println("param:"+param);

        FreemarkerUtil.initConfig("service.ftl");
        FreemarkerUtil.generator(servicePath+Domain+"Service.java",param);
    }

    private static String getGeneratorPath() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Map<String, String> map = new HashMap<String, String>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document = saxReader.read(pomPath);
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();

    }


}
