package com.sophiezp;

import com.codename1.ui.*;
import com.codename1.ui.layouts.BoxLayout;

public class SignUpScreen extends Form {

    private Form previousForm;

    public SignUpScreen(Form parent) {
        super("Crear Cuenta", BoxLayout.y());
        this.previousForm = parent;

        this.getAllStyles().setBgColor(0xE8F4F8);
        this.getAllStyles().setBgTransparency(255);

        TextField fullNameField = new TextField("", "Nombre Completo", 30, TextField.ANY);
        fullNameField.getAllStyles().setMargin(12, 12, 8, 12);
        fullNameField.getAllStyles().setBgColor(0xFFFFFF);
        fullNameField.getAllStyles().setFgColor(0x1A1A1A);
        fullNameField.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
        fullNameField.getAllStyles().setBgTransparency(255);
        fullNameField.getAllStyles().setPadding(10, 10, 10, 10);

        TextField emailField = new TextField("", "Correo Electrónico", 30, TextField.EMAILADDR);
        emailField.getAllStyles().setMargin(12, 12, 8, 12);
        emailField.getAllStyles().setBgColor(0xFFFFFF);
        emailField.getAllStyles().setFgColor(0x1A1A1A);
        emailField.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
        emailField.getAllStyles().setBgTransparency(255);
        emailField.getAllStyles().setPadding(10, 10, 10, 10);

        TextField universityField = new TextField("", "Universidad", 30, TextField.ANY);
        universityField.getAllStyles().setMargin(12, 12, 8, 12);
        universityField.getAllStyles().setBgColor(0xFFFFFF);
        universityField.getAllStyles().setFgColor(0x1A1A1A);
        universityField.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
        universityField.getAllStyles().setBgTransparency(255);
        universityField.getAllStyles().setPadding(10, 10, 10, 10);

        TextField passwordField = new TextField("", "Contraseña", 20, TextField.PASSWORD);
        passwordField.getAllStyles().setMargin(12, 12, 8, 12);
        passwordField.getAllStyles().setBgColor(0xFFFFFF);
        passwordField.getAllStyles().setFgColor(0x1A1A1A);
        passwordField.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
        passwordField.getAllStyles().setBgTransparency(255);
        passwordField.getAllStyles().setPadding(10, 10, 10, 10);

        TextField confirmPasswordField = new TextField("", "Confirmar Contraseña", 20, TextField.PASSWORD);
        confirmPasswordField.getAllStyles().setMargin(12, 12, 8, 12);
        confirmPasswordField.getAllStyles().setBgColor(0xFFFFFF);
        confirmPasswordField.getAllStyles().setFgColor(0x1A1A1A);
        confirmPasswordField.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
        confirmPasswordField.getAllStyles().setBgTransparency(255);
        confirmPasswordField.getAllStyles().setPadding(10, 10, 10, 10);

        Button signUpButton = new Button("Registrarse");
        signUpButton.getAllStyles().setMarginTop(25);
        signUpButton.getAllStyles().setMarginBottom(15);
        signUpButton.getAllStyles().setBgColor(0x2E7D32);
        signUpButton.getAllStyles().setFgColor(0xFFFFFF);
        signUpButton.getAllStyles().setPadding(18, 20, 18, 20);
        signUpButton.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
        signUpButton.getAllStyles().setAlignment(Component.CENTER);

        Button backButton = new Button("Volver");
        backButton.getAllStyles().setMarginTop(10);
        backButton.getAllStyles().setBgColor(0xD32F2F);
        backButton.getAllStyles().setFgColor(0xFFFFFF);
        backButton.getAllStyles().setPadding(18, 20, 18, 20);
        backButton.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
        backButton.getAllStyles().setAlignment(Component.CENTER);

        signUpButton.addActionListener(e -> {
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String university = universityField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (fullName.isEmpty() || email.isEmpty() || university.isEmpty() || password.isEmpty()) {
                Dialog.show("Error", "Por favor completa todos los campos", "OK", null);
                return;
            }

            if (!email.contains("@")) {
                Dialog.show("Error", "Ingresa un correo válido", "OK", null);
                return;
            }

            if (password.length() < 6) {
                Dialog.show("Error", "La contraseña debe tener al menos 6 caracteres", "OK", null);
                return;
            }

            if (!password.equals(confirmPassword)) {
                Dialog.show("Error", "Las contraseñas no coinciden", "OK", null);
                return;
            }

            registrarUsuario(fullName, email, university, password);
        });

        backButton.addActionListener(e -> {
            if (previousForm != null) {
                previousForm.show();
            }
        });

        Label titleLabel = new Label("Crear Nueva Cuenta");
        titleLabel.getAllStyles().setFgColor(0x1B5E20);
        titleLabel.getAllStyles().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE));
        titleLabel.getAllStyles().setAlignment(Component.CENTER);
        titleLabel.getAllStyles().setMarginBottom(20);

        Label instructionLabel = new Label("Completa el formulario para crear tu cuenta");
        instructionLabel.getAllStyles().setFgColor(0x455A64);
        instructionLabel.getAllStyles().setAlignment(Component.CENTER);
        instructionLabel.getAllStyles().setMarginBottom(20);

        this.add(titleLabel);
        this.add(instructionLabel);
        this.add(fullNameField);
        this.add(emailField);
        this.add(universityField);
        this.add(passwordField);
        this.add(confirmPasswordField);
        this.add(signUpButton);
        this.add(backButton);

        this.setScrollableY(true);
    }

    private void registrarUsuario(String fullName, String email, String university, String password) {
        String jsonBody = "{"
                + "\"fullName\":\"" + escapeJson(fullName) + "\","
                + "\"email\":\"" + escapeJson(email) + "\","
                + "\"universityName\":\"" + escapeJson(university) + "\","
                + "\"passwordHash\":\"" + escapeJson(password) + "\""
                + "}";

        ApiClient.post("/users/register", jsonBody, response -> {
            if (response.isSuccess()) {
                Dialog.show("Éxito", "Cuenta creada correctamente. Por favor inicia sesión.", "OK", null);
                new LoginScreen().show();
            } else {
                Dialog.show("Error", response.getMessage(), "OK", null);
            }
        });
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