package interfaz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import negocio.*;
import persistencia.DBHelper;
import persistencia.DataManager;

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
    ComboBox cmbDistrito;
    @FXML
    ComboBox cmbSeccion;
    @FXML
    ComboBox cmbCircuito;
    @FXML
    ComboBox cmbMesa;

    @FXML
    TableView tblConteos;
    @FXML
    TableColumn colAgrupacion;
    @FXML
    TableColumn colVotos;

    @FXML
    TextField txtDirectorio;

    Gestor gestor = new Gestor();
    Pais p;
    String todos = "<todos>";

    public void initialize()
    {
        disableAll();
    }

    public void btnDirectorio_OnPressed(ActionEvent actionEvent)
    {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Seleccione el directorio en donde ubicar los archivos");
        File directorio = dc.showDialog(null);
        if (directorio != null)
        {
            String path = directorio.getAbsolutePath();
            try
            {
                p = gestor.cargarDatosDeArchivos(path);
                txtDirectorio.setText(path);
                enableAndLoad(cmbDistrito, p);
                btnFiltrar.setDisable(false);
            }
            catch(FileNotFoundException e)
            {
                disableAll();
                txtDirectorio.setText("");
                Object[] options = {"Aceptar"};
                JOptionPane.showOptionDialog(null,
                        "No se encontraron los archivos en el directorio indicado",
                        "Error",
                        JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
            }
        }
    }

    private void disableAll()
    {
        disableAndClear(cmbDistrito);
        disableAndClear(cmbSeccion);
        disableAndClear(cmbCircuito);
        disableAndClear(cmbMesa);
        btnFiltrar.setDisable(true);
    }

    /** Toma como parámetro un ComboBox, elimina su contenido y lo deshabilita */
    private void disableAndClear(ComboBox combo)
    {
        combo.getItems().clear();
        combo.setDisable(true);
    }

    /** Toma como parámetro un ComboBox y un objeto Region y carga el ComboBox con las subdivisiones de la Region
     *  Agrega también al comboBox un elemento "<todos>"
     *  Habilita el ComboBox */
    private void enableAndLoad(ComboBox combo, Region r)
    {
        ArrayList contenido = new ArrayList<>();
        contenido.add(todos);
        contenido.addAll(r.listarSubdivisiones());
        combo.getItems().clear();
        combo.getItems().addAll(contenido);
        combo.setValue(todos);
        combo.setDisable(false);
    }

    public void cmbDistrito_SelectionChanged(ActionEvent actionEvent)
    {
        if (cmbDistrito.getValue() == null) return;
        if (cmbDistrito.getValue().toString().equals(todos))
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
        if (cmbSeccion.getValue().toString().equals(todos))
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
        if (cmbCircuito.getValue().toString().equals(todos))
        {
            disableAndClear(cmbMesa);
        }
        else
        {
            enableAndLoad(cmbMesa, (Region)cmbCircuito.getValue());
        }
    }

    public void btnFiltrar_OnPressed(ActionEvent actionEvent)
    {
        if (cmbDistrito.getValue() != null)
        {
            colAgrupacion.setCellValueFactory(new PropertyValueFactory<Conteo, String>("nombreAgrupacion"));
            colVotos.setCellValueFactory(new PropertyValueFactory<Conteo, String>("cantidad"));

            tblConteos.getItems().clear();
            tblConteos.getItems().addAll(buscarConteos());
        }
    }

    private Collection<Conteo> buscarConteos()
    {
        if (cmbDistrito.getValue().equals(todos)) return p.getConteos();
        if (cmbSeccion.getValue().equals(todos)) return ((Distrito)cmbDistrito.getValue()).getConteos();
        if (cmbCircuito.getValue().equals(todos)) return ((Seccion)cmbSeccion.getValue()).getConteos();
        if (cmbMesa.getValue().equals(todos)) return ((Circuito)cmbCircuito.getValue()).getConteos();
        return ((Mesa)cmbMesa.getValue()).getConteos();
    }

    public void btnSalir_OnPressed(ActionEvent actionEvent)
    {
        DataManager dm = new DataManager();
        dm.clearData();
//        Stage stage = (Stage) btnSalir.getScene().getWindow();
//        stage.close();
    }
}
