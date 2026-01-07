package com.sophiezp;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;

public class AddEvaluationForm extends Form {

    public AddEvaluationForm(Form previousScreen, Long subjectId, String subjectName) {
        super("Nueva Calificación", BoxLayout.y());

        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        // Campos del formulario
        TextField nameField = new TextField("", "Nombre (Ej: Parcial 1, Quiz 2)", 20, TextField.ANY);

        TextField percentageField = new TextField("", "Porcentaje (0-100)", 20, TextField.DECIMAL);
        percentageField.setHint("Ejemplo: 30 para 30%");

        TextField gradeField = new TextField("", "Nota Obtenida", 20, TextField.DECIMAL);
        gradeField.setHint("Ejemplo: 4.5");

        Label infoLabel = new Label("El porcentaje indica cuánto vale esta evaluación del total de la materia");
        infoLabel.getAllStyles().setFgColor(0x666666);
        infoLabel.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL));

        Button saveButton = new Button("Guardar Calificación");
        saveButton.getAllStyles().setMarginTop(30);
        saveButton.getAllStyles().setBgColor(0x4CAF50);
        saveButton.getAllStyles().setFgColor(0xFFFFFF);
        saveButton.getAllStyles().setPadding(15, 15, 15, 15);

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String percentageStr = percentageField.getText().trim();
            String gradeStr = gradeField.getText().trim();

            if (name.isEmpty() || percentageStr.isEmpty() || gradeStr.isEmpty()) {
                Dialog.show("Error", "Todos los campos son obligatorios", "OK", null);
                return;
            }

            try {
                double percentage = Double.parseDouble(percentageStr);
                double grade = Double.parseDouble(gradeStr);

                if (percentage <= 0 || percentage > 100) {
                    Dialog.show("Error", "El porcentaje debe estar entre 0 y 100", "OK", null);
                    return;
                }

                if (grade < 0 || grade > 5) {
                    Dialog.show("Error", "La nota debe estar entre 0 y 5", "OK", null);
                    return;
                }

                guardarCalificacion(subjectId, name, percentage, grade, previousScreen);

            } catch (NumberFormatException ex) {
                Dialog.show("Error", "Por favor ingresa valores numéricos válidos", "OK", null);
            }
        });

        this.add(new Label("Materia: " + subjectName));
        this.add(nameField);
        this.add(percentageField);
        this.add(gradeField);
        this.add(infoLabel);
        this.add(saveButton);
    }

    private void guardarCalificacion(Long subjectId, String name, double percentage, double grade, Form previous) {
        String escapedName = escapeJson(name);

        String jsonBody = "{" +
                "\"name\":\"" + escapedName + "\"," +
                "\"percentage\":" + percentage + "," +
                "\"obtainedGrade\":" + grade +
                "}";

        System.out.println("=== GUARDANDO CALIFICACIÓN ===");
        System.out.println("JSON Body: " + jsonBody);

        ApiClient.post("/evaluations/subject/" + subjectId, jsonBody, response -> {
            System.out.println("Response Code: " + response.getResponseCode());
            System.out.println("Response Success: " + response.isSuccess());

            if (response.isSuccess() || response.getResponseCode() == 201) {
                Dialog.show("Éxito", "Calificación guardada correctamente", "OK", null);
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