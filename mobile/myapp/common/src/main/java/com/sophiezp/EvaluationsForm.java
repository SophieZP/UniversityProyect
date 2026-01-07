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

public class EvaluationsForm extends Form {

    private Long subjectId;
    private String subjectName;
    private Form previousScreen;
    private Container evaluationsContainer;
    private Label promedioLabel;

    public EvaluationsForm(Form previous, Long subjectId, String subjectName) {
        super("Calificaciones - " + subjectName, BoxLayout.y());

        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.previousScreen = previous;

        getToolbar().setBackCommand("", e -> previousScreen.showBack());

        // Label para mostrar el promedio ponderado
        promedioLabel = new Label("Promedio: Calculando...");
        promedioLabel.getAllStyles().setFont(Font.createSystemFont(
                Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE
        ));
        promedioLabel.getAllStyles().setFgColor(0x2196F3);
        promedioLabel.getAllStyles().setAlignment(Component.CENTER);
        promedioLabel.getAllStyles().setPadding(20, 20, 20, 20);
        promedioLabel.getAllStyles().setBgColor(0xE3F2FD);
        promedioLabel.getAllStyles().setBgTransparency(255);
        this.add(promedioLabel);

        // Contenedor para la lista de calificaciones
        evaluationsContainer = new Container(BoxLayout.y());
        evaluationsContainer.setScrollableY(true);
        this.add(evaluationsContainer);

        // FAB para agregar nueva calificación
        FloatingActionButton fab = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
        fab.bindFabToContainer(this);
        fab.addActionListener(e -> new AddEvaluationForm(this, subjectId, subjectName).show());
    }

    @Override
    protected void onShowCompleted() {
        super.onShowCompleted();
        cargarCalificaciones();
    }

    private void cargarCalificaciones() {
        evaluationsContainer.removeAll();
        Label loadingLabel = new Label("Cargando calificaciones...");
        loadingLabel.getAllStyles().setAlignment(Component.CENTER);
        evaluationsContainer.add(loadingLabel);
        this.revalidate();

        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/evaluations/subject/" + subjectId);
        request.setHttpMethod("GET");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                try {
                    byte[] data = request.getResponseData();
                    if (data != null && data.length > 0) {
                        JSONParser parser = new JSONParser();
                        Map<String, Object> response = parser.parseJSON(
                                new InputStreamReader(new ByteArrayInputStream(data), "UTF-8")
                        );

                        Object dataField = response.get("data");
                        final ArrayList<Map<String, Object>> listaEvaluaciones;

                        if (dataField instanceof ArrayList) {
                            listaEvaluaciones = (ArrayList<Map<String, Object>>) dataField;
                        } else if (response.containsKey("root")) {
                            listaEvaluaciones = (ArrayList<Map<String, Object>>) response.get("root");
                        } else {
                            listaEvaluaciones = null;
                        }

                        Display.getInstance().callSerially(() -> {
                            evaluationsContainer.removeAll();

                            if (listaEvaluaciones != null && !listaEvaluaciones.isEmpty()) {
                                // Calcular promedio ponderado
                                double promedioTotal = calcularPromedioPonderado(listaEvaluaciones);
                                String promedioStr = formatearNumero(promedioTotal, 2);
                                promedioLabel.setText("📊 Promedio Acumulado: " + promedioStr + " / 5.0");

                                // Cambiar color según la nota
                                if (promedioTotal >= 4.0) {
                                    promedioLabel.getAllStyles().setFgColor(0x4CAF50); // Verde
                                } else if (promedioTotal >= 3.0) {
                                    promedioLabel.getAllStyles().setFgColor(0xFF9800); // Naranja
                                } else {
                                    promedioLabel.getAllStyles().setFgColor(0xF44336); // Rojo
                                }

                                for (Map<String, Object> evalData : listaEvaluaciones) {
                                    evaluationsContainer.add(crearTarjetaCalificacion(evalData));
                                }
                            } else {
                                promedioLabel.setText("📊 Sin calificaciones registradas");
                                promedioLabel.getAllStyles().setFgColor(0x999999);

                                Label emptyLabel = new Label("No hay calificaciones registradas");
                                emptyLabel.getAllStyles().setAlignment(Component.CENTER);
                                emptyLabel.getAllStyles().setFgColor(0x999999);
                                emptyLabel.getAllStyles().setMarginTop(50);
                                evaluationsContainer.add(emptyLabel);
                            }

                            evaluationsContainer.animateLayout(200);
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Display.getInstance().callSerially(() -> {
                        evaluationsContainer.removeAll();
                        evaluationsContainer.add(new Label("Error al cargar calificaciones"));
                        this.revalidate();
                    });
                }
            }
        });
        NetworkManager.getInstance().addToQueue(request);
    }

    private double calcularPromedioPonderado(ArrayList<Map<String, Object>> evaluaciones) {
        double sumaNotas = 0.0;
        double sumaPorcentajes = 0.0;

        for (Map<String, Object> eval : evaluaciones) {
            Object gradeObj = eval.get("obtainedGrade");
            Object percentageObj = eval.get("percentage");

            if (gradeObj != null && percentageObj != null) {
                double grade = ((Number) gradeObj).doubleValue();
                double percentage = ((Number) percentageObj).doubleValue();

                sumaNotas += (grade * percentage / 100.0);
                sumaPorcentajes += percentage;
            }
        }

        // Si el total de porcentajes es menor a 100, ajustamos proporcionalmente
        if (sumaPorcentajes > 0) {
            return (sumaNotas / sumaPorcentajes) * 100;
        }
        return 0.0;
    }

