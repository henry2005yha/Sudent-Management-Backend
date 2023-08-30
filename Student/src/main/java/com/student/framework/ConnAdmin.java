package com.student.framework;

import java.sql.Connection;
import java.util.ArrayList;


import com.nirvasoft.database.ConnMgr;

import password.DESedeEncryption;

public class ConnAdmin {
	
	public ConnAdmin() {
		super();
	}
	
	public static String servername = "";
	public static String port = "";
	public static String instance = "";
	public static String dbname = "";
	public static String dbUsr = "";
	public static String dbPwd = "";
	public static String connType = "";
	static String path = "";
	static String url="";

	public static Connection getConn() {

		Connection conn = null;
		
			readConnectionString();
			conn = (new ConnMgr(servername, Integer.parseInt(port), instance, dbname, dbUsr, dbPwd,
					Integer.parseInt(connType))).getConn();
			//System.out.println("i m done in con");
		return conn;
	}
	
	
	private static void readConnectionString() {
		String line = "";
		ArrayList<String> arl = new ArrayList<String>();
		path = ServerSession.serverPath + "WEB-INF/data/ConncetionConfig.txt";
		System.out.println(path);

		try {
			arl = FileUtil.readFile(path);
			//System.out.println("i m done reading file");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//get the line of connectionConfig
		for (int i = 0; i < arl.size(); i++) {
			if (!arl.get(i).equals("")) {
				if (arl.get(i).startsWith("1")) {
					line = arl.get(i);
					break;
				}
			}
		}
		String[] l_split = line.split(",");
		servername = l_split[1];
		port = l_split[2];
		instance = l_split[3];
		dbname = l_split[4];
		dbUsr = l_split[5];
		dbPwd =decryptPIN(l_split[6]);
		connType = l_split[7];
	}
	
	public static String decryptPIN(String p) {
		String pw = "";
		try {
			DESedeEncryption myEncryptor = new DESedeEncryption();
			pw = myEncryptor.decrypt(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pw;
	}
}
