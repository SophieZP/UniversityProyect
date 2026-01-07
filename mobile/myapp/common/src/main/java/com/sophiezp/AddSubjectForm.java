package com.sophiezp;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;

public class AddSubjectForm extends Form {

    public AddSubjectForm(Form previousScreen) {
        super("Nueva Materia", BoxLayout.y());

        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        // Campos del formulario
        TextField nameField = new TextField("", "Nombre de la Materia", 20, TextField.ANY);
        TextField teacherField = new TextField("", "Nombre del Profesor", 20, TextField.ANY);

        // Botón de Guardar
        Button saveButton = new Button("Guardar Materia");
        saveButton.getAllStyles().setMarginTop(30);

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String teacher = teacherField.getText().trim();

            if(name.isEmpty()) {
                Dialog.show("Error", "El nombre es obligatorio", "OK", null);
                return;
            }

            guardarMateria(name, teacher, previousScreen);
        });

        this.add(nameField);
        this.add(teacherField);
        this.add(saveButton);
    }

    private void guardarMateria(String name, String teacher, Form previousScreen) {
        Long userId = UserSession.getInstance().getId();

        System.out.println("=== DEBUG SESIÓN ===");
        System.out.println("User ID en sesión: " + userId);
        System.out.println("Full Name: " + UserSession.getInstance().getFullName());
        System.out.println("Email: " + UserSession.getInstance().getEmail());

        if (userId == null) {
            Dialog.show("Error", "No hay sesión activa. Por favor inicia sesión nuevamente.", "OK", null);
            new LoginScreen().show();
            return;
        }

        // Escapar comillas en el nombre y profesor
        String escapedName = escapeJson(name);
        String escapedTeacher = escapeJson(teacher);

        String jsonBody = "{" +
                "\"name\":\"" + escapedName + "\"," +
                "\"teacherName\":\"" + escapedTeacher + "\"," +
                "\"minPassingGrade\":3.0," +
                "\"colorCode\":\"#336699\"" +
                "}";

        System.out.println("=== GUARDANDO MATERIA ===");
        System.out.println("User ID: " + userId);
        System.out.println("JSON Body: " + jsonBody);

        ApiClient.post("/subjects/user/" + userId, jsonBody, response -> {
            System.out.println("Response Code: " + response.getResponseCode());
            System.out.println("Response Success: " + response.isSuccess());
            System.out.println("Response Message: " + response.getMessage());

            if(response.isSuccess() || response.getResponseCode() == 201) {
                Dialog.show("Éxito", "Materia creada correctamente", "OK", null);
                new DashboardForm().show();
            } else {
                String errorMsg = response.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "Error desconocido (código: " + response.getResponseCode() + ")";
                }
                Dialog.show("Error", errorMsg, "OK", null);
            }
        });
    }

    /**
     * Escapa caracteres especiales para JSON
     */
    private String escapeJson(String input) {
        if (input == null) return "";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\\':
                    result.append("\\\\");
                    break;
                case '"':
                    result.append("\\\"");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                default:
                    result.append(c);
            }
        }
        return result.toString();
    }
}