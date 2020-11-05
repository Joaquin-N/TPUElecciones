package negocio;

import soporte.TSBHashtable;

import java.util.Collection;

public class Pais extends Region
{
    private TSBHashtable<String, Distrito> distritos;

    public Pais(String codigo, String nombre)
    {
        super(codigo, nombre);
        distritos = new TSBHashtable<>();
    }

    public Distrito obtenerDistrito(String codigo)
    {
        Distrito d = distritos.get(codigo);
        if (d == null)
        {
            d = new Distrito(codigo);
            distritos.put(codigo, d);
        }
        return d;
    }

    public Collection<Distrito> listarSubdivisiones()
    {
        return distritos.values();
    }
}
