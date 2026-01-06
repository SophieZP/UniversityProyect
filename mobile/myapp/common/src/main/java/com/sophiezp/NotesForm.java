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

public class NotesForm extends Form {

    private Long subjectId;
    private String subjectName;
    private Form previousScreen;

    public NotesForm(Form previous, Long subjectId, String subjectName) {
        super("Notas - " + subjectName, BoxLayout.y());

        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.previousScreen = previous;

        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        // Contenedor para la lista de notas
        Container notesContainer = new Container(BoxLayout.y());
        this.add(notesContainer);

        UserSession session = UserSession.getInstance();

        // Cargar notas desde el servidor
        cargarNotas(session.getId(), notesContainer);

        // FAB para agregar nueva nota
        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
        fab.bindFabToContainer(this);
        fab.addActionListener(e -> new AddNoteForm(this, subjectId, subjectName).show());
    }

    private void cargarNotas(Long userId, Container container) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/notes/subject/" + subjectId + "/user/" + userId);
        request.setPost(false);
        request.setHttpMethod("GET");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                try {
                    byte[] data = request.getResponseData();
                    JSONParser parser = new JSONParser();
                    Map<String, Object> response = parser.parseJSON(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));

                    ArrayList<Map<String, Object>> listaNotas = (ArrayList<Map<String, Object>>) response.get("root");

                    container.removeAll();

                    if (listaNotas != null && !listaNotas.isEmpty()) {
                        for (Map<String, Object> notaData : listaNotas) {
                            container.add(crearTarjetaNota(notaData));
                        }
                    } else {
                        container.add(new Label("No tienes notas en esta materia"));
                    }

                    this.revalidate();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    container.add(new Label("Error cargando notas"));
                }
            } else {
                container.add(new Label("Error conexión: " + request.getResponseCode()));
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    private Container crearTarjetaNota(Map<String, Object> notaData) {
        Container tarjeta = new Container(BoxLayout.y());
        tarjeta.getAllStyles().setPadding(5, 5, 5, 5);
        tarjeta.getAllStyles().setMargin(5, 5, 5, 5);
        tarjeta.getAllStyles().setBorder(Border.createRoundBorder(5, 5, 0xFF9C27B0));

        Label titulo = new Label((String) notaData.get("title"));
        titulo.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));

        String content = (String) notaData.get("content");
        String preview = content.length() > 100 ? content.substring(0, 100) + "..." : content;
        Label preview_label = new Label(preview);

        Label fecha = new Label("Creada: " + notaData.get("createdAt"));
        fecha.getAllStyles().setFgColor(0xFF888888);
        fecha.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));

        Button editBtn = new Button("Editar");
        Button deleteBtn = new Button("Eliminar");

        Long noteId = ((Number) notaData.get("id")).longValue();

        editBtn.addActionListener(e -> {
            new EditNoteForm(this, noteId, (String) notaData.get("title"), content).show();
        });

        deleteBtn.addActionListener(e -> {
            Command yes = new Command("Sí, eliminar");
            Command no = new Command("Cancelar");
            Command[] cmds = {yes, no};
            Command result = Dialog.show("Confirmar", "¿Eliminar esta nota?", cmds);
            if (result == yes) {
                eliminarNota(noteId);
            }
        });

        tarjeta.add(titulo);
        tarjeta.add(preview_label);
        tarjeta.add(fecha);

        Container buttonContainer = new Container(BoxLayout.x());
        buttonContainer.add(editBtn);
        buttonContainer.add(deleteBtn);
        tarjeta.add(buttonContainer);

        return tarjeta;
    }

    private void eliminarNota(Long noteId) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/notes/" + noteId);
        request.setPost(false);
        request.setHttpMethod("DELETE");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                Dialog.show("Éxito", "Nota eliminada", "OK", null);
                this.show();
            } else {
                Dialog.show("Error", "No se pudo eliminar", "OK", null);
            }
        });

        NetworkManager.getInstance().addToQueue(request);
    }
}
