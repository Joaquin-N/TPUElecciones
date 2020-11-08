package interfaz;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import negocio.*;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

public class Controller
{
    @FXML
    Button btnDirectorio;
    @FXML
    Button btnFiltrar;
    @FXML
    Button btnSalir;
    @FXML
    Button btnGuardar;
    @FXML
    Button btnCargar;

    @FXML
    ComboBox<Region> cmbDistrito;
    @FXML
    ComboBox<Region> cmbSeccion;
    @FXML
    ComboBox<Region> cmbCircuito;
    @FXML
    ComboBox<Region> cmbMesa;

    @FXML
    TableView tblConteos;
    @FXML
    TableColumn colAgrupacion;
    @FXML
    TableColumn colVotos;

    @FXML
    Pane pane;
    @FXML
    TextField txtDirectorio;

    GestorElecciones ge = new GestorElecciones();
    boolean dbData = false;
    Region p;
    Region todos = new Region("-1", "<todos>");

    public void initialize()
    {
        disableFields();
    }

    /*
     * Busca los archivos de resultados de elecciones en un directorio indicado por el usuario e
     * indica al GestorelElecciones que realice la carga de los datos.
     */
    public void btnDirectorio_OnPressed(ActionEvent actionEvent)
    {
        /* Abre una instancia del explorador de archivos para que el usuario seleccione el directorio donde
         * se ubican los archivos */
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Seleccione el directorio en donde ubicar los archivos");
        File directorio = dc.showDialog(null);

        // Valida que se haya seleccionado un directorio
        if (directorio != null)
        {
            String path = directorio.getAbsolutePath();

            // Habilita el cursor de espera
            pane.getScene().setCursor(Cursor.WAIT);
            disableButtons(true);
            disableFields();

            // Llama al método cargarArchivos() del GestorElecciones
            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() throws FileNotFoundException
                {
                    p = ge.cargarArchivos(path);
                    return null ;
                }
            };

            // Carga las regiones en pantalla y habilita los controles
            task.setOnSucceeded(e ->
            {
                txtDirectorio.setText(path);
                enableAndLoad(cmbDistrito, p);
                dbData = false;
                btnFiltrar_OnPressed(new ActionEvent());
                disableButtons(false);
                pane.getScene().setCursor(Cursor.DEFAULT);
            });

            task.setOnFailed(e ->
            {
                disableButtons(false);
                pane.getScene().setCursor(Cursor.DEFAULT);
                txtDirectorio.setText("");
                Object[] options = {"Aceptar"};
                JOptionPane.showOptionDialog(null,
                        "No se encontraron los archivos en el directorio indicado",
                        "Error",
                        JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
            });
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        }
    }

