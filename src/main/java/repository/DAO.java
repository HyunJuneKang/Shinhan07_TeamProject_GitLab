package main.java.repository;

import java.sql.Connection;

import main.java.util.DBUtil;

public class DAO {
	private static void connOpen() {
		Connection conn = DBUtil.dbConnect();

	}
	
	
	
	private static void connClose() {
		

	}
}	
