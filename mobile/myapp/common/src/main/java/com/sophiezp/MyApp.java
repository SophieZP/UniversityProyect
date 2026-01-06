package com.sophiezp;

import com.codename1.system.Lifecycle;
import com.codename1.ui.*;

/**
 * Clase principal limpia.
 */
public class MyApp extends Lifecycle {

    // ESTA VARIABLE ES LA QUE TE FALTABA ANTES
    // Sirve para recordar en qué pantalla estaba el usuario si la app se minimiza
    private Form current;

    @Override
    public void runApp() {
        // Si la app ya estaba abierta y se minimizó, restauramos la pantalla
        if(current != null){
            current.show();
            return;
        }

        // --- AQUÍ INICIA TU APP ---

        // 1. Creamos la pantalla de Login
        LoginScreen login = new LoginScreen();

        // 2. La mostramos
        login.show();

        // 3. Guardamos la referencia en 'current'
        current = login;
    }
}