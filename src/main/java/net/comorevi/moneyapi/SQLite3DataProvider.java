package net.comorevi.moneyapi;

import net.comorevi.moneyapi.MoneySAPI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite3DataProvider {
	
	private MoneySAPI plugin;
	private Connection connection;
	private Statement statement;
	private boolean allowprint = false;
	
	public SQLite3DataProvider(MoneySAPI plugin){
		this.plugin = plugin;
		
		try {
			Class.forName("org.sqlite.JDBC");
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		connection = null;
			try {
				// データベースとの接続を確立
				connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/Datadb.db");
				
				statement = connection.createStatement();
				statement.setQueryTimeout(30);
				
				statement.executeUpdate("create table if not exists money (id integer primary key autoincrement, username text not null, money integer not null)");
				if(allowprint){
					printAllData();
				}
		   }
		   catch(SQLException e) {
			   // if the error message is "out of memory",
			   // it probably means no database file is found
			   System.err.println(e.getMessage());
		   }
	}
	
	public boolean existsAccount(String username) {
		try {
			ResultSet rs = statement.executeQuery("select username from money where username = '"+ username +"'");
			if(rs.getString("username") != null) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return false;
	}
	
	public void createAccount(String username, int defaultmoney) {
		if(!existsAccount(username)) {
			try {
				statement.executeUpdate("insert into money(username, money) values('"+ username +"', "+ defaultmoney +")");
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	public int getMoney(String username) {
		if(existsAccount(username)) {
			try {
				ResultSet rs = statement.executeQuery("select money from money where username = '"+ username +"'");
				return rs.getInt("money");
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
		return 404;
	}
	
	public void setMoney(String username, int value) {
		if(existsAccount(username)){
			try {
				statement.execute("update data set money = " + value + " where username = '" + username + "'");
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	public void addMoney(String username, int value) {
		int money = getMoney(username);
		try {
			value+=money;
			statement.execute("update data set money = " + value + " where username = '" + username + "'");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void grantMoney(String username, int value) {
		addMoney(username, value);
	}
	
	public void reduceMoney(String username, int value) {
		int money = getMoney(username);
		try {
			value-=money;
			if(value >= 0){
				statement.execute("update data set money = " + value + " where username = '" + username + "'");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void payMoney(String username, String targetname, int value) {
		reduceMoney(username, value);
		addMoney(targetname, value);
	}
	
	public void printAllData() {
		try {
			ResultSet rs = statement.executeQuery("select * from money");
			while(rs.next()) {
				System.out.println("id = " + rs.getInt("id"));
				System.out.println("username = " + rs.getString("username"));
				System.out.println("money = " + rs.getString("money"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void unLoadSqlite(){
		try {
			   if(connection != null)
				  connection.close();
		   }
		   	catch(SQLException e) {
		   		System.err.println(e.getMessage());
		   	}
	}
	
}