package com.sophiezp;

import com.codename1.components.FloatingActionButton;
import com.codename1.io.*;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.Style;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class DashboardForm extends Form {

    public DashboardForm() {
        super("Mis Materias", BoxLayout.y());

        // Set background color
        this.getAllStyles().setBgColor(0xF5F5F5); // Light gray
        this.getAllStyles().setBgTransparency(255);

        UserSession session = UserSession.getInstance();
        Label welcomeLabel = new Label("Hola, " + session.getFullName());
        welcomeLabel.getAllStyles().setFgColor(0x333333);
        welcomeLabel.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        welcomeLabel.getAllStyles().setMarginBottom(20);
        this.add(welcomeLabel);

        // --- BOTONES EN EL TOOLBAR ---
        Container toolbarContainer = new Container(BoxLayout.x());
        
        Button notificationsBtn = new Button("🔔");
        notificationsBtn.addActionListener(e -> new NotificationsForm(this).show());
        
        Button logoutBtn = new Button("Cerrar Sesión");
        logoutBtn.addActionListener(e -> {
            // Confirmar antes de cerrar sesión
            Command yes = new Command("Sí, cerrar sesión");
            Command no = new Command("Cancelar");
            Command[] cmds = {yes, no};
            
            Command result = Dialog.show("Confirmar", "¿Deseas cerrar sesión?", cmds);
            if (result == yes) {
                UserSession.getInstance().closeSession();
                new LoginScreen().show();
            }
        });
        
        toolbarContainer.add(notificationsBtn);
        toolbarContainer.add(logoutBtn);
        this.add(toolbarContainer);

        // Contenedor para la lista
        Container materiasContainer = new Container(BoxLayout.y());
        // IMPORTANTE: Quitamos el scroll del contenedor interno para que use el scroll del Formulario
        // materiasContainer.setScrollableY(true);
        this.add(materiasContainer);

        System.out.println("--- INICIANDO DASHBOARD PARA ID: " + session.getId() + " ---");

        if (session.getId() != null) {
            cargarMaterias(session.getId(), materiasContainer);
        } else {
            materiasContainer.add(new Label("Error: ID de usuario nulo"));
        }

        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
        fab.bindFabToContainer(this);
        fab.addActionListener(e -> new AddSubjectForm(this).show());
    }

    private void cargarMaterias(Long userId, Container container) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/subjects/user/" + userId);
        request.setPost(false);
        request.setHttpMethod("GET");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                try {
                    byte[] data = request.getResponseData();
                    JSONParser parser = new JSONParser();
                    Map<String, Object> response = parser.parseJSON(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));

                    // Codename One envuelve las listas en una llave "root"
                    ArrayList<Map<String, Object>> listaMaterias = (ArrayList<Map<String, Object>>) response.get("root");

                    container.removeAll();

                    if (listaMaterias != null && !listaMaterias.isEmpty()) {
                        System.out.println("Materias encontradas: " + listaMaterias.size());

                        for (Map<String, Object> materiaData : listaMaterias) {
                            // Imprimir nombre para verificar
                            System.out.println("Renderizando materia: " + materiaData.get("name"));
                            container.add(crearTarjetaMateria(materiaData));
                        }
                    } else {
                        System.out.println("La lista de materias llegó vacía");
                        container.add(new Label("No tienes materias inscritas"));
                    }

                    // Refrescar el diseño visual
                    this.revalidate();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    container.add(new Label("Error leyendo datos"));
                }
            } else {
                System.out.println("Error del servidor: " + request.getResponseCode());
                container.add(new Label("Error conexión: " + request.getResponseCode()));
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    // Método auxiliar para diseñar la tarjeta de la materia
    private Container crearTarjetaMateria(Map<String, Object> data) {
        // Validar nulos
        String nombre = data.get("name") != null ? (String) data.get("name") : "Sin Nombre";
        String profesor = data.get("teacherName") != null ? (String) data.get("teacherName") : "Sin Profesor";
        String colorCode = (String) data.get("colorCode");

        // Parse color, default to white if invalid
        int bgColor = 0xFFFFFF;
        try {
            if (colorCode != null && colorCode.startsWith("#")) {
                bgColor = Integer.parseInt(colorCode.substring(1), 16);
            }
        } catch (Exception e) {
            bgColor = 0xFFFFFF;
        }

        Container card = new Container(BoxLayout.y());
        card.getAllStyles().setBgColor(bgColor);
        card.getAllStyles().setBgTransparency(255);
        card.getAllStyles().setMargin(15, 15, 15, 15);
        card.getAllStyles().setPadding(20, 20, 20, 20);

        Style s = card.getAllStyles();
        s.setBorder(Border.createLineBorder(2, 0x333333));

        // --- CAMBIO CLAVE AQUÍ ---
        // En lugar de Label, usamos Button.
        Button btnNombre = new Button(nombre);

        // Le quitamos el estilo de botón para que parezca un Label normal
        // (Sin bordes, sin fondo gris, alineado a la izquierda)
        btnNombre.setUIID("Label");
        btnNombre.getAllStyles().setFgColor(0x000000); // Texto negro
        btnNombre.getAllStyles().setAlignment(Component.LEFT);

        Label lblProfe = new Label(profesor);
        lblProfe.getAllStyles().setFgColor(0x888888);
        lblProfe.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL));

        card.add(btnNombre);
        card.add(lblProfe);

        // leadComponent transfiere el clic de la tarjeta al botón
        card.setLeadComponent(btnNombre);

        // AHORA SÍ funcionará el ActionListener porque es un Botón
        btnNombre.addActionListener(evt -> {
            System.out.println("Entrando a materia: " + nombre);

            Object idObj = data.get("id");
            Long subjectId = 0L;
            if (idObj != null) {
                subjectId = Float.valueOf(idObj.toString()).longValue();
            }

            new SubjectDetailForm(this, subjectId, nombre).show();
        });

        return card;
    }
}