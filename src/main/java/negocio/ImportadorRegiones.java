package negocio;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ImportadorRegiones
{
    private Pais p;

    public Pais cargarRegiones(String path)
    {
        p = new Pais("1", "Argentina");
        File f = new File(path + "/descripcion_regiones.dsv");
        try (Scanner archivoRegiones = new Scanner(f))
        {
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
        } catch (FileNotFoundException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return p;
    }

    public void cargarDistrito(String codigo, String nombre)
    {
        Distrito d = p.buscarDistrito(codigo);
        if (d == null)
        {
            d = new Distrito(codigo, nombre);
            p.agregarDistrito(d);
        }
        else d.setNombre(nombre);
    }

    public void cargarSeccion(String codigo, String nombre)
    {
        String dCod = codigo.substring(0,2);
        Distrito d = p.buscarDistrito(dCod);
        if (d == null)
        {
            d = new Distrito(dCod);
            p.agregarDistrito(d);
        }
        Seccion s = d.buscarSeccion(codigo);
        if (s == null) d.agregarSeccion(new Seccion(codigo, nombre));
        else s.setNombre(nombre);
    }

    public void cargarCircuito(String codigo, String nombre)
    {
        String dCod = codigo.substring(0,2);
        String sCod = codigo.substring(0,5);

        Distrito d = p.buscarDistrito(dCod);
        if (d == null)
        {
            d = new Distrito(dCod);
            p.agregarDistrito(d);
        }

        Seccion s = d.buscarSeccion(sCod);
        if (s == null)
        {
            s = new Seccion(sCod);
            d.agregarSeccion(s);
        }

        s.agregarCircuito(new Circuito(codigo, nombre));
    }

}
