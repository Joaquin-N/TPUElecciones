package negocio;

import soporte.TSBHashtable;

import java.util.Collection;

public class Circuito
{
    private String codigo;
    private String nombre;
    private TSBHashtable<String, Mesa> mesas;
    private TSBHashtable<String, Conteo> conteos;

    public Circuito(String codigo, String nombre)
    {
        this.codigo = codigo;
        this.nombre = nombre;
        mesas = new TSBHashtable<>();
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

    public Collection<Mesa> listarMesas()
    {
        return mesas.values();
    }

    @Override
    public String toString()
    {
        return nombre;
    }
}
