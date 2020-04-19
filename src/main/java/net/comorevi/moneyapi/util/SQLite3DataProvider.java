package net.comorevi.moneyapi.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLite3DataProvider {
	private Connection connection = null;
	
	public SQLite3DataProvider(){
		connectSQL();
	}
	
	public boolean existsAccount(String username) {
	    try {
	        String sql = "SELECT username FROM money WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(30);
            statement.setString(1, username);

            boolean result = statement.executeQuery().next();
            statement.close();

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return false;
	}
	
	public List<String> getPlayerListHoldOverCertainAmount(int amount) {
	    try {
	        String sql = "SELECT username FROM money WHERE money >= ?";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setQueryTimeout(30);
	        statement.setInt(1, amount);

	        ResultSet rs = statement.executeQuery();
	        List<String> result = new ArrayList<>();
	        while (rs.next()) {
	            result.add(rs.getString("username"));
            }
            rs.close();
            statement.close();

	        return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return null;
	}
	
	public void createAccount(String username, int defaultmoney, boolean record) {
	    try {
	        if (existsAccount(username)) return;

	        String sql = "INSERT INTO money ( username, money, record ) values ( ?, ?, ? )";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setQueryTimeout(30);
	        statement.setString(1, username);
	        statement.setInt(2, defaultmoney);
            statement.setBoolean(3, record);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	public void deleteAccount(String username) {
		try {
			if (!existsAccount(username)) return;

			String sql = "DELETE FROM money WHERE username = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, username);
			statement.setQueryTimeout(30);

			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getMoney(String username) {
	    try {
	        if (!existsAccount(username)) return -1;

	        String sql = "SELECT money FROM money WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(30);
            statement.setString(1, username);

            int result = statement.executeQuery().getInt("money");
            statement.close();

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return -1;
	}

	public Map<Integer, Map<String, Integer>> getRanking(int range) {
	    return null;
    }

    public boolean getPublishStatus(String username) {
	    try {
	        if (!existsAccount(username)) return Boolean.parseBoolean(null);

	        String sql = "SELECT record FROM money WHERE username = ?";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setQueryTimeout(30);
	        statement.setString(1, username);

	        boolean result = statement.executeQuery().getBoolean("record");
	        statement.close();

	        return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Boolean.parseBoolean(null);
    }
	
    public void setMoney(String username, int value) {
	    try {
	        if (!existsAccount(username)) return;

	        String sql = "UPDATE money SET money = ? WHERE username = ?";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setQueryTimeout(30);
	        statement.setInt(1, value);
	        statement.setString(2, username);

	        statement.execute();
	        statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	public void setPublishStatus(String username, boolean record) {
	    try {
	        if (!existsAccount(username)) return;

	        String sql = "UPDATE money SET record = ? WHERE username = ?";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setQueryTimeout(30);
	        statement.setBoolean(1, record);
	        statement.setString(2, username);

	        statement.execute();
	        statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
    public void addMoney(String username, int value) {
	    try {
	        int pocketMoney = getMoney(username);

	        String sql = "UPDATE money SET money = ? WHERE username = ?";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setInt(1, value + pocketMoney);
	        statement.setString(2, username);

	        statement.execute();
	        statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	public void reduceMoney(String username, int value) {
	    try {
	        int pocketMoney = getMoney(username);

	        String sql = "UPDATE money SET money = ? WHERE username = ?";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setQueryTimeout(30);
	        if (value < 0) {
	            pocketMoney += value;
            } else {
	            pocketMoney -= value;
            }
	        statement.setInt(1, pocketMoney);
	        statement.setString(2, username);

	        statement.execute();
	        statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

    private void connectSQL () {
        try {
            Class.forName("org.sqlite.JDBC");
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:./plugins/MoneySAPI/DataDB.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS money (" +
                            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            " username TEXT NOT NULL," +
                            " money INTEGER NOT NULL," +
                            " record INTEGER NOT NULL )"
            );
            statement.close();
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void disConnectSQL() {
	    if (connection != null) {
	        try {
	            connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}