package negocio;

import java.util.Objects;

public class Conteo
{
    private Agrupacion agrupacion;
    private int cantidad;

    public Conteo(Agrupacion agrupacion)
    {
        this(agrupacion, 0);
    }

    public Conteo(Agrupacion agrupacion, int cantidad)
    {
        this.agrupacion = agrupacion;
        this.cantidad = cantidad;
    }

    public Agrupacion getAgrupacion()
    {
        return agrupacion;
    }

    public String getNombreAgrupacion() { return agrupacion.getNombre(); }

    public int getCantidad()
    {
        return cantidad;
    }

    public void sumar(int cantidad)
    {
        this.cantidad += cantidad;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conteo conteo = (Conteo) o;
        return cantidad == conteo.cantidad &&
                agrupacion.equals(conteo.agrupacion);
    }

    @Override
    public int hashCode()
    {
        int hash = Objects.hash(agrupacion) * 67;
        hash += Objects.hash(cantidad) * 23;
        return hash;
    }

    @Override
    public String toString()
    {
        return "Agrupación: " + getNombreAgrupacion() + " | Votos: " + cantidad;
    }
}
