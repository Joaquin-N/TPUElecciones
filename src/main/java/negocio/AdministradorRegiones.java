package negocio;

public class AdministradorRegiones
{
    private Region pais;

    public AdministradorRegiones(Region pais)
    {
        this.pais = pais;
    }


    /*
     * Método que recibe un código y un nombre como parámetro, determina de
     * que tipo de región se trata y la agrega a la estructura de regiones
     * en memoria.
     */
    public void cargarRegion(String codigo, String nombre)
    {
        switch (codigo.length())
        {
            case 2:
                cargarDistrito(codigo, nombre);
                break;
            case 5:
                cargarSeccion(codigo, nombre);
                break;
            case 11:
                cargarCircuito(codigo, nombre);
                break;
        }
    }

    private void cargarDistrito(String codigo, String nombre)
    {
        Region distrito = pais.obtenerSubregion(codigo);
        distrito.setNombre(nombre);
    }

    private void cargarSeccion(String codigo, String nombre)
    {
        String dCod = codigo.substring(0,2);

        Region distrito = pais.obtenerSubregion(dCod);
        Region seccion = distrito.obtenerSubregion(codigo);
        seccion.setNombre(nombre);
    }

    private void cargarCircuito(String codigo, String nombre)
    {
        String dCod = codigo.substring(0,2);
        String sCod = codigo.substring(0,5);

        Region distrito = pais.obtenerSubregion(dCod);
        Region seccion = distrito.obtenerSubregion(sCod);

        Region circuito = seccion.obtenerSubregion(codigo);

        circuito.setNombre(nombre);
    }

    public void cargarMesa(String codigo, String codigoCircuito)
    {
        String dCod = codigoCircuito.substring(0,2);
        String sCod = codigoCircuito.substring(0,5);

        Region distrito = pais.obtenerSubregion(dCod);
        Region seccion = distrito.obtenerSubregion(sCod);
        Region circuito = seccion.obtenerSubregion(codigoCircuito);
        Region mesa = circuito.obtenerSubregion(codigo);
        mesa.setNombre(codigo);
    }

    /*
     * Método que recibe los datos contenidos en el archivo de resultados de elecciones
     * y realiza la suma de los votos totales para el páis y los votos totales para cada
     * distrito, sección, circuito y mesa; según la agrupación indicada.
     */
    public void sumarVotos(Agrupacion agrupacion, String codDistrito, String codSeccion, String codCircuito, String codMesa, int cantidad)
    {
        pais.sumar(agrupacion, cantidad);

        Region distrito = pais.obtenerSubregion(codDistrito);
        distrito.sumar(agrupacion, cantidad);

        Region seccion = distrito.obtenerSubregion(codSeccion);
        seccion.sumar(agrupacion, cantidad);

        Region circuito = seccion.obtenerSubregion(codCircuito);
        circuito.sumar(agrupacion, cantidad);

        Region mesa = circuito.obtenerSubregion(codMesa);
        mesa.setNombre(codMesa);
        mesa.sumar(agrupacion, cantidad);
    }
}
