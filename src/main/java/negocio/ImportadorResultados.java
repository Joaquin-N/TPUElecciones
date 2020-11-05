package negocio;

import soporte.TSBHashtable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ImportadorResultados
{
    public void cargarResultados(String path, Pais pais, TSBHashtable<String, Agrupacion> agrupaciones) throws FileNotFoundException
    {
        {
            File f = new File(path + "/mesas_totales_agrp_politica.dsv");
            Scanner archivoMesas = new Scanner(f);
            //int c = 0;
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

                pais.sumar(a, votos);

                Distrito d = pais.obtenerDistrito(codigoDistrito);
                d.sumar(a, votos);

                Seccion s = d.obtenerSeccion(codigoSeccion);
                s.sumar(a, votos);

                Circuito c = s.obtenerCircuito(codigoCircuito);
                c.sumar(a, votos);

                Mesa m = c.obtenerMesa(codigoMesa);
                m.sumar(a, votos);

                //c+=1;
                //if (c % 1000 == 0) System.out.println(c);
                //System.out.println(codigoDistrito +" " +codigoSeccion+" " +codigoCircuito+" " +codigoMesa+" " +codigoCategoria+" " +codigoAgrupacion+" " +votos);
            }
        }
    }
}
