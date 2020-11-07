package negocio;

import soporte.TSB_OAHashtable;

import java.util.Collection;
import java.util.Objects;

public class Region
{
    private String codigo;
    private String nombre;
    private TSB_OAHashtable<String, Conteo> conteos;
    private TSB_OAHashtable<String, Region> subregiones;

    public Region(String codigo) { this(codigo, ""); }
    public Region(String codigo, String nombre)
    {
        this.codigo = codigo;
        this.nombre = nombre;
        this.conteos = new TSB_OAHashtable<>();
        this.subregiones = new TSB_OAHashtable<>();
    }

    public String getCodigo()
    {
        return codigo;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Region obtenerSubregion(String codigo)
    {
        Region r = subregiones.get(codigo);
        if (r == null)
        {
            r = new Region(codigo);
            subregiones.put(codigo, r);
        }
        return r;
    }

    public Collection<Region> listarSubregiones()
    {
        return subregiones.values();
    }

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
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return codigo.equals(region.codigo);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(codigo) * 67;
    }

    @Override
    public String toString()
    {
        return nombre;
    }
}
