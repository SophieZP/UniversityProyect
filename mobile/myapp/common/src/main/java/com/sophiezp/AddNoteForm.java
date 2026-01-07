package com.sophiezp;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;

public class AddNoteForm extends Form {

    public AddNoteForm(Form previousScreen, Long subjectId, String subjectName) {
        super("Nueva Nota", BoxLayout.y());

        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        TextField titleField = new TextField("", "Título de la nota", 20, TextField.ANY);
        TextArea contentArea = new TextArea(5, 20);
        contentArea.setHint("Contenido de la nota...");

        ComboBox<String> colorCombo = new ComboBox<>("#2196F3", "#4CAF50", "#FFC107", "#E91E63", "#9C27B0");

        Button saveButton = new Button("Guardar Nota");
        saveButton.getAllStyles().setMarginTop(20);

        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Dialog.show("Error", "Todos los campos son obligatorios", "OK", null);
                return;
            }

            guardarNota(subjectId, title, content, colorCombo.getSelectedItem(), previousScreen);
        });

        this.add(new Label("Materia: " + subjectName));
        this.add(titleField);
        this.add(new Label("Contenido:"));
        this.add(contentArea);
        this.add(new Label("Color:"));
        this.add(colorCombo);
        this.add(saveButton);
    }

    private void guardarNota(Long subjectId, String title, String content, String color, Form previous) {
        UserSession session = UserSession.getInstance();

        String escapedTitle = escapeJson(title);
        String escapedContent = escapeJson(content);

        String jsonBody = "{" +
                "\"title\":\"" + escapedTitle + "\"," +
                "\"content\":\"" + escapedContent + "\"," +
                "\"colorCode\":\"" + color + "\"" +
                "}";

        System.out.println("=== GUARDANDO NOTA ===");
        System.out.println("JSON Body: " + jsonBody);

        ApiClient.post("/notes/subject/" + subjectId + "/user/" + session.getId(), jsonBody, response -> {
            System.out.println("Response Code: " + response.getResponseCode());
            System.out.println("Response Success: " + response.isSuccess());

            if (response.isSuccess() || response.getResponseCode() == 201) {
                Dialog.show("Éxito", "Nota guardada correctamente", "OK", null);
                previous.show();
            } else {
                String errorMsg = response.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "Error desconocido (código: " + response.getResponseCode() + ")";
                }
                Dialog.show("Error", errorMsg, "OK", null);
            }
        });
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
}