package negocio;

import soporte.TSBHashtable;

import java.util.Collection;

public abstract class Region
{
    protected String codigo;
    protected String nombre;
    protected TSBHashtable<String, Conteo> conteos;

    public Region(String codigo, String nombre)
    {
        this.codigo = codigo;
        this.nombre = nombre;
        this.conteos = new TSBHashtable<>();
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public abstract Collection listarSubdivisiones();

    public Collection<Conteo> getConteos() { return conteos.values(); }

    public void sumar(Agrupacion a, int votos)
    {
        Conteo c = conteos.get(a.getCodigo());
        if (c == null)
        {
            c = new Conteo(a);
            conteos.put(a.getCodigo(), c);
        }
        c.sumar(votos);
    }

    @Override
    public String toString()
    {
        return nombre;
    }
}
