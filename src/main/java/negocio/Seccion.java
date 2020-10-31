package negocio;

import soporte.TSBHashtable;

import java.util.Collection;

public class Seccion
{
    private String codigo;
    private String nombre;
    private TSBHashtable<String, Circuito> circuitos;
    private TSBHashtable<String, Conteo> conteos;

    public Seccion(String codigo) { this(codigo, ""); }
    public Seccion(String codigo, String nombre)
    {
        this.codigo = codigo;
        this.nombre = nombre;
        circuitos = new TSBHashtable<>();
        conteos = new TSBHashtable<>();
    }

    public String getCodigo()
    {
        return codigo;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre){ this.nombre = nombre; }

    public void agregarCircuito(Circuito c)
    {
        circuitos.put(c.getCodigo(), c);
    }

    public Collection<Circuito> listarCircuitos()
    {
        return circuitos.values();
    }

    @Override
    public String toString()
    {
        return nombre;
    }
}
