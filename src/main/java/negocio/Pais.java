package negocio;

import soporte.TSBHashtable;

import java.util.Collection;

public class Pais implements Region
{
    private String codigo;
    private String nombre;
    private TSBHashtable<String, Distrito> distritos;
    private TSBHashtable<String, Agrupacion> agrupaciones;
    private TSBHashtable<String, Conteo> conteos;

    public Pais(String codigo, String nombre)
    {
        this.codigo = codigo;
        this.nombre = nombre;
        distritos = new TSBHashtable<>();
        agrupaciones = new TSBHashtable<>();
        conteos = new TSBHashtable<>();
    }

    public Distrito obtenerDistrito(String codigo)
    {
        Distrito d = distritos.get(codigo);
        if (d == null) d = new Distrito(codigo);
        distritos.put(codigo, d);
        return d;
    }

    public Collection<Distrito> listarSubdivisiones()
    {
        return distritos.values();
    }

    public Collection<Conteo> getConteos() { return conteos.values(); }

    @Override
    public String toString()
    {
        return "Pais{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", distritos=" + distritos +
                ", agrupaciones=" + agrupaciones +
                '}';
    }
}
