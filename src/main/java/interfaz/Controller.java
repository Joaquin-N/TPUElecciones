package interfaz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import negocio.*;

import java.io.File;
import java.util.ArrayList;

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

    Pais p;

    public void initialize()
    {
        cmbDistrito.setDisable(true);
        cmbSeccion.setDisable(true);
        cmbCircuito.setDisable(true);
        cmbMesa.setDisable(true);
    }

    public void btnDirectorio_OnPressed(ActionEvent actionEvent)
    {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Seleccione el directorio en donde ubicar los archivos");
        File directorio = dc.showDialog(null);
        if (directorio != null)
        {
            // TODO: validar que los archivos esten en la carpeta selecionada
            String path = directorio.getAbsolutePath();
            txtDirectorio.setText(path);

            ImportadorRegiones ir = new ImportadorRegiones();
            ImportadorAgrupaciones ia = new ImportadorAgrupaciones();
            p = ir.cargarRegiones(path);
            ia.cargarAgrupaciones(path);

            ArrayList distritos = new ArrayList<>();
            distritos.add("<todos>");
            distritos.addAll(p.listarDistritos());
            cmbDistrito.getItems().addAll(distritos);
            cmbDistrito.setValue("<todos>");
            cmbDistrito.setDisable(false);
        }
    }

    public void cmbDistrito_SelectionChanged(ActionEvent actionEvent)
    {
        if (cmbDistrito.getValue() == null) return;
        if (cmbDistrito.getValue().toString().equals("<todos>"))
        {
            cmbSeccion.getItems().clear();
            cmbSeccion.setDisable(true);
            cmbCircuito.getItems().clear();
            cmbCircuito.setDisable(true);
            cmbMesa.getItems().clear();
            cmbMesa.setDisable(true);
        }
        else
        {
            Distrito d = (Distrito) cmbDistrito.getValue();
            ArrayList secciones = new ArrayList<>();
            secciones.add("<todos>");
            secciones.addAll(d.listarSecciones());
            cmbSeccion.getItems().addAll(secciones);
            cmbSeccion.setValue("<todos>");
            cmbSeccion.setDisable(false);
        }
    }

    public void cmbSeccion_SelectionChanged(ActionEvent actionEvent)
    {
        if (cmbSeccion.getValue() == null) return;
        if (cmbSeccion.getValue().toString().equals("<todos>"))
        {
            cmbCircuito.getItems().clear();
            cmbCircuito.setDisable(true);
            cmbMesa.getItems().clear();
            cmbMesa.setDisable(true);
        }
        else
        {
            Seccion s = (Seccion) cmbSeccion.getValue();
            ArrayList circuitos = new ArrayList<>();
            circuitos.add("<todos>");
            circuitos.addAll(s.listarCircuitos());
            cmbCircuito.getItems().addAll(circuitos);
            cmbCircuito.setValue("<todos>");
            cmbCircuito.setDisable(false);
        }
    }

    public void cmbCircuito_SelectionChanged(ActionEvent actionEvent)
    {
        if (cmbCircuito.getValue() == null) return;
        if (cmbCircuito.getValue().toString().equals("<todos>"))
        {
            cmbMesa.getItems().clear();
            cmbMesa.setDisable(true);
        }
        else
        {
            Circuito c = (Circuito) cmbCircuito.getValue();
            ArrayList mesas = new ArrayList<>();
            mesas.add("<todos>");
            mesas.addAll(c.listarMesas());
            cmbMesa.getItems().addAll(mesas);
            cmbMesa.setValue("<todos>");
            cmbMesa.setDisable(false);
        }
    }

    public void btnFiltrar_OnPressed(ActionEvent actionEvent)
    {
        ObservableList<Conteo> conteos = FXCollections.observableArrayList();

        colAgrupacion.setCellValueFactory(new PropertyValueFactory<Conteo, String>("nombreAgrupacion"));
        colVotos.setCellValueFactory(new PropertyValueFactory<Conteo, String>("cantidad"));

        tblConteos.getItems().clear();
        tblConteos.getItems().addAll(conteos);

    }

    public void btnSalir_OnPressed(ActionEvent actionEvent)
    {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }
}
