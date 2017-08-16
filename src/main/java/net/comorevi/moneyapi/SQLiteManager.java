package net.comorevi.moneyapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteManager {
	
	private MoneySAPI plugin;
	private Connection connection;
	private Statement statement;
	
	public SQLiteManager(MoneySAPI plugin){
		this.plugin = plugin;
		this.loadSqlite("userdata", "data");
	}
	
	public boolean isRegister(String username){
		try {
			if(statement.executeQuery("select count * from data where username = '" + username + "'") != null){
				return true;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return false;
	}
	
	public boolean createAccount(String username, double defaultMoney){
		if(!isRegister(username)){
			try {
				statement.executeUpdate("insert into data values('" + username + "', " + defaultMoney + ")");
				return true;//作成した
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}else{
			return false;//作成しなかった
		}
		return false;//作成しなかった
	}
	
	public int getMoney(String username){
		if(isRegister(username)){
			try {
				ResultSet rs = statement.executeQuery("select money from data where username = '" + username + "'");
				while(rs.next()){
					return rs.getInt("money");
				}
				rs.close();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		} else {
			return 0;
		}
		return 0;
	}
	
	public void setMoney(String username, int value){
		if(isRegister(username)){
			try {
				statement.execute("update data set money = " + value + " where username = '" + username + "'");
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	public void addMoney(String username, int value){
		int money = this.getMoney(username);
		try {
			value+=money;
			statement.execute("update data set money = " + value + " where username = '" + username + "'");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void grantMoney(String username, int value){
		addMoney(username, value);
	}
	
	public void reduceMoney(String username, int value){
		int money = this.getMoney(username);
		try {
			value-=money;
			if(value >= 0){
				statement.execute("update data set money = " + value + " where username = '" + username + "'");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void payMoney(String username, String targetusername, int value){
		reduceMoney(username, value);
		addMoney(targetusername, value);
	}
	
	public void loadSqlite(String filename, String tablename) {
		try {
			Class.forName("org.sqlite.JDBC");
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		this.connection = null;
			try {
				// データベースとの接続を確立
				connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/" + filename + ".db");
				
				statement = connection.createStatement();
				statement.setQueryTimeout(30);
				
				// sampleテーブルがあれば テーブル消去
				statement.executeUpdate("create table if not exists " + tablename + " (username string, money integer)");
		   }
		   catch(SQLException e) {
			   // if the error message is "out of memory",
			   // it probably means no database file is found
			   System.err.println(e.getMessage());
		   }
	}
	
	public void unLoadSqlite(String filename, String tablename){
		try {
			   if(connection != null)
				  connection.close();
		   }
		   	catch(SQLException e) {
			   // connection close failed.
			   System.err.println(e);
		   	}
	}
}
