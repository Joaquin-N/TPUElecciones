package negocio;

import soporte.TSBHashtable;

import java.io.FileNotFoundException;

public class Gestor
{
    private Pais pais;
    private TSBHashtable<String, Agrupacion> agrupaciones;

    public Pais cargarDatosDeArchivos(String path) throws FileNotFoundException
    {
        ImportadorRegiones iRegiones = new ImportadorRegiones();
        ImportadorAgrupaciones iAgrupaciones = new ImportadorAgrupaciones();
        ImportadorResultados iResultados = new ImportadorResultados();

        pais = iRegiones.cargarRegiones(path);
        agrupaciones = iAgrupaciones.cargarAgrupaciones(path);
        iResultados.cargarResultados(path, pais, agrupaciones);

        return pais;
    }
}
