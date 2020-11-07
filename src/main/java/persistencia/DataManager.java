package persistencia;

import negocio.AdministradorRegiones;
import negocio.Agrupacion;
import negocio.Conteo;
import negocio.Region;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class DataManager
{
    DBHelper dbh = new DBHelper();

    /*
     * Vacía todas las filas de las tablas de la base de datos.
     */
    public void clearData() throws SQLException
    {
        try
        {
            dbh.connect();
            dbh.executeNonQuery("DELETE FROM conteos");
            dbh.executeNonQuery("DELETE FROM mesas");
            dbh.executeNonQuery("DELETE FROM regiones");
        }
        finally
        {
            dbh.closeConnection();
        }
    }

    /*
     * Carga la estructura de regiones desde la base de datos.
     */
    public Region loadRegiones() throws SQLException
    {
        ResultSet regiones, mesas;

        Region pais = new Region("1", "Argentina");
        AdministradorRegiones admRegiones = new AdministradorRegiones(pais);

        try
        {
            dbh.connect();
            regiones = dbh.executeQuery("SELECT * FROM regiones");

            while(regiones.next())
                admRegiones.cargarRegion(regiones.getString("codigo"), regiones.getString("nombre"));

            mesas = dbh.executeQuery("SELECT * FROM mesas");

            while(mesas.next())
                admRegiones.cargarMesa(mesas.getString("codigo"), mesas.getString("codigo_region"));

            return pais;
        }
        finally
        {
            dbh.closeConnection();
        }
    }

    /*
     * Carga los conteos almacenados en la base de datos
     */
    public Collection<Conteo> loadConteos(String codigo, boolean mesa)
    {
        ResultSet conteos;
        ArrayList<Conteo> listConteos = new ArrayList<>();
        String field = mesa ? "codigo_mesa" : "codigo_region";
        try
        {
            dbh.connect();
            conteos = dbh.executeQuery("SELECT * FROM conteos WHERE " + field + " = '" + codigo + "'");

            while(conteos.next())
            {
                Agrupacion a = new Agrupacion(conteos.getString("codigo_agrupacion"), conteos.getString("nombre_agrupacion"));
                Conteo c = new Conteo(a, Integer.parseInt(conteos.getString("cantidad")));
                listConteos.add(c);
            }
            return listConteos;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            dbh.closeConnection();
        }
    }

    /*
     * Guarda la estructura de regiones, las mesas y los conteos en la base de datos.
     */
    public void saveData(Region pais) throws SQLException
    {
        try
        {
            dbh.connect();

            guardarRegion(pais);

            for(Region distrito : pais.listarSubregiones())
            {
                guardarRegion(distrito);

                for(Region seccion : distrito.listarSubregiones())
                {
                    guardarRegion(seccion);

                    for(Region circuito : seccion.listarSubregiones())
                    {
                       guardarRegion(circuito);

                        for(Region mesa : circuito.listarSubregiones())
                        {
                            guardarMesa(mesa, circuito.getCodigo());
                        }
                    }
                }
            }

        }
        finally
        {
            dbh.closeConnection();
        }
    }

    private void guardarRegion(Region region) throws SQLException
    {
        dbh.executeNonQuery("INSERT INTO regiones VALUES ('" +
                region.getCodigo() + "','" +
                region.getNombre() + "')");

        StringBuilder sql = new StringBuilder();

        boolean first = true;
        for(Conteo conteo : region.getConteos())
        {
            if(first)
            {
                sql.append("INSERT INTO conteos(codigo_agrupacion, nombre_agrupacion, codigo_region, cantidad) VALUES('");
                first = false;
            }
            else
                sql.append(",('");

            sql.append(
                    conteo.getAgrupacion().getCodigo() + "','" +
                    conteo.getAgrupacion().getNombre() + "','" +
                    region.getCodigo() + "'," +
                    conteo.getCantidad() + ")");
        }
        if(!first) dbh.executeNonQuery(sql.toString());
    }

    private void guardarMesa(Region mesa, String supCod) throws SQLException
    {
        dbh.executeNonQuery("INSERT INTO mesas VALUES ('" +
                mesa.getCodigo() + "','" +
                supCod +"')");

        StringBuilder sql = new StringBuilder();

        boolean first = true;
        for(Conteo conteo : mesa.getConteos())
        {
            if(first)
            {
                sql.append("INSERT INTO conteos(codigo_agrupacion, nombre_agrupacion, codigo_mesa, cantidad) VALUES ('");
                first = false;
            }
            else
                sql.append(",('");
            sql.append(
                    conteo.getAgrupacion().getCodigo() + "','" +
                    conteo.getAgrupacion().getNombre() + "','" +
                    mesa.getCodigo() + "'," +
                    conteo.getCantidad() + ")");
        }
        if(!first) dbh.executeNonQuery(sql.toString());
    }
}
