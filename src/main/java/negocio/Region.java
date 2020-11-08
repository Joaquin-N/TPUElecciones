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

    
    /*
     * Método que busca la región correspondiente al código pasado por parámetro en la TSB_OAHashtable "subregiones".
     * Si la encuentra devuelve la región. Si no la encuentra, crea un nuevo objeto Región con el código indicado,
     * lo pone en la TSB_OAHashtable y luego devuelve el objeto creado.
     */
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


    /*
     * Devuelve el listado de regiones contenidas en la TSB_OAHashtable "subregiones".
     */
    public Collection<Region> listarSubregiones()
    {
        return subregiones.values();
    }


    /*
     * Devuelve el listado de conteos contenidos en la TSB_OAHashtable "conteos".
     */
    public Collection<Conteo> getConteos()
    {
        return conteos.values();
    }

    /*
     * Método que busca el conteo correspondiente a la agrupación pasada por parámetro en la TSB_OAHashtable "conteos".
     * Si lo encuentra suma al conteo la cantidad de votos pasada por parámetro. Si no lo encuentra, crea un nuevo objeto
     * Conteo asociado a la agrupación indicada y lo coloca en la TSB_OAHashtable. Luego le suma la cantidad de votos indicada.
     */
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
