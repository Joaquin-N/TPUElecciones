package negocio;

import soporte.TSBHashtable;

import java.util.Collection;

public class Mesa
{
    private String codigo;
    private TSBHashtable<String, Conteo> conteos;

    public Mesa(String codigo)
    {
        this.codigo = codigo;
        conteos = new TSBHashtable<>();
    }

    public Collection<Conteo> getConteos() { return conteos.values(); }

    @Override
    public String toString()
    {
        return codigo;
    }
}
