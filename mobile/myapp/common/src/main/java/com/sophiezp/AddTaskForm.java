package com.sophiezp;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.spinner.Picker;
import com.codename1.l10n.SimpleDateFormat;
import java.util.Date;

public class AddTaskForm extends Form {

    public AddTaskForm(Form previousScreen, Long subjectId, String subjectName) {
        super("Nueva Tarea", BoxLayout.y());

        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        TextField titleField = new TextField("", "Título (Ej: Parcial 1)", 20, TextField.ANY);
        TextField descField = new TextField("", "Descripción", 20, TextField.ANY);

        Picker datePicker = new Picker();
        datePicker.setType(Display.PICKER_TYPE_DATE_AND_TIME);
        datePicker.setDate(new Date());

        ComboBox<String> typeCombo = new ComboBox<>("HOMEWORK", "EXAM", "EVENT");

        Button saveButton = new Button("Guardar Tarea");
        saveButton.getAllStyles().setMarginTop(30);

        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String desc = descField.getText().trim();
            Date date = datePicker.getDate();
            String type = typeCombo.getSelectedItem();

            if(title.isEmpty()) {
                Dialog.show("Error", "El título es obligatorio", "OK", null);
                return;
            }

            guardarTarea(subjectId, title, desc, date, type, previousScreen);
        });

        this.add(new Label("Materia: " + subjectName));
        this.add(titleField);
        this.add(descField);
        this.add(new Label("Fecha de entrega:"));
        this.add(datePicker);
        this.add(new Label("Tipo de actividad:"));
        this.add(typeCombo);
        this.add(saveButton);
    }

    private void guardarTarea(Long subjectId, String title, String desc, Date date, String type, Form previous) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String dateStr = sdf.format(date);

        String escapedTitle = escapeJson(title);
        String escapedDesc = escapeJson(desc);

        String jsonBody = "{" +
                "\"title\":\"" + escapedTitle + "\"," +
                "\"description\":\"" + escapedDesc + "\"," +
                "\"dueDate\":\"" + dateStr + "\"," +
                "\"type\":\"" + type + "\"" +
                "}";

        System.out.println("=== GUARDANDO TAREA ===");
        System.out.println("JSON Body: " + jsonBody);

        ApiClient.post("/tasks/subject/" + subjectId, jsonBody, response -> {
            System.out.println("Response Code: " + response.getResponseCode());
            System.out.println("Response Success: " + response.isSuccess());

            if(response.isSuccess() || response.getResponseCode() == 201) {
                Dialog.show("Éxito", "Tarea agendada correctamente", "OK", null);
                previous.showBack();
            } else {
                String errorMsg = response.getMessage();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "Error desconocido (código: " + response.getResponseCode() + ")";
                }
                Dialog.show("Error", errorMsg, "OK", null);
            }
        });
    }

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