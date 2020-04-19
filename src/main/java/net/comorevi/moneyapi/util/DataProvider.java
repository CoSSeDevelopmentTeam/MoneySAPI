package net.comorevi.moneyapi.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataProvider {

	public static final String TABLE_MONEY = "money";
	public static final String TABLE_COIN = "coin";
	public static final String TABLE_BANK = "bank";
	private Connection connection = null;
	
	public DataProvider(){
		connectSQL();
	}

	public boolean existsUserData(String table, String user) throws SQLException {
		if (!table.equals(TABLE_MONEY) && !table.equals(TABLE_COIN) && !table.equals(TABLE_BANK)) throw new SQLException("Table not found.");
		try {
			String sql = "SELECT username FROM ? WHERE username = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setQueryTimeout(30);
			statement.setString(1, table);
			statement.setString(2, user);

			boolean result = statement.executeQuery().next();
			statement.close();

			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<String> getUserListHoldOverCertainAmount(String table, int amount) throws SQLException {
		if (!table.equals(TABLE_MONEY) && !table.equals(TABLE_COIN) && !table.equals(TABLE_BANK)) throw new SQLException("Table not found.");
	    try {
	        String sql = "SELECT username FROM ? WHERE ? >= ?";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        statement.setQueryTimeout(30);
	        statement.setString(1, table);
			if (table.equals(TABLE_MONEY)) {
				statement.setString(2, "money");
			} else {
				statement.setString(2, "value");
			}
			statement.setInt(3, amount);

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
	
	public void createUserData(String table, String username, int defValue, boolean record) throws SQLException {
		if (!table.equals(TABLE_MONEY) && !table.equals(TABLE_COIN) && !table.equals(TABLE_BANK)) throw new SQLException("Table not found.");
	    try {
	        if (existsUserData(table, username)) return;
	        if (table.equals(TABLE_MONEY)) {
				String sql = "INSERT INTO money ( username, money, record ) values ( ?, ?, ? )";
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setQueryTimeout(30);
				statement.setString(1, username);
				statement.setInt(2, defValue);
				statement.setBoolean(3, record);

				statement.executeUpdate();
				statement.close();
			} else {
				String sql = "INSERT INTO ? ( username, value ) values ( ?, ? )";
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setQueryTimeout(30);
				statement.setString(1, table);
				statement.setString(2, username);
				statement.setInt(3, defValue);

				statement.executeUpdate();
				statement.close();
			}
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	public void deleteUserData(String table, String username) throws SQLException {
		if (!table.equals(TABLE_MONEY) && !table.equals(TABLE_COIN) && !table.equals(TABLE_BANK)) throw new SQLException("Table not found.");
		try {
			if (!existsUserData(table, username)) return;

			String sql = "DELETE FROM ? WHERE username = ?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setQueryTimeout(30);
			statement.setString(1, table);
			statement.setString(2, username);

			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getUserData(String table, String username) throws Exception {
		if (!table.equals(TABLE_MONEY) && !table.equals(TABLE_COIN) && !table.equals(TABLE_BANK)) throw new SQLException("Table not found.");
	    try {
	        if (!existsUserData(table, username)) throw new Exception("User data not found.");

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
	    throw new Exception("User data not found.");
	}

	public Map<Integer, Map<String, Integer>> getRanking(int range) {
	    return null;
    }

    public boolean getPublishStatus(String username) {
	    try {
	        if (!existsUserData(TABLE_MONEY, username)) return Boolean.parseBoolean(null);

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
	
    public void setUserData(String table, String username, int value) throws SQLException {
		if (!table.equals(TABLE_MONEY) && !table.equals(TABLE_COIN) && !table.equals(TABLE_BANK)) throw new SQLException("Table not found.");
	    try {
	        if (!existsUserData(table, username)) return;
			if (table.equals(TABLE_MONEY)) {
				String sql = "UPDATE money SET money = ? WHERE username = ?";
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setQueryTimeout(30);
				statement.setInt(1, value);
				statement.setString(2, username);

				statement.execute();
				statement.close();
			} else {
				String sql = "UPDATE ? SET value = ? WHERE username = ?";
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setQueryTimeout(30);
				statement.setString(1, table);
				statement.setInt(2, value);
				statement.setString(3, username);

				statement.execute();
				statement.close();
			}
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	public void setPublishStatus(String username, boolean record) {
	    try {
	        if (!existsUserData(TABLE_MONEY, username)) return;

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
	
    public void addUserData(String table, String username, int value) throws SQLException {
		if (!table.equals(TABLE_MONEY) && !table.equals(TABLE_COIN) && !table.equals(TABLE_BANK)) throw new SQLException("Table not found.");
	    try {
			if (!existsUserData(table, username)) return;
	        int pocket = getUserData(table, username);

	        if (table.equals(TABLE_MONEY)) {
				String sql = "UPDATE money SET money = ? WHERE username = ?";
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setInt(1, value + pocket);
				statement.setString(2, username);

				statement.execute();
				statement.close();
			} else {
				String sql = "UPDATE ? SET value = ? WHERE username = ?";
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setString(1, table);
				statement.setInt(2, value + pocket);
				statement.setString(3, username);

				statement.execute();
				statement.close();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	public void reduceUserData(String table, String username, int value) throws SQLException {
		if (!table.equals(TABLE_MONEY) && !table.equals(TABLE_COIN) && !table.equals(TABLE_BANK)) throw new SQLException("Table not found.");
	    try {
			if (!existsUserData(table, username)) return;
	        int pocket = getUserData(table, username);

	        if (table.equals(TABLE_MONEY)) {
				String sql = "UPDATE money SET money = ? WHERE username = ?";
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setQueryTimeout(30);
				if (value < 0) {
					pocket += value;
				} else {
					pocket -= value;
				}
				statement.setInt(1, pocket);
				statement.setString(2, username);

				statement.execute();
				statement.close();
			} else {
				String sql = "UPDATE ? SET value = ? WHERE username = ?";
				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setQueryTimeout(30);
				if (value < 0) {
					pocket += value;
				} else {
					pocket -= value;
				}
				statement.setString(1, table);
				statement.setInt(2, pocket);
				statement.setString(3, username);

				statement.execute();
				statement.close();
			}
        } catch (Exception e) {
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
            statement.executeUpdate(
            		"CREATE TABLE IF NOT EXISTS coin (" +
							" id INTEGER PRIMARY KEY," +
							" username TEXT NOT NULL," +
							" value INTEGER NOT NULL )"
			);
            statement.executeUpdate(
            		"CREATE TABLE IF NOT EXISTS bank (" +
							" id INTEGER PRIMARY KEY AUTOINCREMENT," +
							" username TEXT NOT NULL," +
							" value INTEGER NOT NULL)"
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