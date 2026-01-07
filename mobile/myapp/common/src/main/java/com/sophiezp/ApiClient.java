package com.sophiezp;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkManager;
import com.codename1.io.JSONParser;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

/**
 * Cliente centralizado para realizar llamadas a la API REST.
 * Maneja la construcción de requests, parseo de respuestas y manejo de errores.
 */
public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";

    /**
     * Realiza una llamada POST a la API con JSON.
     */
    public static void post(String endpoint, String jsonBody, ApiCallback callback) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl(BASE_URL + endpoint);
        request.setPost(true);
        request.setHttpMethod("POST");
        request.setContentType("application/json");
        request.setRequestBody(jsonBody);

        request.addResponseListener(networkEvent -> {
            int responseCode = request.getResponseCode();
            ApiResponse response = parseResponse(request, responseCode);
            callback.onResponse(response);
        });

        NetworkManager.getInstance().addToQueue(request);
    }

    /**
     * Realiza una llamada GET a la API.
     */
    public static void get(String endpoint, ApiCallback callback) {
        ConnectionRequest request = new ConnectionRequest();
        request.setUrl(BASE_URL + endpoint);
        request.setHttpMethod("GET");

        request.addResponseListener(networkEvent -> {
            int responseCode = request.getResponseCode();
            ApiResponse response = parseResponse(request, responseCode);
            callback.onResponse(response);
        });

        NetworkManager.getInstance().addToQueue(request);
    }

    /**
     * Parsea la respuesta del servidor.
     */
    private static ApiResponse parseResponse(ConnectionRequest request, int responseCode) {
        ApiResponse response = new ApiResponse();
        response.setResponseCode(responseCode);

        try {
            if (responseCode == 200 || responseCode == 201) {
                byte[] data = request.getResponseData();
                if (data != null && data.length > 0) {
                    JSONParser parser = new JSONParser();
                    Map<String, Object> jsonResponse = parser.parseJSON(
                            new InputStreamReader(new ByteArrayInputStream(data), "UTF-8")
                    );

                    // IMPORTANTE: Extraer el campo "data" de la respuesta del backend
                    Object dataField = jsonResponse.get("data");

                    // Debug: Imprimir estructura completa
                    System.out.println("=== RESPUESTA DEL SERVIDOR ===");
                    System.out.println("Response completo: " + jsonResponse);
                    System.out.println("Campo 'data': " + dataField);
                    System.out.println("==============================");

                    response.setSuccess(true);
                    response.setMessage(getMessageFromResponse(jsonResponse));

                    if (dataField instanceof Map) {
                        // Si "data" es un objeto Map, lo pasamos
                        response.setData((Map<String, Object>) dataField);
                    } else if (dataField instanceof ArrayList) {
                        // Si "data" es un array, lo envolvemos en un Map con clave "items"
                        Map<String, Object> wrappedData = new java.util.HashMap<>();
                        wrappedData.put("items", dataField);
                        response.setData(wrappedData);
                    } else if (dataField != null) {
                        // Si es cualquier otro tipo, lo envolvemos
                        Map<String, Object> wrappedData = new java.util.HashMap<>();
                        wrappedData.put("value", dataField);
                        response.setData(wrappedData);
                    } else {
                        // Si no hay campo "data", usar toda la respuesta
                        response.setData(jsonResponse);
                    }
                } else {
                    response.setSuccess(true);
                    response.setMessage("Operación exitosa");
                }
            } else if (responseCode == 400) {
                String errorMsg = new String(request.getResponseData());
                response.setSuccess(false);
                response.setMessage(extractErrorMessage(errorMsg));
                response.setErrorCode("BAD_REQUEST");
            } else if (responseCode == 401) {
                response.setSuccess(false);
                response.setMessage("Credenciales incorrectas");
                response.setErrorCode("UNAUTHORIZED");
            } else if (responseCode == 404) {
                response.setSuccess(false);
                response.setMessage("Recurso no encontrado");
                response.setErrorCode("NOT_FOUND");
            } else if (responseCode == 500) {
                response.setSuccess(false);
                response.setMessage("Error interno del servidor");
                response.setErrorCode("SERVER_ERROR");
            } else {
                response.setSuccess(false);
                response.setMessage("Error desconocido (código: " + responseCode + ")");
                response.setErrorCode("UNKNOWN_ERROR");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error al procesar la respuesta del servidor: " + e.getMessage());
            response.setErrorCode("PARSE_ERROR");
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Extrae el mensaje de error desde la respuesta JSON.
     */
    private static String extractErrorMessage(String errorResponse) {
        try {
            if (errorResponse.contains("\"message\"")) {
                int startIndex = errorResponse.indexOf("\"message\":\"") + 11;
                int endIndex = errorResponse.indexOf("\"", startIndex);
                return errorResponse.substring(startIndex, endIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorResponse;
    }

    /**
     * Obtiene el mensaje desde la respuesta JSON.
     */
    private static String getMessageFromResponse(Map<String, Object> response) {
        if (response != null && response.containsKey("message")) {
            return response.get("message").toString();
        }
        return null;
    }

    /**
     * Interfaz callback para manejar respuestas asincrónicas.
     */
    public interface ApiCallback {
        void onResponse(ApiResponse response);
    }

    /**
     * Clase para encapsular la respuesta de la API.
     */
    public static class ApiResponse {
        private int responseCode;
        private boolean success;
        private String message;
        private String errorCode;
        private Map<String, Object> data;

        public int getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(int responseCode) {
            this.responseCode = responseCode;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }
    }
}