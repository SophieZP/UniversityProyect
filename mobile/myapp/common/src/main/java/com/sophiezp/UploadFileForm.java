package com.sophiezp;

import com.codename1.io.FileSystemStorage;
import com.codename1.io.MultipartRequest;
import com.codename1.io.NetworkManager;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.components.ToastBar;
import java.io.IOException;
import java.io.InputStream;

public class UploadFileForm extends Form {

    private String selectedFilePath = null;
    private String selectedFileName = null;
    private Label fileSelectedLabel;

    public UploadFileForm(Form previousScreen, Long subjectId, String subjectName) {
        super("Subir Archivo", BoxLayout.y());

        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        // Botón para seleccionar archivo
        Button selectFileBtn = new Button("📎 Seleccionar Archivo");
        selectFileBtn.getAllStyles().setMarginTop(20);
        selectFileBtn.getAllStyles().setMarginBottom(10);

        fileSelectedLabel = new Label("Ningún archivo seleccionado");
        fileSelectedLabel.getAllStyles().setFgColor(0x666666);

        TextField descriptionField = new TextField("", "Descripción (opcional)", 20, TextField.ANY);

        Button uploadButton = new Button("Subir Archivo");
        uploadButton.getAllStyles().setMarginTop(20);

        // Acción: Seleccionar archivo
        selectFileBtn.addActionListener(evt -> {
            // ✅ CORREGIDO: openGallery acepta (ActionListener<ActionEvent>, int)
            Display.getInstance().openGallery(new com.codename1.ui.events.ActionListener<com.codename1.ui.events.ActionEvent>() {
                @Override
                public void actionPerformed(com.codename1.ui.events.ActionEvent actionEvent) {
                    String selectedFile = (String) actionEvent.getSource();
                    if (selectedFile != null) {
                        selectedFilePath = selectedFile;
                        // Usar indexOf y substring correctamente
                        int lastSlash = selectedFilePath.lastIndexOf('/');
                        if (lastSlash == -1) {
                            lastSlash = selectedFilePath.lastIndexOf('\\');
                        }
                        selectedFileName = lastSlash >= 0 ?
                                selectedFilePath.substring(lastSlash + 1) : selectedFilePath;

                        fileSelectedLabel.setText("✓ Archivo: " + selectedFileName);
                        fileSelectedLabel.getAllStyles().setFgColor(0x4CAF50);
                        UploadFileForm.this.revalidate();

                        System.out.println("Archivo seleccionado: " + selectedFilePath);
                    }
                }
            }, Display.GALLERY_ALL);
        });

        // Acción: Subir archivo
        uploadButton.addActionListener(evt -> {
            if (selectedFilePath == null) {
                Dialog.show("Error", "Por favor selecciona un archivo primero", "OK", null);
                return;
            }

            String description = descriptionField.getText().trim();
            subirArchivoReal(subjectId, selectedFilePath, selectedFileName, description, previousScreen);
        });

        this.add(new Label("Materia: " + subjectName));
        this.add(selectFileBtn);
        this.add(fileSelectedLabel);
        this.add(new Label("Descripción:"));
        this.add(descriptionField);
        this.add(uploadButton);
    }

    private void subirArchivoReal(Long subjectId, String filePath, String fileName, String description, Form previous) {
        UserSession session = UserSession.getInstance();

        try {
            FileSystemStorage fs = FileSystemStorage.getInstance();

            // ✅ Leer el archivo correctamente
            InputStream is = fs.openInputStream(filePath);
            byte[] fileData = com.codename1.io.Util.readInputStream(is);
            is.close();

            System.out.println("=== SUBIENDO ARCHIVO ===");
            System.out.println("Nombre: " + fileName);
            System.out.println("Tamaño: " + fileData.length + " bytes");
            System.out.println("URL: http://localhost:8080/api/files/subject/" + subjectId + "/user/" + session.getId());

            // Crear MultipartRequest
            MultipartRequest request = new MultipartRequest();
            request.setUrl("http://localhost:8080/api/files/subject/" + subjectId + "/user/" + session.getId());
            request.setPost(true);
            request.setHttpMethod("POST");

            // ✅ IMPORTANTE: Añadir el archivo con el nombre correcto "file" (debe coincidir con @RequestParam en el backend)
            request.addData("file", fileData, "application/octet-stream");
            request.setFilename("file", fileName); // Establecer el nombre del archivo

            // Añadir descripción como parámetro
            if (description != null && !description.isEmpty()) {
                request.addArgument("description", description);
            }

            request.addResponseListener(evt -> {
                int responseCode = request.getResponseCode();
                System.out.println("=== RESPUESTA SERVIDOR ===");
                System.out.println("Response Code: " + responseCode);

                if (responseCode == 200 || responseCode == 201) {
                    Display.getInstance().callSerially(() -> {
                        ToastBar.showInfoMessage("Archivo subido correctamente");
                        previous.show();
                    });
                } else {
                    byte[] responseData = request.getResponseData();
                    String errorMsg = "Error código: " + responseCode;
                    if (responseData != null && responseData.length > 0) {
                        errorMsg = new String(responseData);
                        System.out.println("Error del servidor: " + errorMsg);
                    }
                    String finalErrorMsg = errorMsg;
                    Display.getInstance().callSerially(() -> {
                        Dialog.show("Error", "No se pudo subir el archivo: " + finalErrorMsg, "OK", null);
                    });
                }
            });

            NetworkManager.getInstance().addToQueue(request);

        } catch (IOException ex) {
            ex.printStackTrace();
            Dialog.show("Error", "Error al leer el archivo: " + ex.getMessage(), "OK", null);
        }
    }

    private String getFileType(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            return fileName.substring(lastDot + 1).toUpperCase();
        }
        return "UNKNOWN";
    }
}