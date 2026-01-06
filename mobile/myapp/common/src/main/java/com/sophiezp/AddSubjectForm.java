package com.sophiezp;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkManager;

public class AddSubjectForm extends Form {

    public AddSubjectForm(Form previousScreen) {
        super("Nueva Materia", BoxLayout.y());

        // Botón de atrás (Toolbar)
        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        // Campos del formulario
        TextField nameField = new TextField("", "Nombre de la Materia", 20, TextField.ANY);
        TextField teacherField = new TextField("", "Nombre del Profesor", 20, TextField.ANY);

        // Botón de Guardar
        Button saveButton = new Button("Guardar Materia");
        saveButton.getAllStyles().setMarginTop(30);

        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String teacher = teacherField.getText();

            if(name.isEmpty()) {
                Dialog.show("Error", "El nombre es obligatorio", "OK", null);
                return;
            }

            guardarMateria(name, teacher);
        });

        this.add(nameField);
        this.add(teacherField);
        this.add(saveButton);
    }

    private void guardarMateria(String name, String teacher) {
        // 1. Obtener ID del usuario de la sesión
        Long userId = UserSession.getInstance().getId();

        // 2. Preparar conexión
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/subjects/user/" + userId);
        request.setPost(true);
        request.setHttpMethod("POST");
        request.setContentType("application/json");

        // 3. Crear JSON
        // Nota: Enviamos un color por defecto (#336699) y nota mínima (3.0)
        String jsonBody = "{" +
                "\"name\":\"" + name + "\"," +
                "\"teacherName\":\"" + teacher + "\"," +
                "\"minPassingGrade\":3.0," +
                "\"colorCode\":\"#336699\"" +
                "}";

        request.setRequestBody(jsonBody);

        request.addResponseListener(e -> {
            if(request.getResponseCode() == 200) {
                // Si guardó bien, mostramos mensaje y volvemos al Dashboard
                // PERO: Creamos un Dashboard NUEVO para que recargue la lista
                Dialog.show("Éxito", "Materia creada correctamente", "OK", null);
                new DashboardForm().show();
            } else {
                Dialog.show("Error", "No se pudo guardar", "OK", null);
            }
        });

        NetworkManager.getInstance().addToQueue(request);
    }
}