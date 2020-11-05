package persistencia;

import java.sql.*;

public class DBHelper
{
    Connection connection = null;
    String url = "jdbc:mysql://db4free.net:3306/tpuelecciones";
    Statement st;

    public void connect() throws SQLException
    {
        connection = DriverManager.getConnection (url,"cjn_tsb","D2gWGCiQnRaSjFB");
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

