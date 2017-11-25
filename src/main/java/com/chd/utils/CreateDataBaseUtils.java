package com.chd.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

public class CreateDataBaseUtils {
	
	private List<File> fileList = new ArrayList<File>();
	@Test
	public void testName() throws Exception {
		// 创建SAXReader对象
		SAXReader reader = new SAXReader();
		
		for (File file : fileList) {
			// 读取文件 转换成Document
			Document document = reader.read(file);
			// 获取根节点元素对象
			Element root = document.getRootElement();
			Element resultMap = root.element("resultMap");
			String tableName = resultMap.attributeValue("type");
			tableName = tableName.substring(tableName.lastIndexOf(".") + 1, tableName.length()).toLowerCase();
			StringBuilder dropTableSql = new StringBuilder(" DROP TABLE IF EXISTS " + tableName + ";\n");
			StringBuilder createTbaleSql = new StringBuilder(" CREATE TABLE IF NOT EXISTS " + tableName + "( \n");

			List<Element> list = resultMap.elements();
			Iterator<Element> iterator = list.iterator();
			while (iterator.hasNext()) {
				Element element = (Element) iterator.next();
				String columnName = element.attributeValue("column");
				String columnType = element.attributeValue("jdbcType");
				String name = element.getName();
				if(name.equals("id") && columnType.equals("INTEGER")){
					createTbaleSql.append(" " + columnName + " " + columnType + " " + " NOT NULL AUTO_INCREMENT,\n");
					continue;
				}
				if(columnType.equals("VARCHAR")){
					createTbaleSql.append(" "+columnName + " " + columnType + " " + "(255),\n");
				}else{
					createTbaleSql.append(" "+columnName + " " + columnType + ",\n ");
				}
			}
			Element element = resultMap.element("id");
			String primaryKey = element.attributeValue("column");
			createTbaleSql.append(" PRIMARY KEY ("+primaryKey+") \n");
			createTbaleSql.append(" ); ");
			StringBuilder executeSql = new StringBuilder();
			executeSql.append(dropTableSql).append(createTbaleSql);
			System.out.println(executeSql);
		}
		
	}
	
	@Test
	public void testCreateTable() throws Exception {
		
	}
	
	@Before
	public void testReadFiles() throws Exception {
		File file = new File("src/main/resources/sqlmapper/");
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				this.fileList.add(files[i].getAbsoluteFile());
			}
		}
	}
}
