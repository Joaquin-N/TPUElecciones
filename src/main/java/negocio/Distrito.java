package negocio;

import soporte.TSBHashtable;

import java.util.Collection;

public class Distrito
{
    private String codigo;
    private String nombre;
    private TSBHashtable<String, Seccion> secciones;
    private TSBHashtable<String, Conteo> conteos;

    public Distrito(String codigo)
    {
        this(codigo, "");
    }
    public Distrito(String codigo, String nombre)
    {
        this.codigo = codigo;
        this.nombre = nombre;
        secciones = new TSBHashtable<>();
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
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Seccion buscarSeccion(String codigo)
    {
        return secciones.get(codigo);
    }

    public void agregarSeccion(Seccion s)
    {
        secciones.put(s.getCodigo(), s);
    }

    public Collection<Seccion> listarSecciones()
    {
        return secciones.values();
    }

    @Override
    public String toString()
    {
        return nombre;
    }
}
