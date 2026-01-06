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
    private Container tasksContainer;

    public SubjectDetailForm(Form previous, Long subjectId, String subjectName) {
        super(subjectName, new BorderLayout());
        this.subjectId = subjectId;

        getToolbar().setBackCommand("", e -> previous.showBack());

        Tabs tabs = new Tabs();

        // --- TAB 1: TAREAS ---
        Container tabTareas = new Container(new BorderLayout());

        tasksContainer = new Container(BoxLayout.y());
        tasksContainer.setScrollableY(true);
        tasksContainer.setScrollVisible(true);

        tabTareas.add(BorderLayout.CENTER, tasksContainer);

        // --- TAB 2: NOTAS ---
        Container tabNotas = new Container(BoxLayout.y());
        Container headerNotas = new Container(BoxLayout.x());
        Label lblNotas = new Label("Tus Notas");
        lblNotas.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        Button addNotaBtn = new Button("+");
        addNotaBtn.getAllStyles().setMarginLeft(200);
        addNotaBtn.addActionListener(e -> new AddNoteForm(this, subjectId, subjectName).show());
        headerNotas.add(lblNotas);
        headerNotas.add(addNotaBtn);
        
        Container notesContainer = new Container(BoxLayout.y());
        notesContainer.setScrollableY(true);
        cargarNotas(subjectId, notesContainer);
        tabNotas.add(headerNotas);
        tabNotas.add(notesContainer);

        // --- TAB 3: ARCHIVOS ---
        Container tabArchivos = new Container(BoxLayout.y());
        Container headerArchivos = new Container(BoxLayout.x());
        Label lblArchivos = new Label("Archivos de la Materia");
        lblArchivos.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        Button addArchivoBtn = new Button("+");
        addArchivoBtn.getAllStyles().setMarginLeft(150);
        addArchivoBtn.addActionListener(e -> new UploadFileForm(this, subjectId, subjectName).show());
        headerArchivos.add(lblArchivos);
        headerArchivos.add(addArchivoBtn);
        
        Container filesContainer = new Container(BoxLayout.y());
        filesContainer.setScrollableY(true);
        cargarArchivos(subjectId, filesContainer);
        tabArchivos.add(headerArchivos);
        tabArchivos.add(filesContainer);

        tabs.addTab("Tareas", tabTareas);
        tabs.addTab("Notas", tabNotas);
        tabs.addTab("Archivos", tabArchivos);

        this.add(BorderLayout.CENTER, tabs);

        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
        fab.bindFabToContainer(this);
        fab.addActionListener(e -> {
            new AddTaskForm(this, this.subjectId, getTitle()).show();
        });
    }

    private void cargarTareas() {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/tasks/subject/" + this.subjectId);
        request.setHttpMethod("GET");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                try {
                    byte[] data = request.getResponseData();
                    JSONParser parser = new JSONParser();
                    Map<String, Object> response = parser.parseJSON(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));

                    ArrayList<Map<String, Object>> listaTareas = (ArrayList<Map<String, Object>>) response.get("root");

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
                    callSerially(() -> ToastBar.showErrorMessage("Error al leer datos"));
                }
            } else {
                callSerially(() -> ToastBar.showErrorMessage("Error de conexión: " + request.getResponseCode()));
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    // --- MODIFICADO: AHORA INCLUYE CHECKBOX Y LÓGICA DE COMPLETADO ---
    private Container crearItemTarea(Map<String, Object> data) {
        String titulo = (String) data.get("title");
        String fechaRaw = (String) data.get("dueDate");

        // 1. Extraer ID y Estado de forma segura
        Object idObj = data.get("id");
        Long taskId = Float.valueOf(idObj.toString()).longValue();
        
        // Convertir isCompleted de forma segura (puede venir como String o Boolean)
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

        // 2. Usamos BoxLayout.x (Horizontal) para poner Check a la izquierda
        Container item = new Container(BoxLayout.x());
        item.getAllStyles().setBgColor(0xFFFFFF);
        item.getAllStyles().setBgTransparency(255);
        item.getAllStyles().setPadding(15, 15, 15, 15);
        item.getAllStyles().setMargin(5, 5, 10, 10);
        item.getAllStyles().setBorder(Border.createLineBorder(1, 0xDDDDDD));

        // 3. Crear CheckBox
        CheckBox check = new CheckBox();
        check.setSelected(isCompleted);

        // 4. Crear contenedor vertical para los textos (Título y Fecha)
        Container textos = new Container(BoxLayout.y());

        Label lblTitulo = new Label(titulo);
        // Estilo base
        lblTitulo.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));

        Label lblFecha = new Label("Entrega: " + fechaMostrar);
        lblFecha.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        lblFecha.getAllStyles().setFgColor(0x666666);

        // 5. Aplicar estilo visual si ya viene completada
        if (isCompleted) {
            lblTitulo.getAllStyles().setFgColor(0xAAAAAA); // Gris
            lblTitulo.getAllStyles().setStrikeThru(true);  // Tachado
        } else {
            lblTitulo.getAllStyles().setFgColor(0x000000); // Negro
            lblTitulo.getAllStyles().setStrikeThru(false); // Normal
        }

        // 6. Lógica al tocar el CheckBox
        check.addActionListener(e -> {
            toggleTarea(taskId, check, lblTitulo);
        });

        textos.add(lblTitulo);
        textos.add(lblFecha);

        item.add(check);
        item.add(textos);

        // 7. NUEVO: Agregar botón de eliminar a la derecha
        Button btnEliminar = new Button("🗑️");
        btnEliminar.getAllStyles().setMarginLeft(10);
        btnEliminar.getAllStyles().setPaddingLeft(0);
        btnEliminar.getAllStyles().setPaddingRight(0);
        btnEliminar.addActionListener(e -> {
            // Confirmar antes de eliminar
            Command yes = new Command("Eliminar");
            Command no = new Command("Cancelar");
            Command[] cmds = {yes, no};
            
            Command result = Dialog.show("Confirmar", "¿Eliminar esta tarea?", cmds);
            if (result == yes) {
                eliminarTarea(taskId);
            }
        });
        item.add(btnEliminar);

        return item;
    }

    // --- NUEVO MÉTODO: ELIMINAR TAREA ---
    private void eliminarTarea(Long taskId) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/tasks/" + taskId);
        request.setPost(false); // No es POST
        request.setHttpMethod("DELETE");

        request.addResponseListener(e -> {
            if(request.getResponseCode() == 200) {
                // Éxito: Recargar la lista de tareas
                callSerially(() -> {
                    ToastBar.showInfoMessage("Tarea eliminada");
                    cargarTareas();
                });
            } else {
                callSerially(() -> {
                    ToastBar.showErrorMessage("Error al eliminar tarea");
                });
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    // --- NUEVO MÉTODO: CONECTA CON EL BACKEND AL HACER CLIC ---
    private void toggleTarea(Long taskId, CheckBox check, Label lblTitulo) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/tasks/" + taskId + "/toggle");
        request.setPost(true);
        request.setHttpMethod("PUT");

        request.addResponseListener(e -> {
            if(request.getResponseCode() == 200) {
                // Éxito: Actualizamos visualmente
                boolean estaCompleta = check.isSelected();

                if(estaCompleta) {
                    lblTitulo.getAllStyles().setFgColor(0xAAAAAA);
                    lblTitulo.getAllStyles().setStrikeThru(true);
                } else {
                    lblTitulo.getAllStyles().setFgColor(0x000000);
                    lblTitulo.getAllStyles().setStrikeThru(false);
                }
                lblTitulo.repaint(); // Forzar repintado inmediato
            } else {
                // Si falla, revertimos el check visualmente
                check.setSelected(!check.isSelected());
                ToastBar.showErrorMessage("Error al actualizar tarea");
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    @Override
    protected void onShowCompleted() {
        super.onShowCompleted();
        cargarTareas();
    }

    // --- MÉTODO PARA CARGAR NOTAS ---
    private void cargarNotas(Long subjectId, Container container) {
        UserSession session = UserSession.getInstance();
        
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/notes/subject/" + subjectId + "/user/" + session.getId());
        request.setPost(false);
        request.setHttpMethod("GET");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                try {
                    byte[] data = request.getResponseData();
                    if (data != null && data.length > 0) {
                        JSONParser parser = new JSONParser();
                        Map<String, Object> response = parser.parseJSON(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));

                        ArrayList<Map<String, Object>> listaNotas = (ArrayList<Map<String, Object>>) response.get("root");

                        callSerially(() -> {
                            container.removeAll();

                            if (listaNotas != null && !listaNotas.isEmpty()) {
                                for (Map<String, Object> notaData : listaNotas) {
                                    container.add(crearTarjetaNota(notaData));
                                }
                            } else {
                                Label emptyLabel = new Label("No tienes notas en esta materia");
                                emptyLabel.getAllStyles().setAlignment(Component.CENTER);
                                emptyLabel.getAllStyles().setFgColor(0x999999);
                                container.add(emptyLabel);
                            }

                            container.animateLayout(200);
                        });
                    } else {
                        callSerially(() -> {
                            container.removeAll();
                            Label emptyLabel = new Label("No tienes notas en esta materia");
                            emptyLabel.getAllStyles().setAlignment(Component.CENTER);
                            container.add(emptyLabel);
                        });
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    callSerially(() -> {
                        container.removeAll();
                        Label errorLabel = new Label("Error al cargar notas");
                        errorLabel.getAllStyles().setFgColor(0xFF0000);
                        container.add(errorLabel);
                    });
                }
            } else if (request.getResponseCode() == 404) {
                callSerially(() -> {
                    container.removeAll();
                    Label emptyLabel = new Label("No tienes notas en esta materia");
                    emptyLabel.getAllStyles().setAlignment(Component.CENTER);
                    container.add(emptyLabel);
                });
            } else {
                callSerially(() -> {
                    container.removeAll();
                    Label errorLabel = new Label("Error de conexión: " + request.getResponseCode());
                    errorLabel.getAllStyles().setFgColor(0xFF0000);
                    container.add(errorLabel);
                });
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

        tarjeta.add(titulo);
        tarjeta.add(preview_label);
        tarjeta.add(fecha);

        return tarjeta;
    }

    // --- MÉTODO PARA CARGAR ARCHIVOS ---
    private void cargarArchivos(Long subjectId, Container container) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/files/subject/" + subjectId);
        request.setPost(false);
        request.setHttpMethod("GET");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                try {
                    byte[] data = request.getResponseData();
                    if (data != null && data.length > 0) {
                        JSONParser parser = new JSONParser();
                        Map<String, Object> response = parser.parseJSON(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));

                        ArrayList<Map<String, Object>> listaArchivos = (ArrayList<Map<String, Object>>) response.get("root");

                        callSerially(() -> {
                            container.removeAll();

                            if (listaArchivos != null && !listaArchivos.isEmpty()) {
                                for (Map<String, Object> archivoData : listaArchivos) {
                                    container.add(crearTarjetaArchivo(archivoData));
                                }
                            } else {
                                Label emptyLabel = new Label("No hay archivos en esta materia");
                                emptyLabel.getAllStyles().setAlignment(Component.CENTER);
                                emptyLabel.getAllStyles().setFgColor(0x999999);
                                container.add(emptyLabel);
                            }

                            container.animateLayout(200);
                        });
                    } else {
                        callSerially(() -> {
                            container.removeAll();
                            Label emptyLabel = new Label("No hay archivos en esta materia");
                            emptyLabel.getAllStyles().setAlignment(Component.CENTER);
                            container.add(emptyLabel);
                        });
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    callSerially(() -> {
                        container.removeAll();
                        Label errorLabel = new Label("Error al cargar archivos");
                        errorLabel.getAllStyles().setFgColor(0xFF0000);
                        container.add(errorLabel);
                    });
                }
            } else if (request.getResponseCode() == 404) {
                callSerially(() -> {
                    container.removeAll();
                    Label emptyLabel = new Label("No hay archivos en esta materia");
                    emptyLabel.getAllStyles().setAlignment(Component.CENTER);
                    container.add(emptyLabel);
                });
            } else {
                callSerially(() -> {
                    container.removeAll();
                    Label errorLabel = new Label("Error de conexión: " + request.getResponseCode());
                    errorLabel.getAllStyles().setFgColor(0xFF0000);
                    container.add(errorLabel);
                });
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    private Container crearTarjetaArchivo(Map<String, Object> archivoData) {
        Container tarjeta = new Container(BoxLayout.y());
        tarjeta.getAllStyles().setPadding(5, 5, 5, 5);
        tarjeta.getAllStyles().setMargin(5, 5, 5, 5);
        tarjeta.getAllStyles().setBorder(Border.createRoundBorder(5, 5, 0xFF2196F3));

        Label nombre = new Label((String) archivoData.get("fileName"));
        nombre.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));

        Long fileSize = ((Number) archivoData.get("fileSize")).longValue();
        String fileSizeStr = formatearTamano(fileSize);
        Label tamaño = new Label("Tamaño: " + fileSizeStr);

        String tipo = (String) archivoData.get("fileType");
        Label tipo_label = new Label("Tipo: " + tipo);

        Label fecha = new Label("Subido: " + archivoData.get("uploadedAt"));
        fecha.getAllStyles().setFgColor(0xFF888888);

        tarjeta.add(nombre);
        tarjeta.add(tamaño);
        tarjeta.add(tipo_label);
        tarjeta.add(fecha);

        return tarjeta;
    }

    private String formatearTamano(Long bytes) {
        if (bytes <= 0) return "0 B";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)) + " MB";
        if (bytes < 1024L * 1024 * 1024 * 1024) return (bytes / (1024 * 1024 * 1024)) + " GB";
        return (bytes / (1024L * 1024 * 1024 * 1024)) + " TB";
    }
}