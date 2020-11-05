package negocio;

import soporte.TSBHashtable;

import java.util.Collection;

public class Distrito extends Region
{
    private TSBHashtable<String, Seccion> secciones;

    public Distrito(String codigo)
    {
        this(codigo, "");
    }
    public Distrito(String codigo, String nombre)
    {
        super(codigo, nombre);
        secciones = new TSBHashtable<>();
    }

    public Seccion obtenerSeccion(String codigo)
    {
        Seccion s = secciones.get(codigo);
        if (s == null)
        {
            s = new Seccion(codigo);
            secciones.put(codigo, s);
        }
        return s;
    }

    public Collection<Seccion> listarSubdivisiones()
    {
        return secciones.values();
    }
}
