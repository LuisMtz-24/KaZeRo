package com.example.kazero.network;

import com.example.kazero.models.Product;
import com.example.kazero.models.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {

    // IMPORTANTE: Cambia esta URL por la de tu API desplegada
    // Si estás usando Railway, será algo como: https://tu-app.up.railway.app
    // Si estás en desarrollo local con emulador: http://10.0.2.2:3000
    private static final String BASE_URL = "https://tramway.proxy.rlwy.net:3000";

    // ==================== USER ENDPOINTS ====================

    public static User registerUser(User user) {
        try {
            URL url = new URL(BASE_URL + "/api/users/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Crear JSON del usuario
            JSONObject json = new JSONObject();
            json.put("username", user.getUsername());
            json.put("password", user.getPassword());
            json.put("email", user.getEmail());
            json.put("address", user.getAddress());
            json.put("postal_code", user.getPostalCode());

            // Enviar request
            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            // Leer respuesta
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject responseJson = new JSONObject(response.toString());
                if (responseJson.getBoolean("success")) {
                    user.setId(responseJson.getInt("userId"));
                    return user;
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User loginUser(String username, String password) {
        try {
            URL url = new URL(BASE_URL + "/api/users/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", password);

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject responseJson = new JSONObject(response.toString());
                if (responseJson.getBoolean("success")) {
                    JSONObject userJson = responseJson.getJSONObject("user");
                    return new User(
                            userJson.getInt("id"),
                            userJson.getString("username"),
                            userJson.getString("email"),
                            userJson.getString("address"),
                            userJson.getString("postal_code")
                    );
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User getUserById(int userId) {
        try {
            URL url = new URL(BASE_URL + "/api/users/" + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject responseJson = new JSONObject(response.toString());
                if (responseJson.getBoolean("success")) {
                    JSONObject userJson = responseJson.getJSONObject("user");
                    return new User(
                            userJson.getInt("id"),
                            userJson.getString("username"),
                            userJson.getString("email"),
                            userJson.getString("address"),
                            userJson.getString("postal_code")
                    );
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateUser(User user) {
        try {
            URL url = new URL(BASE_URL + "/api/users/" + user.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("email", user.getEmail());
            json.put("address", user.getAddress());
            json.put("postal_code", user.getPostalCode());

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            int responseCode = conn.getResponseCode();
            conn.disconnect();

            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== PRODUCT ENDPOINTS ====================

    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "/api/products");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject responseJson = new JSONObject(response.toString());
                if (responseJson.getBoolean("success")) {
                    JSONArray productsArray = responseJson.getJSONArray("products");

                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject productJson = productsArray.getJSONObject(i);
                        Product product = new Product(
                                productJson.getInt("id"),
                                productJson.getString("name"),
                                productJson.getString("description"),
                                productJson.getDouble("price"),
                                productJson.optString("image_url", ""),
                                productJson.getString("category"),
                                productJson.getInt("stock")
                        );
                        products.add(product);
                    }
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public static Product getProductById(int productId) {
        try {
            URL url = new URL(BASE_URL + "/api/products/" + productId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject responseJson = new JSONObject(response.toString());
                if (responseJson.getBoolean("success")) {
                    JSONObject productJson = responseJson.getJSONObject("product");
                    return new Product(
                            productJson.getInt("id"),
                            productJson.getString("name"),
                            productJson.getString("description"),
                            productJson.getDouble("price"),
                            productJson.optString("image_url", ""),
                            productJson.getString("category"),
                            productJson.getInt("stock")
                    );
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "/api/products/category/" + category);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject responseJson = new JSONObject(response.toString());
                if (responseJson.getBoolean("success")) {
                    JSONArray productsArray = responseJson.getJSONArray("products");

                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject productJson = productsArray.getJSONObject(i);
                        Product product = new Product(
                                productJson.getInt("id"),
                                productJson.getString("name"),
                                productJson.getString("description"),
                                productJson.getDouble("price"),
                                productJson.optString("image_url", ""),
                                productJson.getString("category"),
                                productJson.getInt("stock")
                        );
                        products.add(product);
                    }
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public static List<Product> searchProducts(String query) {
        List<Product> products = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "/api/products/search/" + query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                JSONObject responseJson = new JSONObject(response.toString());
                if (responseJson.getBoolean("success")) {
                    JSONArray productsArray = responseJson.getJSONArray("products");

                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject productJson = productsArray.getJSONObject(i);
                        Product product = new Product(
                                productJson.getInt("id"),
                                productJson.getString("name"),
                                productJson.getString("description"),
                                productJson.getDouble("price"),
                                productJson.optString("image_url", ""),
                                productJson.getString("category"),
                                productJson.getInt("stock")
                        );
                        products.add(product);
                    }
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}