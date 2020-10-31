package negocio;

import soporte.TSBHashtable;

import java.util.Collection;
import java.util.List;

public class Pais
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

    public void agregarDistrito(Distrito d)
    {
        distritos.put(d.getCodigo(), d);
    }

    public Distrito buscarDistrito(String codigo)
    {
        return distritos.get(codigo);
    }

    public Collection<Distrito> listarDistritos()
    {
        return distritos.values();
    }

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
