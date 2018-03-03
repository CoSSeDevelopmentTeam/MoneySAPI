package net.comorevi.moneyapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite3DataProvider {
	
	private MoneySAPI plugin;
	
	public SQLite3DataProvider(MoneySAPI plugin){
		this.plugin = plugin;
		this.connect();
	}
	
	public boolean existsAccount(String username) {
        Connection connection = null;
		try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toString() + "/DataDB.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("select * from money where username = '"+ username +"'");
			return rs.next();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
		return false;
	}
	
	public void createAccount(String username, int defaultmoney) {
		if(!existsAccount(username)) {
            Connection connection = null;
			try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toString() + "/DataDB.db");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
				statement.executeUpdate("insert into money(username, money) values('"+ username +"', "+ defaultmoney +")");
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			} finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
		}
	}
	
	public int getMoney(String username) {
		if(existsAccount(username)) {
            Connection connection = null;
			try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toString() + "/DataDB.db");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
				ResultSet rs = statement.executeQuery("select money from money where username = '"+ username +"'");
				return rs.getInt("money");
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			} finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
		}
		return 404;
	}
	
	public void setMoney(String username, int value) {
		if(existsAccount(username)){
            Connection connection = null;
			try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toString() + "/DataDB.db");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
				statement.execute("update money set money = " + value + " where username = '" + username + "'");
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			} finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
		}
	}
	
	public void addMoney(String username, int value) {
		int money = getMoney(username);
        Connection connection = null;
		try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toString() + "/DataDB.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
			value+=money;
			statement.execute("update money set money = " + value + " where username = '" + username + "'");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	public void grantMoney(String username, int value) {
		addMoney(username, value);
	}
	
	public void reduceMoney(String username, int value) {
		int money = getMoney(username);
        Connection connection = null;
		try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toString() + "/DataDB.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
			money-=value;
			if(value >= 0){
				statement.execute("update money set money = " + money + " where username = '" + username + "'");
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	public void payMoney(String username, String targetname, int value) {
		reduceMoney(username, value);
		addMoney(targetname, value);
	}
	
	public void printAllData() {
        Connection connection = null;
		try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toString() + "/DataDB.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("select * from money");
			while(rs.next()) {
				System.out.println("id = " + rs.getInt("id"));
				System.out.println("username = " + rs.getString("username"));
				System.out.println("money = " + rs.getString("money"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
	}

    public void connect () {
        try {
            Class.forName("org.sqlite.JDBC");
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toString() + "/DataDB.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("create table if not exists money (id integer primary key autoincrement, username text not null, money integer not null)");
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
}