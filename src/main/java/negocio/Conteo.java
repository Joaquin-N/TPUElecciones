package negocio;

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
}
