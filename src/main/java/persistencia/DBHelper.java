package persistencia;

import java.sql.*;

public class DBHelper
{
    Connection connection = null;
    String url = "jdbc:mysql://localhost:3306/tpu?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    Statement st;

    public void connect() throws SQLException
    {
        connection = DriverManager.getConnection (url,"tsb","1234");
        st = connection.createStatement();
    }

    public void closeConnection()
    {
        if (connection != null)
        {
            try
            {
                connection.close ();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public ResultSet executeQuery(String sql) throws SQLException
    {
        return st.executeQuery(sql);
    }

    public void executeNonQuery(String sql) throws SQLException
    {
        st.executeUpdate(sql);
    }
}

