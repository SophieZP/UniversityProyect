package com.sophiezp;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkManager;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;

public class EditNoteForm extends Form {

    public EditNoteForm(Form previousScreen, Long noteId, String titulo, String contenido) {
        super("Editar Nota", BoxLayout.y());

        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        TextField titleField = new TextField(titulo, "Título de la nota", 20, TextField.ANY);
        TextArea contentArea = new TextArea(5, 20);
        contentArea.setText(contenido);

        Button updateButton = new Button("Actualizar Nota");
        updateButton.getAllStyles().setMarginTop(20);

        updateButton.addActionListener(e -> {
            String title = titleField.getText();
            String content = contentArea.getText();

            if (title.isEmpty() || content.isEmpty()) {
                Dialog.show("Error", "Todos los campos son obligatorios", "OK", null);
                return;
            }

            actualizarNota(noteId, title, content, previousScreen);
        });

        this.add(titleField);
        this.add(new Label("Contenido:"));
        this.add(contentArea);
        this.add(updateButton);
    }

    private void actualizarNota(Long noteId, String title, String content, Form previous) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/notes/" + noteId);
        request.setPost(true);
        request.setHttpMethod("PUT");
        request.setContentType("application/json");

        String jsonBody = "{" +
                "\"title\":\"" + title.replace("\"", "\\\"") + "\"," +
                "\"content\":\"" + content.replace("\"", "\\\"").replace("\n", "\\n") + "\"" +
                "}";

        request.setRequestBody(jsonBody);

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                Dialog.show("Éxito", "Nota actualizada", "OK", null);
                previous.show();
            } else {
                Dialog.show("Error", "No se pudo actualizar", "OK", null);
            }
        });

        NetworkManager.getInstance().addToQueue(request);
    }
}
