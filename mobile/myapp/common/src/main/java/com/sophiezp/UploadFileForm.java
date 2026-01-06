package com.sophiezp;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkManager;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;

public class UploadFileForm extends Form {

    public UploadFileForm(Form previousScreen, Long subjectId, String subjectName) {
        super("Subir Archivo", BoxLayout.y());

        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        TextField fileNameField = new TextField("", "Nombre del archivo", 30, TextField.ANY);
        TextField descriptionField = new TextField("", "Descripción (opcional)", 20, TextField.ANY);
        
        Button uploadButton = new Button("Subir Archivo");
        uploadButton.getAllStyles().setMarginTop(20);

        uploadButton.addActionListener(e -> {
            String fileName = fileNameField.getText();
            String description = descriptionField.getText();

            if (fileName.isEmpty()) {
                Dialog.show("Error", "El nombre del archivo es obligatorio", "OK", null);
                return;
            }

            subirArchivo(subjectId, fileName, description, previousScreen);
        });

        this.add(new Label("Materia: " + subjectName));
        this.add(new Label("Nombre del archivo:"));
        this.add(fileNameField);
        this.add(new Label("Descripción:"));
        this.add(descriptionField);
        this.add(uploadButton);
    }

    private void subirArchivo(Long subjectId, String fileName, String description, Form previous) {
        UserSession session = UserSession.getInstance();

        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/files/subject/" + subjectId + "/user/" + session.getId());
        request.setPost(true);
        request.setHttpMethod("POST");
        request.setContentType("application/json");

        String escapeDescription = escapeJson(description != null ? description : "");
        String jsonBody = "{" +
                "\"fileName\":\"" + escapeJson(fileName) + "\"," +
                "\"fileType\":\"" + getFileType(fileName) + "\"," +
                "\"fileSize\":0," +
                "\"filePath\":\"" + escapeJson(fileName) + "\"," +
                "\"description\":\"" + escapeDescription + "\"" +
                "}";

        request.setRequestBody(jsonBody);

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                Dialog.show("Éxito", "Archivo subido", "OK", null);
                previous.show();
            } else {
                Dialog.show("Error", "No se pudo subir el archivo (Código: " + request.getResponseCode() + ")", "OK", null);
            }
        });

        NetworkManager.getInstance().addToQueue(request);
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    private String getFileType(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot + 1).toUpperCase();
        }
        return "UNKNOWN";
    }
}
