package com.sophiezp;

import java.util.Map;

public class UserSession {
    // Instancia única
    private static UserSession instance;

    // Datos del usuario
    private Long id;
    private String fullName;
    private String email;
    private String universityName;

    // Constructor privado (nadie puede crear instancias fuera de aquí)
    private UserSession() {}

    // --- ¡ESTE MÉTODO TE FALTABA! ---
    // Es vital para que UserSession.getInstance() funcione
    public static UserSession getInstance() {
        if(instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void startSession(Map<String, Object> userData) {
        // CORRECCIÓN: Parseo robusto del ID
        Object idObj = userData.get("id");
        if (idObj != null) {
            // Convertimos a String primero y luego a número para evitar errores de cast
            // (Funciona si es Integer 1, Double 1.0 o String "1")
            this.id = Float.valueOf(idObj.toString()).longValue();
        }

        this.fullName = (String) userData.get("fullName");
        this.email = (String) userData.get("email");
        this.universityName = (String) userData.get("universityName");

        // Imprimimos en consola para verificar que se guardó
        System.out.println("Sesión iniciada para ID: " + this.id);
    }

    public void closeSession() {
        instance = null;
    }

    // Getters
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getUniversity() { return universityName; }
}