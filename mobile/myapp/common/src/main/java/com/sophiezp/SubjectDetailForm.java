package com.sophiezp;

import com.codename1.components.FloatingActionButton;
import com.codename1.components.ToastBar;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkManager;
import com.codename1.io.JSONParser;
import com.codename1.ui.plaf.Border;
import static com.codename1.ui.CN.*;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class SubjectDetailForm extends Form {

    private Long subjectId;
    private String subjectName;
    private Container tasksContainer;
    private Container notesContainer;
    private Container filesContainer;
    private Container evaluationsContainer;
    private int currentTab = 0; // Para saber en qué pestaña estamos

    public SubjectDetailForm(Form previous, Long subjectId, String subjectName) {
        super(subjectName, new BorderLayout());
        this.subjectId = subjectId;
        this.subjectName = subjectName;

        getToolbar().setBackCommand("", e -> previous.showBack());

        Tabs tabs = new Tabs();

        // --- TAB 1: TAREAS ---
        Container tabTareas = new Container(new BorderLayout());
        tasksContainer = new Container(BoxLayout.y());
        tasksContainer.setScrollableY(true);
        tasksContainer.setScrollVisible(true);
        tabTareas.add(BorderLayout.CENTER, tasksContainer);

        // --- TAB 2: CALIFICACIONES (NUEVO) ---
        Container tabCalificaciones = new Container(new BorderLayout());
        evaluationsContainer = new Container(BoxLayout.y());
        evaluationsContainer.setScrollableY(true);
        evaluationsContainer.setScrollVisible(true);
        tabCalificaciones.add(BorderLayout.CENTER, evaluationsContainer);

        // --- TAB 3: NOTAS ---
        Container tabNotas = new Container(new BorderLayout());
        notesContainer = new Container(BoxLayout.y());
        notesContainer.setScrollableY(true);
        notesContainer.setScrollVisible(true);
        tabNotas.add(BorderLayout.CENTER, notesContainer);

        // --- TAB 4: ARCHIVOS ---
        Container tabArchivos = new Container(new BorderLayout());
        filesContainer = new Container(BoxLayout.y());
        filesContainer.setScrollableY(true);
        filesContainer.setScrollVisible(true);
        tabArchivos.add(BorderLayout.CENTER, filesContainer);

        tabs.addTab("Tareas", tabTareas);
        tabs.addTab("Notas", tabCalificaciones);
        tabs.addTab("Apuntes", tabNotas);
        tabs.addTab("Archivos", tabArchivos);

        // Detectar cambios de pestaña
        tabs.addSelectionListener((i, ii) -> {
            currentTab = ii;
        });

        this.add(BorderLayout.CENTER, tabs);

        // ✅ FAB CON MENÚ CONTEXTUAL DINÁMICO
        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
        fab.bindFabToContainer(this);

        fab.addActionListener(e -> {
            Dialog menuDialog = new Dialog();
            menuDialog.setLayout(BoxLayout.y());
            menuDialog.setDialogUIID("Container");
            menuDialog.getAllStyles().setBgTransparency(0);

            // Opciones según la pestaña actual
            if (currentTab == 0) { // Tareas
                Button addTaskBtn = new Button("📝 Nueva Tarea");
                addTaskBtn.getAllStyles().setMargin(10, 10, 10, 10);
                addTaskBtn.addActionListener(ev -> {
                    menuDialog.dispose();
                    new AddTaskForm(this, subjectId, subjectName).show();
                });
                menuDialog.add(addTaskBtn);
            } else if (currentTab == 1) { // Calificaciones
                Button addEvalBtn = new Button("📊 Nueva Calificación");
                addEvalBtn.getAllStyles().setMargin(10, 10, 10, 10);
                addEvalBtn.addActionListener(ev -> {
                    menuDialog.dispose();
                    new AddEvaluationForm(this, subjectId, subjectName).show();
                });
                menuDialog.add(addEvalBtn);
            } else if (currentTab == 2) { // Notas
                Button addNoteBtn = new Button("📄 Nuevo Apunte");
                addNoteBtn.getAllStyles().setMargin(10, 10, 10, 10);
                addNoteBtn.addActionListener(ev -> {
                    menuDialog.dispose();
                    new AddNoteForm(this, subjectId, subjectName).show();
                });
                menuDialog.add(addNoteBtn);
            } else if (currentTab == 3) { // Archivos
                Button addFileBtn = new Button("📎 Subir Archivo");
                addFileBtn.getAllStyles().setMargin(10, 10, 10, 10);
                addFileBtn.addActionListener(ev -> {
                    menuDialog.dispose();
                    new UploadFileForm(this, subjectId, subjectName).show();
                });
                menuDialog.add(addFileBtn);
            }

            Button cancelBtn = new Button("Cancelar");
            cancelBtn.getAllStyles().setMargin(10, 10, 10, 10);
            cancelBtn.addActionListener(ev -> menuDialog.dispose());
            menuDialog.add(cancelBtn);

            menuDialog.show();
        });
    }

    @Override
    protected void onShowCompleted() {
        super.onShowCompleted();
        cargarTareas();
        cargarCalificaciones();
        cargarNotas();
        cargarArchivos();
    }

    // ========== TAREAS ==========
    private void cargarTareas() {
        tasksContainer.removeAll();
        Label loadingLabel = new Label("Cargando tareas...");
        loadingLabel.getAllStyles().setAlignment(Component.CENTER);
        tasksContainer.add(loadingLabel);

        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/tasks/subject/" + subjectId);
        request.setHttpMethod("GET");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                try {
                    byte[] data = request.getResponseData();
                    JSONParser parser = new JSONParser();
                    Map<String, Object> response = parser.parseJSON(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));

                    Object dataField = response.get("data");
                    final ArrayList<Map<String, Object>> listaTareas;

                    if (dataField instanceof ArrayList) {
                        listaTareas = (ArrayList<Map<String, Object>>) dataField;
                    } else if (response.containsKey("root")) {
                        listaTareas = (ArrayList<Map<String, Object>>) response.get("root");
                    } else {
                        listaTareas = null;
                    }

                    callSerially(() -> {
                        tasksContainer.removeAll();

                        if (listaTareas != null && !listaTareas.isEmpty()) {
                            for (Map<String, Object> taskData : listaTareas) {
                                tasksContainer.add(crearItemTarea(taskData));
                            }
                        } else {
                            Label emptyLabel = new Label("No hay tareas pendientes");
                            emptyLabel.getAllStyles().setAlignment(Component.CENTER);
                            emptyLabel.getAllStyles().setFgColor(0x999999);
                            emptyLabel.getAllStyles().setMarginTop(50);
                            tasksContainer.add(emptyLabel);
                        }

                        tasksContainer.animateLayout(200);
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    callSerially(() -> {
                        tasksContainer.removeAll();
                        tasksContainer.add(new Label("Error al cargar tareas"));
                    });
                }
            } else {
                callSerially(() -> {
                    tasksContainer.removeAll();
                    tasksContainer.add(new Label("Error de conexión: " + request.getResponseCode()));
                });
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    private Container crearItemTarea(Map<String, Object> data) {
        String titulo = (String) data.get("title");
        String fechaRaw = (String) data.get("dueDate");

        Object idObj = data.get("id");
        Long taskId = Float.valueOf(idObj.toString()).longValue();

        Object completedObj = data.get("isCompleted");
        boolean isCompleted = false;
        if (completedObj != null) {
            if (completedObj instanceof Boolean) {
                isCompleted = (Boolean) completedObj;
            } else if (completedObj instanceof String) {
                isCompleted = Boolean.parseBoolean((String) completedObj);
            }
        }

        String fechaMostrar = "Sin fecha";
        if (fechaRaw != null && fechaRaw.length() >= 10) {
            fechaMostrar = fechaRaw.substring(0, 10);
        }

        Container item = new Container(BoxLayout.x());
        item.getAllStyles().setBgColor(0xFFFFFF);
        item.getAllStyles().setBgTransparency(255);
        item.getAllStyles().setPadding(15, 15, 15, 15);
        item.getAllStyles().setMargin(5, 5, 10, 10);
        item.getAllStyles().setBorder(Border.createLineBorder(1, 0xDDDDDD));

        CheckBox check = new CheckBox();
        check.setSelected(isCompleted);

        Container textos = new Container(BoxLayout.y());

        Label lblTitulo = new Label(titulo);
        lblTitulo.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));

        Label lblFecha = new Label("Entrega: " + fechaMostrar);
        lblFecha.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        lblFecha.getAllStyles().setFgColor(0x666666);

        if (isCompleted) {
            lblTitulo.getAllStyles().setFgColor(0xAAAAAA);
            lblTitulo.getAllStyles().setStrikeThru(true);
        } else {
            lblTitulo.getAllStyles().setFgColor(0x000000);
            lblTitulo.getAllStyles().setStrikeThru(false);
        }

        check.addActionListener(ev -> toggleTarea(taskId, check, lblTitulo));

        textos.add(lblTitulo);
        textos.add(lblFecha);

        item.add(check);
        item.add(textos);

        Button btnEliminar = new Button("🗑️");
        btnEliminar.getAllStyles().setMarginLeft(10);
        btnEliminar.addActionListener(ev -> {
            Command yes = new Command("Eliminar");
            Command no = new Command("Cancelar");
            Command result = Dialog.show("Confirmar", "¿Eliminar esta tarea?", new Command[]{yes, no});
            if (result == yes) {
                eliminarTarea(taskId);
            }
        });
        item.add(btnEliminar);

        return item;
    }

    private void toggleTarea(Long taskId, CheckBox check, Label lblTitulo) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/tasks/" + taskId + "/toggle");
        request.setPost(true);
        request.setHttpMethod("PUT");

        request.addResponseListener(ev -> {
            if(request.getResponseCode() == 200) {
                boolean estaCompleta = check.isSelected();
                if(estaCompleta) {
                    lblTitulo.getAllStyles().setFgColor(0xAAAAAA);
                    lblTitulo.getAllStyles().setStrikeThru(true);
                } else {
                    lblTitulo.getAllStyles().setFgColor(0x000000);
                    lblTitulo.getAllStyles().setStrikeThru(false);
                }
                lblTitulo.repaint();
            } else {
                check.setSelected(!check.isSelected());
                ToastBar.showErrorMessage("Error al actualizar tarea");
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    private void eliminarTarea(Long taskId) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/tasks/" + taskId);
        request.setPost(false);
        request.setHttpMethod("DELETE");

        request.addResponseListener(ev -> {
            if(request.getResponseCode() == 200) {
                callSerially(() -> {
                    ToastBar.showInfoMessage("Tarea eliminada");
                    cargarTareas();
                });
            } else {
                callSerially(() -> ToastBar.showErrorMessage("Error al eliminar tarea"));
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    // ========== CALIFICACIONES (NUEVO) ==========
    private void cargarCalificaciones() {
        evaluationsContainer.removeAll();

        // Botón para ver detalles completos
        Button verDetallesBtn = new Button("📊 Ver Calificaciones Completas");
        verDetallesBtn.getAllStyles().setBgColor(0x2196F3);
        verDetallesBtn.getAllStyles().setFgColor(0xFFFFFF);
        verDetallesBtn.getAllStyles().setPadding(15, 15, 15, 15);
        verDetallesBtn.getAllStyles().setMargin(10, 10, 10, 10);
        verDetallesBtn.addActionListener(ev -> {
            new EvaluationsForm(this, subjectId, subjectName).show();
        });

        evaluationsContainer.add(verDetallesBtn);

        Label infoLabel = new Label("Aquí podrás gestionar tus calificaciones y ver tu promedio acumulado");
        infoLabel.getAllStyles().setAlignment(Component.CENTER);
        infoLabel.getAllStyles().setFgColor(0x666666);
        infoLabel.getAllStyles().setMarginTop(20);
        evaluationsContainer.add(infoLabel);
    }

    // ========== NOTAS ==========
    private void cargarNotas() {
        notesContainer.removeAll();
        Label loadingLabel = new Label("Cargando apuntes...");
        loadingLabel.getAllStyles().setAlignment(Component.CENTER);
        notesContainer.add(loadingLabel);

        UserSession session = UserSession.getInstance();

        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/notes/subject/" + subjectId + "/user/" + session.getId());
        request.setPost(false);
        request.setHttpMethod("GET");

        request.addResponseListener(ev -> {
            if (request.getResponseCode() == 200) {
                try {
                    byte[] data = request.getResponseData();
                    if (data != null && data.length > 0) {
                        JSONParser parser = new JSONParser();
                        Map<String, Object> response = parser.parseJSON(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));

                        Object dataField = response.get("data");
                        final ArrayList<Map<String, Object>> listaNotas;

                        if (dataField instanceof ArrayList) {
                            listaNotas = (ArrayList<Map<String, Object>>) dataField;
                        } else if (response.containsKey("root")) {
                            listaNotas = (ArrayList<Map<String, Object>>) response.get("root");
                        } else {
                            listaNotas = null;
                        }

                        callSerially(() -> {
                            notesContainer.removeAll();

                            if (listaNotas != null && !listaNotas.isEmpty()) {
                                for (Map<String, Object> notaData : listaNotas) {
                                    notesContainer.add(crearTarjetaNota(notaData));
                                }
                            } else {
                                Label emptyLabel = new Label("No tienes apuntes en esta materia");
                                emptyLabel.getAllStyles().setAlignment(Component.CENTER);
                                emptyLabel.getAllStyles().setFgColor(0x999999);
                                notesContainer.add(emptyLabel);
                            }

                            notesContainer.animateLayout(200);
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    callSerially(() -> {
                        notesContainer.removeAll();
                        notesContainer.add(new Label("Error al cargar apuntes"));
                    });
                }
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    private Container crearTarjetaNota(Map<String, Object> notaData) {
        Container tarjeta = new Container(BoxLayout.y());
        tarjeta.getAllStyles().setPadding(10, 10, 10, 10);
        tarjeta.getAllStyles().setMargin(5, 5, 5, 5);
        tarjeta.getAllStyles().setBorder(Border.createRoundBorder(5, 5, 0xFF9C27B0));
        tarjeta.getAllStyles().setBgColor(0xFFFFFF);
        tarjeta.getAllStyles().setBgTransparency(255);

        Label titulo = new Label((String) notaData.get("title"));
        titulo.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));

        String content = (String) notaData.get("content");
        String preview = content.length() > 100 ? content.substring(0, 100) + "..." : content;
        Label preview_label = new Label(preview);

        tarjeta.add(titulo);
        tarjeta.add(preview_label);

        return tarjeta;
    }

    // ========== ARCHIVOS ==========
    private void cargarArchivos() {
        filesContainer.removeAll();
        Label loadingLabel = new Label("Cargando archivos...");
        loadingLabel.getAllStyles().setAlignment(Component.CENTER);
        filesContainer.add(loadingLabel);

        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/files/subject/" + subjectId);
        request.setPost(false);
        request.setHttpMethod("GET");

        request.addResponseListener(ev -> {
            if (request.getResponseCode() == 200) {
                try {
                    byte[] data = request.getResponseData();
                    if (data != null && data.length > 0) {
                        JSONParser parser = new JSONParser();
                        Map<String, Object> response = parser.parseJSON(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));

                        Object dataField = response.get("data");
                        final ArrayList<Map<String, Object>> listaArchivos;

                        if (dataField instanceof ArrayList) {
                            listaArchivos = (ArrayList<Map<String, Object>>) dataField;
                        } else if (response.containsKey("root")) {
                            listaArchivos = (ArrayList<Map<String, Object>>) response.get("root");
                        } else {
                            listaArchivos = null;
                        }

                        callSerially(() -> {
                            filesContainer.removeAll();

                            if (listaArchivos != null && !listaArchivos.isEmpty()) {
                                for (Map<String, Object> archivoData : listaArchivos) {
                                    filesContainer.add(crearTarjetaArchivo(archivoData));
                                }
                            } else {
                                Label emptyLabel = new Label("No hay archivos en esta materia");
                                emptyLabel.getAllStyles().setAlignment(Component.CENTER);
                                emptyLabel.getAllStyles().setFgColor(0x999999);
                                filesContainer.add(emptyLabel);
                            }

                            filesContainer.animateLayout(200);
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    callSerially(() -> {
                        filesContainer.removeAll();
                        filesContainer.add(new Label("Error al cargar archivos"));
                    });
                }
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    private Container crearTarjetaArchivo(Map<String, Object> archivoData) {
        Container tarjeta = new Container(BoxLayout.y());
        tarjeta.getAllStyles().setPadding(10, 10, 10, 10);
        tarjeta.getAllStyles().setMargin(5, 5, 5, 5);
        tarjeta.getAllStyles().setBorder(Border.createRoundBorder(5, 5, 0xFF2196F3));
        tarjeta.getAllStyles().setBgColor(0xFFFFFF);
        tarjeta.getAllStyles().setBgTransparency(255);

        Label nombre = new Label((String) archivoData.get("fileName"));
        nombre.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));

        String tipo = (String) archivoData.get("fileType");
        Label tipo_label = new Label("Tipo: " + tipo);

        tarjeta.add(nombre);
        tarjeta.add(tipo_label);

        return tarjeta;
    }
}