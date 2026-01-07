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

    private Container materiasContainer;

    public DashboardForm() {
        super("Mis Materias", BoxLayout.y());

        // Set background color
        this.getAllStyles().setBgColor(0xF5F5F5);
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

        // Contenedor para la lista de materias
        materiasContainer = new Container(BoxLayout.y());
        materiasContainer.setScrollableY(true);
        this.add(materiasContainer);

        // FAB para agregar materia
        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
        fab.bindFabToContainer(this);
        fab.addActionListener(e -> new AddSubjectForm(this).show());

        System.out.println("--- DASHBOARD CREADO PARA ID: " + session.getId() + " ---");
    }

    /**
     * ✅ CRÍTICO: Este método se llama CADA VEZ que se muestra el formulario
     * Así garantizamos que la lista de materias siempre esté actualizada
     */
    @Override
    protected void onShowCompleted() {
        super.onShowCompleted();

        UserSession session = UserSession.getInstance();
        if (session.getId() != null) {
            System.out.println("--- RECARGANDO MATERIAS ---");
            cargarMaterias(session.getId());
        } else {
            materiasContainer.removeAll();
            materiasContainer.add(new Label("Error: ID de usuario nulo"));
            this.revalidate();
        }
    }

    private void cargarMaterias(Long userId) {
        // Mostrar indicador de carga
        materiasContainer.removeAll();
        Label loadingLabel = new Label("Cargando materias...");
        loadingLabel.getAllStyles().setAlignment(Component.CENTER);
        materiasContainer.add(loadingLabel);
        this.revalidate();

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

                    // Extraer el array de materias
                    Object dataField = response.get("data");
                    ArrayList<Map<String, Object>> listaMaterias = null;

                    if (dataField instanceof ArrayList) {
                        listaMaterias = (ArrayList<Map<String, Object>>) dataField;
                    } else if (response.containsKey("root")) {
                        listaMaterias = (ArrayList<Map<String, Object>>) response.get("root");
                    }

                    materiasContainer.removeAll();

                    if (listaMaterias != null && !listaMaterias.isEmpty()) {
                        System.out.println("✅ Materias encontradas: " + listaMaterias.size());

                        for (Map<String, Object> materiaData : listaMaterias) {
                            System.out.println("Renderizando materia: " + materiaData.get("name"));
                            materiasContainer.add(crearTarjetaMateria(materiaData));
                        }
                    } else {
                        System.out.println("⚠️ La lista de materias está vacía");
                        Label emptyLabel = new Label("No tienes materias inscritas");
                        emptyLabel.getAllStyles().setAlignment(Component.CENTER);
                        emptyLabel.getAllStyles().setFgColor(0x999999);
                        emptyLabel.getAllStyles().setMarginTop(50);
                        materiasContainer.add(emptyLabel);
                    }

                    this.revalidate();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    materiasContainer.removeAll();
                    Label errorLabel = new Label("Error al cargar materias: " + ex.getMessage());
                    errorLabel.getAllStyles().setFgColor(0xFF0000);
                    materiasContainer.add(errorLabel);
                    this.revalidate();
                }
            } else {
                System.out.println("❌ Error del servidor: " + request.getResponseCode());
                materiasContainer.removeAll();
                Label errorLabel = new Label("Error de conexión: " + request.getResponseCode());
                errorLabel.getAllStyles().setFgColor(0xFF0000);
                materiasContainer.add(errorLabel);
                this.revalidate();
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    private Container crearTarjetaMateria(Map<String, Object> data) {
        String nombre = data.get("name") != null ? (String) data.get("name") : "Sin Nombre";
        String profesor = data.get("teacherName") != null ? (String) data.get("teacherName") : "Sin Profesor";
        String colorCode = (String) data.get("colorCode");

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

        Button btnNombre = new Button(nombre);
        btnNombre.setUIID("Label");
        btnNombre.getAllStyles().setFgColor(0x000000);
        btnNombre.getAllStyles().setAlignment(Component.LEFT);

        Label lblProfe = new Label(profesor);
        lblProfe.getAllStyles().setFgColor(0x888888);
        lblProfe.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL));

        card.add(btnNombre);
        card.add(lblProfe);

        card.setLeadComponent(btnNombre);

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