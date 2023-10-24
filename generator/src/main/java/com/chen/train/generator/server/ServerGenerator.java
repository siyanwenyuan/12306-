package com.chen.train.generator.server;

import com.chen.train.generator.util.DbUtil;
import com.chen.train.generator.util.Field;
import com.chen.train.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ServerGenerator {


    static boolean readOnly = false;
    static String vuePath = "web/src/views/main/";

    static String serverPath = "[module]/src/main/java/com/chen/train/[module]/";
    static String pomPath = "generator\\pom.xml";

    static {
        new File(serverPath).mkdirs();
    }

    public static void main(String[] args) throws IOException, TemplateException, Exception {

        String generatorPath = getGeneratorPath();
        String module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("module:" + module);
        serverPath = serverPath.replace("[module]", module);
        System.out.println("servicePath:" + serverPath);


        // 读取table节点
        Document document = new SAXReader().read("generator/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());


        /**
         * 读取数据源
         *
         */
        Node connectionURL = document.selectSingleNode("//@connectionURL");
        Node userId = document.selectSingleNode("//@userId");
        Node password = document.selectSingleNode("//@password");
        System.out.println("url: " + connectionURL.getText());
        System.out.println("user: " + userId.getText());
        System.out.println("password: " + password.getText());
        DbUtil.url = connectionURL.getText();
        DbUtil.user = userId.getText();
        DbUtil.password = password.getText();


        String Domain = domainObjectName.getText();
        //替换
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        String do_main = tableName.getText().replaceAll("_", "-");
        String tableNameCn = DbUtil.getTableComment(tableName.getText());
        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
        Set<String> typeSet = getJavaTypes(fieldList);


        //将参数封装进去
        Map<String, Object> param = new HashMap<>();
        param.put("Domain", Domain);
        param.put("module",module);

        param.put("domain", domain);
        param.put("do_main", do_main);
        param.put("tableNameCn",tableNameCn);
        param.put("fieldList",fieldList);
        param.put("typeSet",typeSet);
        param.put("readOnly",readOnly);
        System.out.println("param:" + param);

      /* gen(Domain, param, "service","service");
        gen(Domain, param, "controller","controller");
        gen(Domain,param,"req","saveReq");

        gen(Domain,param,"req","queryReq");
        gen(Domain,param,"resp","queryResp");*/


        genVue(do_main,param);

    }


    /**
     * 前端
     * @param do_main
     * @param param
     * @throws IOException
     * @throws TemplateException
     */
    private static void genVue(String do_main, Map<String, Object> param) throws IOException, TemplateException {
        FreemarkerUtil.initConfig("vue.ftl");
        new File(vuePath).mkdirs();
        String fileName = vuePath + do_main + ".vue";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }

    /**
     * 决定生成的为哪个层
     *
     * @param Domain
     * @param param
     * @throws IOException
     * @throws TemplateException
     */

    private static void gen(String Domain, Map<String, Object> param,String packageName, String target) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target + ".ftl");
        String toPath = serverPath + packageName + "/";
        new File(toPath).mkdirs();
        String Target = target.substring(0, 1).toUpperCase() + target.substring(1);
        String fileName = toPath + Domain + Target + ".java";
        FreemarkerUtil.generator(fileName, param);
    }


    /**
     * 得到pom文件
     * 读取pom文件
     * 返回路径
     *
     * @return
     * @throws DocumentException
     */
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

    /**
     * 获取所有的Java类型，使用Set去重
     */
    private static Set<String> getJavaTypes(List<Field> fieldList) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            set.add(field.getJavaType());
        }
        return set;
    }


}
