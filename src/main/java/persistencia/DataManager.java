package persistencia;

import negocio.AdministradorRegiones;
import negocio.Agrupacion;
import negocio.Conteo;
import negocio.Region;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;


/*
 * Clase que se encarga de gestionar la persistencia de los objetos, cargando y guardando datos en la base de datos.
 * Es independiente del motor de base de datos utilizado.
 */
public class DataManager
{
    DBHelper dbh = new DBHelper();

    /*
     * Vacía todas las filas de las tablas de la base de datos.
     */
    public void clearData() throws Exception
    {
        try
        {
            dbh.connect();
            dbh.executeNonQuery("DELETE FROM conteos");
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
    public Region loadRegiones() throws Exception
    {
        ResultSet regiones, mesas;

        Region pais = new Region("1", "Argentina");
        AdministradorRegiones admRegiones = new AdministradorRegiones(pais);

        try
        {
            dbh.connect();
            regiones = dbh.executeQuery("SELECT * FROM regiones");

            while(regiones.next())
            {
                if(regiones.getString("codigo_supregion").equals(""))
                    admRegiones.cargarRegion(regiones.getString("codigo"), regiones.getString("nombre"));
                else
                    admRegiones.cargarMesa(regiones.getString("codigo"), regiones.getString("codigo_supregion"));
            }
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
    public Collection<Conteo> loadConteos(String codigo)
    {
        ResultSet conteos;
        ArrayList<Conteo> listConteos = new ArrayList<>();
        try
        {
            dbh.connect();
            conteos = dbh.executeQuery("SELECT * FROM conteos WHERE codigo_region = '" + codigo + "'");

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
    public void saveData(Region pais) throws Exception
    {
        try
        {
            dbh.connect();

            System.out.println("Guardando datos en la BD...");
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
                            guardarRegion(mesa, circuito.getCodigo());
                        }
                    }
                }
                System.out.println("Regiones del distrito '" + distrito.getNombre() + "' guardadas.");
            }
            System.out.println("Finalizado");

        }
        finally
        {
            dbh.closeConnection();
        }
    }

    private void guardarRegion(Region region) throws SQLException
    {
        guardarRegion(region, "");
    }
    private void guardarRegion(Region region, String sup) throws SQLException
    {
        dbh.executeNonQuery("INSERT INTO regiones VALUES ('" +
                region.getCodigo() + "','" +
                region.getNombre() + "','" +
                sup + "')");

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
}
