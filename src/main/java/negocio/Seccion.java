package negocio;

import soporte.TSBHashtable;

import java.util.Collection;

public class Seccion extends Region
{
    private TSBHashtable<String, Circuito> circuitos;

    public Seccion(String codigo) { this(codigo, ""); }
    public Seccion(String codigo, String nombre)
    {
        super(codigo, nombre);
        circuitos = new TSBHashtable<>();
    }

    public Circuito obtenerCircuito(String codigo)
    {
        Circuito c = circuitos.get(codigo);
        if (c == null)
        {
            c = new Circuito(codigo);
            circuitos.put(codigo, c);
        }
        return c;
    }

    public void agregarCircuito(Circuito c)
    {
        circuitos.put(c.getCodigo(), c);
    }

    public Collection<Circuito> listarSubdivisiones()
    {
        return circuitos.values();
    }
}
