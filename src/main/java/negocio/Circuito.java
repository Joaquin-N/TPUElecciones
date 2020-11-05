package negocio;

import soporte.TSBHashtable;

import java.util.Collection;

public class Circuito extends Region
{
    private TSBHashtable<String, Mesa> mesas;

    public Circuito(String codigo){ this(codigo, ""); }
    public Circuito(String codigo, String nombre)
    {
        super(codigo, nombre);
        mesas = new TSBHashtable<>();
    }

    public Mesa obtenerMesa(String codigo)
    {
        Mesa m = mesas.get(codigo);
        if (m == null)
        {
            m = new Mesa(codigo);
            mesas.put(codigo, m);
        }
        return m;
    }


    public Collection<Mesa> listarSubdivisiones()
    {
        return mesas.values();
    }
}
