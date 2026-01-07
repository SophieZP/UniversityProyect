package com.sophiezp;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;

public class LoginScreen extends Form {

    public LoginScreen() {
        super("Bienvenido", BoxLayout.y());

        this.getAllStyles().setBgColor(0xE8F4F8);
        this.getAllStyles().setBgTransparency(255);

        Label titleLabel = new Label("Gestor Universitario");
        titleLabel.getAllStyles().setFgColor(0x1B5E20);
        titleLabel.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
        titleLabel.getAllStyles().setAlignment(Component.CENTER);
        titleLabel.getAllStyles().setMarginTop(50);
        titleLabel.getAllStyles().setMarginBottom(30);

        TextField emailField = new TextField("", "Correo", 20, TextField.EMAILADDR);
        emailField.getAllStyles().setMargin(12, 12, 8, 12);
        emailField.getAllStyles().setBgColor(0xFFFFFF);
        emailField.getAllStyles().setFgColor(0x1A1A1A);
        emailField.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
        emailField.getAllStyles().setBgTransparency(255);
        emailField.getAllStyles().setPadding(10, 10, 10, 10);

        TextField passwordField = new TextField("", "Contraseña", 20, TextField.PASSWORD);
        passwordField.getAllStyles().setMargin(12, 12, 8, 12);
        passwordField.getAllStyles().setBgColor(0xFFFFFF);
        passwordField.getAllStyles().setFgColor(0x1A1A1A);
        passwordField.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
        passwordField.getAllStyles().setBgTransparency(255);
        passwordField.getAllStyles().setPadding(10, 10, 10, 10);

        Button loginButton = new Button("Iniciar Sesión");
        loginButton.getAllStyles().setMarginTop(25);
        loginButton.getAllStyles().setMarginBottom(15);
        loginButton.getAllStyles().setBgColor(0x2E7D32);
        loginButton.getAllStyles().setFgColor(0xFFFFFF);
        loginButton.getAllStyles().setPadding(18, 20, 18, 20);
        loginButton.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
        loginButton.getAllStyles().setAlignment(Component.CENTER);

        Button signUpButton = new Button("Crear Cuenta");
        signUpButton.getAllStyles().setMarginTop(10);
        signUpButton.getAllStyles().setBgColor(0x1976D2);
        signUpButton.getAllStyles().setFgColor(0xFFFFFF);
        signUpButton.getAllStyles().setPadding(18, 20, 18, 20);
        signUpButton.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
        signUpButton.getAllStyles().setAlignment(Component.CENTER);
        signUpButton.addActionListener(e -> {
            new SignUpScreen(this).show();
        });

        loginButton.addActionListener(e -> {
            String emailText = emailField.getText().trim();
            String passwordText = passwordField.getText();

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Dialog.show("Error", "Por favor completa todos los campos", "OK", null);
                return;
            }

            if (!emailText.contains("@")) {
                Dialog.show("Error", "Ingresa un correo válido", "OK", null);
                return;
            }

            String jsonBody = "{\"email\":\"" + escapeJson(emailText) + "\",\"passwordHash\":\"" + escapeJson(passwordText) + "\"}";

            ApiClient.post("/users/login", jsonBody, response -> {
                if (response.isSuccess()) {
                    try {
                        // Debug: Verificar qué datos llegaron
                        System.out.println("=== LOGIN EXITOSO ===");
                        System.out.println("Datos recibidos: " + response.getData());

                        UserSession.getInstance().startSession(response.getData());

                        Long userId = UserSession.getInstance().getId();
                        System.out.println("ID de sesión guardado: " + userId);

                        if (userId != null) {
                            new DashboardForm().show();
                        } else {
                            Dialog.show("Error", "No se pudo extraer el ID del usuario. Datos: " + response.getData(), "OK", null);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Dialog.show("Error", "Error procesando la respuesta: " + ex.getMessage(), "OK", null);
                    }
                } else {
                    Dialog.show("Error", response.getMessage(), "OK", null);
                }
            });
        });

        this.add(titleLabel);
        this.add(emailField);
        this.add(passwordField);
        this.add(loginButton);
        this.add(signUpButton);
        this.setScrollableY(true);
    }

    /**
     * CORREGIDO: Método compatible con Codename One
     */
    private String escapeJson(String input) {
        if (input == null) return "";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\\':
                    result.append("\\\\");
                    break;
                case '"':
                    result.append("\\\"");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                default:
                    result.append(c);
            }
        }
        return result.toString();
    }
}