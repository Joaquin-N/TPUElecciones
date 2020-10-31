package negocio;

import soporte.TSBHashtable;

public class Mesa
{
    private String codigo;
    private TSBHashtable<String, Conteo> conteos;

    public Mesa(String codigo)
    {
        this.codigo = codigo;
        conteos = new TSBHashtable<>();
    }

    @Override
    public String toString()
    {
        return codigo;
    }
}
