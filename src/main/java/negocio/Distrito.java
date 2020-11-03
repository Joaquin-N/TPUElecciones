package negocio;

import soporte.TSBHashtable;

import java.util.Collection;

public class Distrito implements Region
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

    public Seccion obtenerSeccion(String codigo)
    {
        Seccion s = secciones.get(codigo);
        if (s == null) s = new Seccion(codigo);
        secciones.put(codigo, s);
        return s;
    }

    public Collection<Seccion> listarSubdivisiones()
    {
        return secciones.values();
    }

    public Collection<Conteo> getConteos() { return conteos.values(); }

    @Override
    public String toString()
    {
        return nombre;
    }
}
