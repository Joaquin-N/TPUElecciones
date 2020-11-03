package soporte;

import negocio.Circuito;
import negocio.Distrito;
import negocio.Pais;
import negocio.Seccion;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ImportadorRegiones
{
    private Pais p;

    public Pais cargarRegiones(String path) throws FileNotFoundException
    {
        p = new Pais("1", "Argentina");
        File f = new File(path + "/descripcion_regiones.dsv");
        Scanner archivoRegiones = new Scanner(f);

        // Con el primer nextLine limpiamos las cabeceras
        archivoRegiones.nextLine();
        while(archivoRegiones.hasNextLine())
        {
            String[] linea = archivoRegiones.nextLine().split("\\|");
            String codigo = linea[0];
            String nombre = linea[1];

            switch (codigo.length())
            {
                case 2:
                    cargarDistrito(codigo, nombre);
                    break;
                case 5:
                    cargarSeccion(codigo, nombre);
                    break;
                case 11:
                    cargarCircuito(codigo, nombre);
                    break;
            }
        }
        return p;
    }

    public void cargarDistrito(String codigo, String nombre)
    {
        Distrito d = p.obtenerDistrito(codigo);
        d.setNombre(nombre);
    }

    public void cargarSeccion(String codigo, String nombre)
    {
        String dCod = codigo.substring(0,2);

        Distrito d = p.obtenerDistrito(dCod);
        Seccion s = d.obtenerSeccion(codigo);
        s.setNombre(nombre);
    }

    public void cargarCircuito(String codigo, String nombre)
    {
        String dCod = codigo.substring(0,2);
        String sCod = codigo.substring(0,5);

        Distrito d = p.obtenerDistrito(dCod);
        Seccion s = d.obtenerSeccion(sCod);
        s.agregarCircuito(new Circuito(codigo, nombre));
    }

}