    private Container crearTarjetaCalificacion(Map<String, Object> evalData) {
        Container tarjeta = new Container(BoxLayout.y());
        tarjeta.getAllStyles().setPadding(15, 15, 15, 15);
        tarjeta.getAllStyles().setMargin(10, 10, 10, 10);
        tarjeta.getAllStyles().setBorder(Border.createLineBorder(2, 0xFF2196F3));
        tarjeta.getAllStyles().setBgColor(0xFFFFFF);
        tarjeta.getAllStyles().setBgTransparency(255);

        // Nombre de la evaluación
        String name = evalData.get("name") != null ? (String) evalData.get("name") : "Sin nombre";
        Label nombreLabel = new Label("📝 " + name);
        nombreLabel.getAllStyles().setFont(Font.createSystemFont(
                Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM
        ));

        // Porcentaje
        Object percentageObj = evalData.get("percentage");
        double percentage = percentageObj != null ? ((Number) percentageObj).doubleValue() : 0.0;
        String percentageStr = formatearNumero(percentage, 1);
        Label percentageLabel = new Label("⚖️ Porcentaje: " + percentageStr + "%");
        percentageLabel.getAllStyles().setFgColor(0x666666);

        // Nota obtenida
        Object gradeObj = evalData.get("obtainedGrade");
        double grade = gradeObj != null ? ((Number) gradeObj).doubleValue() : 0.0;
        String gradeStr = formatearNumero(grade, 2);
        Label gradeLabel = new Label("✅ Nota: " + gradeStr + " / 5.0");
        gradeLabel.getAllStyles().setFont(Font.createSystemFont(
                Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM
        ));

        // Color según la nota
        if (grade >= 4.0) {
            gradeLabel.getAllStyles().setFgColor(0x4CAF50); // Verde
        } else if (grade >= 3.0) {
            gradeLabel.getAllStyles().setFgColor(0xFF9800); // Naranja
        } else {
            gradeLabel.getAllStyles().setFgColor(0xF44336); // Rojo
        }

        // Aporte al promedio
        double aporte = (grade * percentage) / 100.0;
        String aporteStr = formatearNumero(aporte, 2);
        Label aporteLabel = new Label("💯 Aporta: " + aporteStr + " puntos al promedio");
        aporteLabel.getAllStyles().setFgColor(0x888888);
        aporteLabel.getAllStyles().setFont(Font.createSystemFont(
                Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL
        ));

        // Botón eliminar
        Button eliminarBtn = new Button("🗑️ Eliminar");
        eliminarBtn.getAllStyles().setBgColor(0xF44336);
        eliminarBtn.getAllStyles().setFgColor(0xFFFFFF);
        eliminarBtn.getAllStyles().setPadding(10, 10, 10, 10);
        eliminarBtn.getAllStyles().setMarginTop(10);

        Object evalIdObj = evalData.get("id");
        Long evalId = evalIdObj != null ? Float.valueOf(evalIdObj.toString()).longValue() : 0L;
        final Long finalEvalId = evalId;

        eliminarBtn.addActionListener(e -> {
            Command yes = new Command("Sí, eliminar");
            Command no = new Command("Cancelar");
            Command[] cmds = {yes, no};
            Command result = Dialog.show("Confirmar", "¿Eliminar esta calificación?", cmds);
            if (result == yes) {
                eliminarCalificacion(finalEvalId);
            }
        });

        tarjeta.add(nombreLabel);
        tarjeta.add(percentageLabel);
        tarjeta.add(gradeLabel);
        tarjeta.add(aporteLabel);
        tarjeta.add(eliminarBtn);

        return tarjeta;
    }

    private void eliminarCalificacion(Long evalId) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl("http://localhost:8080/api/evaluations/" + evalId);
        request.setHttpMethod("DELETE");

        request.addResponseListener(e -> {
            if (request.getResponseCode() == 200) {
                Display.getInstance().callSerially(() -> {
                    Dialog.show("Éxito", "Calificación eliminada", "OK", null);
                    cargarCalificaciones();
                });
            } else {
                Display.getInstance().callSerially(() -> {
                    Dialog.show("Error", "No se pudo eliminar", "OK", null);
                });
            }
        });

        NetworkManager.getInstance().addToQueue(request);
    }

    /**
     * Formatea un número con decimales específicos
     * (Alternativa a String.format que no está disponible en Codename One)
     */
    private String formatearNumero(double numero, int decimales) {
        // Calcular multiplicador sin usar Math.pow
        long multiplicador = 1;
        for (int i = 0; i < decimales; i++) {
            multiplicador *= 10;
        }

        // Redondear al número de decimales especificado
        long redondeado = Math.round(numero * multiplicador);
        double resultado = (double) redondeado / multiplicador;

        // Convertir a String
        String resultadoStr = String.valueOf(resultado);

        // Asegurar que tenga el número correcto de decimales
        int puntoIndex = resultadoStr.indexOf('.');
        if (puntoIndex == -1) {
            resultadoStr += ".";
            puntoIndex = resultadoStr.length() - 1;
        }

        int decimalesActuales = resultadoStr.length() - puntoIndex - 1;
        while (decimalesActuales < decimales) {
            resultadoStr += "0";
            decimalesActuales++;
        }

        return resultadoStr;
    }
}