    /*
     * Llama al método cargarRegiones() del GestorElecciones para cargar los datos desde la base de datos.
     */
    public void btnCargar_OnPressed(ActionEvent actionEvent)
    {
        // Habilita el cursor de espera
        pane.getScene().setCursor(Cursor.WAIT);
        disableButtons(true);
        disableFields();
        txtDirectorio.setText("");

        // Llama al método cargarRegiones() del GestorElecciones
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call()
            {
                try
                {
                    p = ge.cargarRegiones();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null ;
            }
        };

        // Carga los distritos en el ComboBox, si se encuentran datos
        task.setOnSucceeded(e ->
        {
            pane.getScene().setCursor(Cursor.DEFAULT);
            disableButtons(false);
            btnGuardar.setDisable(true);
            if(p == null)
            {
                Object[] options = {"Aceptar"};
                JOptionPane.showOptionDialog(null,
                        "No se encontraron datos en la base de datos",
                        "Error",
                        JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
                return;
            }
            enableAndLoad(cmbDistrito, p);
            dbData = true;
            btnFiltrar_OnPressed(new ActionEvent());
            btnGuardar.setDisable(true);
            pane.getScene().setCursor(Cursor.DEFAULT);
        });

        task.setOnFailed(e ->
        {
            disableButtons(false);
            btnGuardar.setDisable(true);
            pane.getScene().setCursor(Cursor.DEFAULT);
            disableFields();
            Object[] options = {"Aceptar"};
            JOptionPane.showOptionDialog(null,
                    "No se pudo conectar con la base de datos",
                    "Error",
                    JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        });
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    /*
     * Llama al método guardarDatos del GestorElecciones para almacenar los datos en la base de datos.
     */
    public void btnGuardar_OnPressed(ActionEvent actionEvent)
    {
        // Habilita el cursor de espera
        pane.getScene().setCursor(Cursor.WAIT);
        disableCombos(true);
        disableButtons(true);

        // Llama al método GuardarRegiones() del GestorElecciones
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call()
            {
                try
                {
                    ge.guardarDatos(p);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return null ;
            }
        };

        task.setOnSucceeded(e ->
        {
            disableButtons(false);
            disableCombos(false);
            pane.getScene().setCursor(Cursor.DEFAULT);
        });

        task.setOnFailed(e ->
        {
            disableButtons(false);
            disableCombos(false);
            pane.getScene().setCursor(Cursor.DEFAULT);
            Object[] options = {"Aceptar"};
            JOptionPane.showOptionDialog(null,
                    "No se pudo conectar con la base de datos",
                    "Error",
                    JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        });
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    /*
     * Deshabilita y limpia los controles en pantalla.
     */
    private void disableFields()
    {
        disableAndClear(cmbDistrito);
        disableAndClear(cmbSeccion);
        disableAndClear(cmbCircuito);
        disableAndClear(cmbMesa);
        btnFiltrar.setDisable(true);
        btnGuardar.setDisable(true);
        tblConteos.getItems().clear();
    }

    /*
     * Deshabilita o habilita los botones.
     */
    private void disableButtons(boolean disable)
    {
        btnFiltrar.setDisable(disable);
        btnDirectorio.setDisable(disable);
        btnCargar.setDisable(disable);
        btnGuardar.setDisable(disable);
    }

    /*
     * Deshabilita o habilita los ComboBox.
     */
    private void disableCombos(boolean disable)
    {
        cmbDistrito.setDisable(disable);
        cmbSeccion.setDisable(disable);
        cmbCircuito.setDisable(disable);
        cmbMesa.setDisable(disable);
    }

    /*
     * Toma como parámetro un ComboBox, elimina su contenido y lo deshabilita.
     */
    private void disableAndClear(ComboBox<Region> combo)
    {
        combo.getItems().clear();
        combo.setDisable(true);
    }

    /* Toma como parámetro un ComboBox y un objeto Region y carga el ComboBox con las subdivisiones de la Region.
     *  Agrega también al comboBox un elemento "<todos>".
     *  Habilita el ComboBox.
     */
    private void enableAndLoad(ComboBox<Region> combo, Region r)
    {
        ArrayList contenido = new ArrayList<>();
        contenido.add(todos);
        contenido.addAll(r.listarSubregiones());
        combo.getItems().clear();
        combo.getItems().addAll(contenido);
        combo.setValue(todos);
        combo.setDisable(false);
    }

    /*
     * Los siguientes métodos detectan cambios en la selección de las regiones en los
     * ComboBox y gestionan la carga o deshabilitación de los demás ComboBox.
     */
    public void cmbDistrito_SelectionChanged(ActionEvent actionEvent)
    {
        if (cmbDistrito.getValue() == null) return;
        if (cmbDistrito.getValue().equals(todos))
        {
            disableAndClear(cmbSeccion);
            disableAndClear(cmbCircuito);
            disableAndClear(cmbMesa);
        }
        else
        {
            enableAndLoad(cmbSeccion, (Region)cmbDistrito.getValue());
        }
    }

    public void cmbSeccion_SelectionChanged(ActionEvent actionEvent)
    {
        if (cmbSeccion.getValue() == null) return;
        if (cmbSeccion.getValue().equals(todos))
        {
            disableAndClear(cmbCircuito);
            disableAndClear(cmbMesa);
        }
        else
        {
            enableAndLoad(cmbCircuito, (Region)cmbSeccion.getValue());
        }
    }

    public void cmbCircuito_SelectionChanged(ActionEvent actionEvent)
    {
        if (cmbCircuito.getValue() == null) return;
        if (cmbCircuito.getValue().equals(todos))
        {
            disableAndClear(cmbMesa);
        }
        else
        {
            enableAndLoad(cmbMesa, (Region)cmbCircuito.getValue());
        }
    }

    /*
     * Busca los conteos según la región seleccionada y los carga en la grilla.
     */
    public void btnFiltrar_OnPressed(ActionEvent actionEvent)
    {
        colAgrupacion.setCellValueFactory(new PropertyValueFactory<Conteo, String>("nombreAgrupacion"));
        colVotos.setCellValueFactory(new PropertyValueFactory<Conteo, String>("cantidad"));

        tblConteos.getItems().clear();

        Collection conteos = dbData ? cargarConteos() : buscarConteos();
        tblConteos.getItems().addAll(conteos);
    }

    /*
     * Busca los conteos en memoria que fueron cargados desde los archivos.
     */
    private Collection<Conteo> buscarConteos()
    {
        if (cmbDistrito.getValue().equals(todos)) return p.getConteos();
        if (cmbSeccion.getValue().equals(todos)) return cmbDistrito.getValue().getConteos();
        if (cmbCircuito.getValue().equals(todos)) return cmbSeccion.getValue().getConteos();
        if (cmbMesa.getValue().equals(todos)) return cmbCircuito.getValue().getConteos();
        return cmbMesa.getValue().getConteos();
    }

    /*
     * Busca los conteos en la base de datos.
     */
    private Collection<Conteo> cargarConteos()
    {
        if (cmbDistrito.getValue().equals(todos)) return ge.cargarConteos(p.getCodigo());
        if (cmbSeccion.getValue().equals(todos)) return ge.cargarConteos(cmbDistrito.getValue().getCodigo());
        if (cmbCircuito.getValue().equals(todos)) return ge.cargarConteos(cmbSeccion.getValue().getCodigo());
        if (cmbMesa.getValue().equals(todos)) return ge.cargarConteos(cmbCircuito.getValue().getCodigo());
        return ge.cargarConteos(cmbMesa.getValue().getCodigo());
    }

    /*
     * Cierra la aplicación y los procesos que se estén ejecutando
     */
    public void btnSalir_OnPressed(ActionEvent actionEvent)
    {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }
}
