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
    private Container filesContainer;

    public FilesForm(Form previous, Long subjectId, String subjectName) {
        super("Archivos - " + subjectName, BoxLayout.y());

        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.previousScreen = previous;

        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        // Contenedor para la lista de archivos
        filesContainer = new Container(BoxLayout.y());
        filesContainer.setScrollableY(true);
        this.add(filesContainer);

        // FAB para cargar nuevo archivo
        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_CLOUD_UPLOAD);
        fab.bindFabToContainer(this);
        fab.addActionListener(e -> new UploadFileForm(this, subjectId, subjectName).show());
    }

    @Override
    protected void onShowCompleted() {
        super.onShowCompleted();
        cargarArchivos();
    }

    private void cargarArchivos() {
        filesContainer.removeAll();
        Label loadingLabel = new Label("Cargando archivos...");
        loadingLabel.getAllStyles().setAlignment(Component.CENTER);
        filesContainer.add(loadingLabel);
        this.revalidate();

        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/files/subject/" + subjectId);
        request.setPost(false);
        request.setHttpMethod("GET");

        request.addResponseListener(e -> {
            System.out.println("=== RESPONSE FILES ===");
            System.out.println("Response Code: " + request.getResponseCode());

            if (request.getResponseCode() == 200) {
                try {
                    byte[] data = request.getResponseData();
                    if (data != null && data.length > 0) {
                        JSONParser parser = new JSONParser();
                        Map<String, Object> response = parser.parseJSON(
                                new InputStreamReader(new ByteArrayInputStream(data), "UTF-8")
                        );

                        System.out.println("Response completa: " + response);

                        // ✅ CORREGIDO: Extraer correctamente el array de archivos
                        Object dataField = response.get("data");
                        final ArrayList<Map<String, Object>> listaArchivos;

                        if (dataField instanceof ArrayList) {
                            listaArchivos = (ArrayList<Map<String, Object>>) dataField;
                        } else if (response.containsKey("root")) {
                            listaArchivos = (ArrayList<Map<String, Object>>) response.get("root");
                        } else {
                            listaArchivos = null;
                        }

                        System.out.println("Lista archivos: " + listaArchivos);

                        // Actualizar UI en el hilo principal
                        Display.getInstance().callSerially(() -> {
                            filesContainer.removeAll();

                            if (listaArchivos != null && !listaArchivos.isEmpty()) {
                                System.out.println("✅ Archivos encontrados: " + listaArchivos.size());
                                for (Map<String, Object> archivoData : listaArchivos) {
                                    System.out.println("Archivo: " + archivoData);
                                    filesContainer.add(crearTarjetaArchivo(archivoData));
                                }
                            } else {
                                Label emptyLabel = new Label("No hay archivos en esta materia");
                                emptyLabel.getAllStyles().setAlignment(Component.CENTER);
                                emptyLabel.getAllStyles().setFgColor(0x999999);
                                emptyLabel.getAllStyles().setMarginTop(50);
                                filesContainer.add(emptyLabel);
                            }

                            filesContainer.animateLayout(200);
                        });
                    } else {
                        Display.getInstance().callSerially(() -> {
                            filesContainer.removeAll();
                            filesContainer.add(new Label("No hay datos disponibles"));
                            this.revalidate();
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("❌ Error parseando archivos: " + ex.getMessage());
                    Display.getInstance().callSerially(() -> {
                        filesContainer.removeAll();
                        filesContainer.add(new Label("Error al cargar archivos: " + ex.getMessage()));
                        this.revalidate();
                    });
                }
            } else {
                Display.getInstance().callSerially(() -> {
                    filesContainer.removeAll();
                    filesContainer.add(new Label("Error de conexión: " + request.getResponseCode()));
                    this.revalidate();
                });
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    private Container crearTarjetaArchivo(Map<String, Object> archivoData) {
        Container tarjeta = new Container(BoxLayout.y());
        tarjeta.getAllStyles().setPadding(15, 15, 15, 15);
        tarjeta.getAllStyles().setMargin(10, 10, 10, 10);
        tarjeta.getAllStyles().setBorder(Border.createLineBorder(2, 0xFF2196F3));
        tarjeta.getAllStyles().setBgColor(0xFFFFFF);
        tarjeta.getAllStyles().setBgTransparency(255);

        // Nombre del archivo
        String fileName = archivoData.get("fileName") != null ?
                (String) archivoData.get("fileName") : "Sin nombre";
        Label nombre = new Label("📄 " + fileName);
        nombre.getAllStyles().setFont(Font.createSystemFont(
                Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM
        ));
        nombre.getAllStyles().setFgColor(0x000000);

        // Tamaño del archivo
        Object fileSizeObj = archivoData.get("fileSize");
        long fileSize = 0;
        if (fileSizeObj instanceof Number) {
            fileSize = ((Number) fileSizeObj).longValue();
        }
        String fileSizeStr = formatearTamano(fileSize);
        Label tamanoLabel = new Label("📊 Tamaño: " + fileSizeStr);
        tamanoLabel.getAllStyles().setFgColor(0x666666);

        // Tipo de archivo
        String tipo = archivoData.get("fileType") != null ?
                (String) archivoData.get("fileType") : "Desconocido";
        Label tipoLabel = new Label("🔖 Tipo: " + tipo);
        tipoLabel.getAllStyles().setFgColor(0x666666);

        // Fecha de subida
        String fecha = archivoData.get("uploadedAt") != null ?
                (String) archivoData.get("uploadedAt") : "";
        if (fecha.length() >= 10) {
            fecha = fecha.substring(0, 10);
        }
        Label fechaLabel = new Label("📅 Subido: " + fecha);
        fechaLabel.getAllStyles().setFgColor(0x888888);
        fechaLabel.getAllStyles().setFont(Font.createSystemFont(
                Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL
        ));

        // Descripción
        String descripcion = archivoData.get("description") != null ?
                (String) archivoData.get("description") : "";
        if (!descripcion.isEmpty()) {
            Label descLabel = new Label("📝 " + descripcion);
            descLabel.getAllStyles().setFgColor(0x555555);
            descLabel.getAllStyles().setMarginTop(5);
            tarjeta.add(descLabel);
        }

        // Botones de acción
        Container buttonContainer = new Container(BoxLayout.x());
        buttonContainer.getAllStyles().setMarginTop(10);

        Button descargarBtn = new Button("⬇️ Descargar");
        descargarBtn.getAllStyles().setBgColor(0x4CAF50);
        descargarBtn.getAllStyles().setFgColor(0xFFFFFF);
        descargarBtn.getAllStyles().setPadding(10, 10, 10, 10);

        Button eliminarBtn = new Button("🗑️ Eliminar");
        eliminarBtn.getAllStyles().setBgColor(0xF44336);
        eliminarBtn.getAllStyles().setFgColor(0xFFFFFF);
        eliminarBtn.getAllStyles().setPadding(10, 10, 10, 10);
        eliminarBtn.getAllStyles().setMarginLeft(10);

        Object fileIdObj = archivoData.get("id");
        Long fileId = 0L;
        if (fileIdObj != null) {
            fileId = Float.valueOf(fileIdObj.toString()).longValue();
        }
        final Long finalFileId = fileId;

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
                eliminarArchivo(finalFileId);
            }
        });

        buttonContainer.add(descargarBtn);
        buttonContainer.add(eliminarBtn);

        tarjeta.add(nombre);
        tarjeta.add(tamanoLabel);
        tarjeta.add(tipoLabel);
        tarjeta.add(fechaLabel);
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
                Display.getInstance().callSerially(() -> {
                    Dialog.show("Éxito", "Archivo eliminado", "OK", null);
                    cargarArchivos(); // Recargar la lista
                });
            } else {
                Display.getInstance().callSerially(() -> {
                    Dialog.show("Error", "No se pudo eliminar", "OK", null);
                });
            }
        });

        NetworkManager.getInstance().addToQueue(request);
    }
}