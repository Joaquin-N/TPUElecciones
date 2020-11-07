package negocio;

import persistencia.DataManager;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Collection;

public class GestorElecciones
{
    /*
     * Llama el método cargarDatosDeArchivos() del importador de registros para
     * llevar a cabo la carga de las regiones, agrupaciones y conteos.
     */
    public Region cargarArchivos(String path) throws FileNotFoundException
    {
        ImportadorRegistros impRegistros = new ImportadorRegistros(path);
        return impRegistros.cargarDatosDeArchivos();
    }

    /*
     * Busca, carga y devuelve la estructura de regiones desde la base de datos, llamando
     * al método loadRegiones() del DataManager. Si no hay datos devuelve null.
     */
    public Region cargarRegiones() throws SQLException
    {
        DataManager dm = new DataManager();
        Region p = dm.loadRegiones();
        return p.listarSubregiones().size() == 0 ? null : p;
    }

    /*
     * Busca y carga los conteos desde la base de datos para una región dada,
     * llamando al método loadConteos() del DataManager.
     */
    public Collection<Conteo> cargarConteos(String codigo, boolean mesa)
    {
        DataManager dm = new DataManager();
        return dm.loadConteos(codigo, mesa);
    }

    /*
     * Guarda la estructura de regiones y los conteos para cada una de ellas en la base de datos,
     * limpiando primero los datos ya contenidos en ella.
     */
    public void guardarDatos(Region pais) throws SQLException
    {
        DataManager dm = new DataManager();
        dm.clearData();
        dm.saveData(pais);
    }
}
