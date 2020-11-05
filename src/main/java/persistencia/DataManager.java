package persistencia;

import com.mysql.cj.protocol.ResultsetRow;

import java.sql.ResultSet;

public class DataManager
{
    DBHelper dbh = new DBHelper();

    public void clearData()
    {
        try
        {
            dbh.connect();
            dbh.executeNonQuery("DELETE FROM conteos");
            dbh.executeNonQuery("DELETE FROM mesas");
            dbh.executeNonQuery("DELETE FROM regiones");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            dbh.closeConnection();
        }
    }

    public void loadData()
    {
        ResultSet regiones, mesas, conteos;
        try
        {
            dbh.connect();
            regiones = dbh.executeQuery("SELECT * FROM regiones");
            mesas = dbh.executeQuery("SELECT * FROM mesas");
            conteos = dbh.executeQuery("SELECT * FROM conteos");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            dbh.closeConnection();
        }

//        regiones.s
//        for(ResultsetRow row : regiones)

    }
}
