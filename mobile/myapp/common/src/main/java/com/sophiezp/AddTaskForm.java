package com.sophiezp;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.spinner.Picker;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkManager;
import com.codename1.l10n.SimpleDateFormat; // Importante para formatear fechas
import java.util.Date;

public class AddTaskForm extends Form {

    public AddTaskForm(Form previousScreen, Long subjectId, String subjectName) {
        super("Nueva Tarea", BoxLayout.y());

        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        // 1. Campos de Texto
        TextField titleField = new TextField("", "Título (Ej: Parcial 1)", 20, TextField.ANY);
        TextField descField = new TextField("", "Descripción", 20, TextField.ANY);

        // 2. Selector de Fecha y Hora (Picker)
        Picker datePicker = new Picker();
        datePicker.setType(Display.PICKER_TYPE_DATE_AND_TIME);
        datePicker.setDate(new Date()); // Poner fecha actual por defecto

        // 3. Selector de Tipo (ComboBox)
        // Usamos los valores exactos que definimos en el ENUM de Java (Backend)
        ComboBox<String> typeCombo = new ComboBox<>("HOMEWORK", "EXAM", "EVENT");

        // 4. Botón Guardar
        Button saveButton = new Button("Guardar Tarea");
        saveButton.getAllStyles().setMarginTop(30);

        saveButton.addActionListener(e -> {
            String title = titleField.getText();
            String desc = descField.getText();
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
        // Formatear fecha a ISO-8601 que es lo que entiende Spring Boot
        // Ej: "2025-11-20T08:00:00"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String dateStr = sdf.format(date);

        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/tasks/subject/" + subjectId);
        request.setPost(true);
        request.setHttpMethod("POST");
        request.setContentType("application/json");

        String jsonBody = "{" +
                "\"title\":\"" + title + "\"," +
                "\"description\":\"" + desc + "\"," +
                "\"dueDate\":\"" + dateStr + "\"," +
                "\"type\":\"" + type + "\"" +
                "}";

        request.setRequestBody(jsonBody);

        request.addResponseListener(e -> {
            if(request.getResponseCode() == 200) {
                Dialog.show("Éxito", "Tarea agendada", "OK", null);
                // Volvemos a la pantalla anterior
                // Nota: Idealmente deberíamos forzar una recarga, pero por ahora volvemos
                previous.showBack();
            } else {
                Dialog.show("Error", "No se pudo guardar", "OK", null);
            }
        });

        NetworkManager.getInstance().addToQueue(request);
    }
}