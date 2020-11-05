package negocio;

import soporte.TSBHashtable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ImportadorAgrupaciones
{
    TSBHashtable<String, Agrupacion> agrupaciones = new TSBHashtable<>();
    public TSBHashtable<String, Agrupacion> cargarAgrupaciones(String path) throws FileNotFoundException
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
        return agrupaciones;
    }
}
