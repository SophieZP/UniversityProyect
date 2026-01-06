package com.sophiezp;

import com.codename1.io.*;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Border;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class NotificationsForm extends Form {

    private Form previousScreen;

    public NotificationsForm(Form previous) {
        super("Mis Notificaciones", BoxLayout.y());

        this.previousScreen = previous;

        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        // Contenedor para la lista de notificaciones
        Container notificationsContainer = new Container(BoxLayout.y());
        this.add(notificationsContainer);

        // Botón para marcar todas como leídas
        Button markAllReadBtn = new Button("Marcar todas como leídas");
        markAllReadBtn.addActionListener(e -> marcarTodasLeidas());

        this.add(markAllReadBtn);

        UserSession session = UserSession.getInstance();

        // Cargar notificaciones
        cargarNotificaciones(session.getId(), notificationsContainer);
    }

    private void cargarNotificaciones(Long userId, Container container) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/notifications/user/" + userId);
        request.setPost(false);
        request.setHttpMethod("GET");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                try {
                    byte[] data = request.getResponseData();
                    JSONParser parser = new JSONParser();
                    Map<String, Object> response = parser.parseJSON(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"));

                    ArrayList<Map<String, Object>> listaNotificaciones = (ArrayList<Map<String, Object>>) response.get("root");

                    container.removeAll();

                    if (listaNotificaciones != null && !listaNotificaciones.isEmpty()) {
                        for (Map<String, Object> notifData : listaNotificaciones) {
                            container.add(crearTarjetaNotificacion(notifData, container));
                        }
                    } else {
                        container.add(new Label("No tienes notificaciones"));
                    }

                    this.revalidate();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    container.add(new Label("Error cargando notificaciones"));
                }
            } else {
                container.add(new Label("Error conexión: " + request.getResponseCode()));
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    private Container crearTarjetaNotificacion(Map<String, Object> notifData, Container parent) {
        Container tarjeta = new Container(BoxLayout.y());
        tarjeta.getAllStyles().setPadding(5, 5, 5, 5);
        tarjeta.getAllStyles().setMargin(5, 5, 5, 5);

        boolean isRead = notifData.get("isRead") != null && (boolean) notifData.get("isRead");
        int borderColor = isRead ? 0xFFCCCCCC : 0xFFFF9800;
        tarjeta.getAllStyles().setBorder(Border.createRoundBorder(5, 5, borderColor));

        Label titulo = new Label((String) notifData.get("title"));
        titulo.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        if (!isRead) {
            titulo.getAllStyles().setFgColor(0xFFFF9800);
        }

        Label mensaje = new Label((String) notifData.get("message"));
        Label tipo = new Label("Tipo: " + notifData.get("type"));
        Label fecha = new Label("Fecha: " + notifData.get("createdAt"));
        fecha.getAllStyles().setFgColor(0xFF888888);
        fecha.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));

        Long notifId = ((Number) notifData.get("id")).longValue();

        if (!isRead) {
            Button marcarLeidaBtn = new Button("Marcar como leída");
            marcarLeidaBtn.addActionListener(e -> {
                marcarLeidaNotificacion(notifId, parent);
            });
            tarjeta.add(marcarLeidaBtn);
        }

        tarjeta.add(titulo);
        tarjeta.add(mensaje);
        tarjeta.add(tipo);
        tarjeta.add(fecha);

        return tarjeta;
    }

    private void marcarLeidaNotificacion(Long notifId, Container parent) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/notifications/" + notifId + "/read");
        request.setPost(true);
        request.setHttpMethod("PUT");
        request.setContentType("application/json");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                UserSession session = UserSession.getInstance();
                cargarNotificaciones(session.getId(), parent);
            }
        });

        NetworkManager.getInstance().addToQueue(request);
    }

    private void marcarTodasLeidas() {
        UserSession session = UserSession.getInstance();
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/notifications/user/" + session.getId() + "/read-all");
        request.setPost(true);
        request.setHttpMethod("PUT");
        request.setContentType("application/json");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                Dialog.show("Éxito", "Todas las notificaciones marcadas como leídas", "OK", null);
                this.show();
            } else {
                Dialog.show("Error", "No se pudo actualizar", "OK", null);
            }
        });

        NetworkManager.getInstance().addToQueue(request);
    }
}
