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
        return codigo;
    }
}
