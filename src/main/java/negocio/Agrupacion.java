package negocio;

import java.util.Objects;

public class Agrupacion
{
    private String codigo;
    private String nombre;

    public Agrupacion(String codigo, String nombre)
    {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo()
    {
        return codigo;
    }

    public String getNombre()
    {
        return nombre;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agrupacion that = (Agrupacion) o;
        return codigo.equals(that.codigo);
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
