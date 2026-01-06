package com.sophiezp;

import com.codename1.components.FloatingActionButton;
import com.codename1.io.*;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Border;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class FilesForm extends Form {

    private Long subjectId;
    private String subjectName;
    private Form previousScreen;

    public FilesForm(Form previous, Long subjectId, String subjectName) {
        super("Archivos - " + subjectName, BoxLayout.y());

        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.previousScreen = previous;

        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        // Contenedor para la lista de archivos
        Container filesContainer = new Container(BoxLayout.y());
        this.add(filesContainer);

        // Cargar archivos desde el servidor
        cargarArchivos(filesContainer);

        // FAB para cargar nuevo archivo
        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_CLOUD_UPLOAD);
        fab.bindFabToContainer(this);
        fab.addActionListener(e -> new UploadFileForm(this, subjectId, subjectName).show());
    }

    private void cargarArchivos(Container container) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/files/subject/" + subjectId);
        request.setPost(false);
        request.setHttpMethod("GET");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                try {
                    byte[] data = request.getResponseData();
                    JSONParser parser = new JSONParser();
                    Map<String, Object> response = parser.parseJSON(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));

                    ArrayList<Map<String, Object>> listaArchivos = (ArrayList<Map<String, Object>>) response.get("root");

                    container.removeAll();

                    if (listaArchivos != null && !listaArchivos.isEmpty()) {
                        for (Map<String, Object> archivoData : listaArchivos) {
                            container.add(crearTarjetaArchivo(archivoData));
                        }
                    } else {
                        container.add(new Label("No hay archivos en esta materia"));
                    }

                    this.revalidate();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    container.add(new Label("Error cargando archivos"));
                }
            } else {
                container.add(new Label("Error conexión: " + request.getResponseCode()));
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    private Container crearTarjetaArchivo(Map<String, Object> archivoData) {
        Container tarjeta = new Container(BoxLayout.y());
        tarjeta.getAllStyles().setPadding(5, 5, 5, 5);
        tarjeta.getAllStyles().setMargin(5, 5, 5, 5);
        tarjeta.getAllStyles().setBorder(Border.createRoundBorder(5, 5, 0xFF2196F3));

        Label nombre = new Label((String) archivoData.get("fileName"));
        nombre.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));

        Long fileSize = ((Number) archivoData.get("fileSize")).longValue();
        String fileSizeStr = formatearTamano(fileSize);
        Label tamaño = new Label("Tamaño: " + fileSizeStr);

        String tipo = (String) archivoData.get("fileType");
        Label tipo_label = new Label("Tipo: " + tipo);

        Label fecha = new Label("Subido: " + archivoData.get("uploadedAt"));
        fecha.getAllStyles().setFgColor(0xFF888888);
        fecha.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));

        String descripcion = (String) archivoData.get("description");
        if (descripcion != null && !descripcion.isEmpty()) {
            Label desc_label = new Label("Descripción: " + descripcion);
            tarjeta.add(desc_label);
        }

        Button descargarBtn = new Button("Descargar");
        Button eliminarBtn = new Button("Eliminar");

        Long fileId = ((Number) archivoData.get("id")).longValue();

        descargarBtn.addActionListener(e -> {
            Dialog.show("Información", "La descarga se iniciará en el dispositivo", "OK", null);
            // Aquí se podría implementar la descarga real
        });

        eliminarBtn.addActionListener(e -> {
            Command yes = new Command("Sí, eliminar");
            Command no = new Command("Cancelar");
            Command[] cmds = {yes, no};
            Command result = Dialog.show("Confirmar", "¿Eliminar este archivo?", cmds);
            if (result == yes) {
                eliminarArchivo(fileId);
            }
        });

        tarjeta.add(nombre);
        tarjeta.add(tamaño);
        tarjeta.add(tipo_label);
        tarjeta.add(fecha);

        Container buttonContainer = new Container(BoxLayout.x());
        buttonContainer.add(descargarBtn);
        buttonContainer.add(eliminarBtn);
        tarjeta.add(buttonContainer);

        return tarjeta;
    }

    private String formatearTamano(Long bytes) {
        if (bytes <= 0) return "0 B";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)) + " MB";
        if (bytes < 1024L * 1024 * 1024 * 1024) return (bytes / (1024 * 1024 * 1024)) + " GB";
        return (bytes / (1024L * 1024 * 1024 * 1024)) + " TB";
    }

    private void eliminarArchivo(Long fileId) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/files/" + fileId);
        request.setPost(false);
        request.setHttpMethod("DELETE");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                Dialog.show("Éxito", "Archivo eliminado", "OK", null);
                this.show();
            } else {
                Dialog.show("Error", "No se pudo eliminar", "OK", null);
            }
        });

        NetworkManager.getInstance().addToQueue(request);
    }
}
