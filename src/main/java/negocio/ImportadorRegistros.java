package negocio;

import soporte.TSB_OAHashtable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ImportadorRegistros
{
    private Region pais;
    private AdministradorRegiones admRegiones;
    private TSB_OAHashtable<String, Agrupacion> agrupaciones;
    private String path;

    public ImportadorRegistros(String path)
    {
        this.path = path;
        pais = new Region("1", "Argentina");
        admRegiones = new AdministradorRegiones(pais);
        agrupaciones = new TSB_OAHashtable<>();
    }

    /*
     * Método que guía la carga de los datos desde los archivos, en el directorio indicado
     * por el usuario. Devuelve la Region "pais", raíz de la estructura de regiones generada.
     */
    public Region cargarDatosDeArchivos() throws FileNotFoundException
    {
        System.out.println("Procesando archivos...");
        cargarRegiones();
        cargarAgrupaciones();
        cargarResultados();
        System.out.println("Finalizado");
        return pais;
    }

    /*
     * Carga los datos de las regiones desde el archivo "descripcion_regiones.dsv".
     */
    private void cargarRegiones() throws FileNotFoundException
    {
        File f = new File(path + "/descripcion_regiones.dsv");
        Scanner archivoRegiones = new Scanner(f);

        // Con el primer nextLine limpiamos las cabeceras
        archivoRegiones.nextLine();
        while(archivoRegiones.hasNextLine())
        {
            String[] linea = archivoRegiones.nextLine().split("\\|");
            String codigo = linea[0];
            String nombre = linea[1];

            admRegiones.cargarRegion(codigo, nombre);
        }
    }

    /*
     * Carga los datos de las agrupaciones desde el archivo "descripcion_postulaciones.dsv".
     */
    private void cargarAgrupaciones() throws FileNotFoundException
    {
        File f = new File(path + "/descripcion_postulaciones.dsv");
        Scanner archivoAgrupaciones = new Scanner(f);

        // Con el primer nextLine limpiamos las cabeceras
        archivoAgrupaciones.nextLine();
        while(archivoAgrupaciones.hasNextLine())
        {
            String[] linea = archivoAgrupaciones.nextLine().split("\\|");
            if(linea[0].equals("000100000000000"))
            {
                String codigo = linea[2];
                String nombre = linea[3];

                if (!agrupaciones.containsKey(codigo)) agrupaciones.put(codigo, new Agrupacion(codigo, nombre));
            }
        }
    }

    /*
     * Carga las mesas y los votos en cada una de ellas desde el archivo "mesas_totales_agrp_politica.dsv".
     * Llama al método sumarVotos() del AdministradorRegiones para realizar el conteo de los votos por
     * región y agrupación.
     */
    private void cargarResultados() throws FileNotFoundException
    {

        File f = new File(path + "/mesas_totales_agrp_politica.dsv");
        Scanner archivoMesas = new Scanner(f);

        archivoMesas.nextLine();
        while(archivoMesas.hasNextLine())
        {
            String[] linea = archivoMesas.nextLine().split("\\|");
            String codigoCategoria = linea[4];
            if (!codigoCategoria.equals("000100000000000")) continue;

            String codigoAgrupacion = linea[5];
            String codigoDistrito = linea[0];
            String codigoSeccion = linea[1];
            String codigoCircuito = linea[2];
            String codigoMesa = linea[3];

            int votos = Integer.parseInt(linea[6]);
            Agrupacion a = agrupaciones.get(codigoAgrupacion);

            admRegiones.sumarVotos(a, codigoDistrito, codigoSeccion, codigoCircuito, codigoMesa, votos);
        }

    }

}
