package com.example.kazero.database;

import com.example.kazero.models.Product;
import com.example.kazero.models.User;
import com.example.kazero.network.ApiClient;

import java.util.List;

public class DatabaseHelper {

    public static boolean registerUser(User user) {
        User registeredUser = ApiClient.registerUser(user);
        return registeredUser != null;
    }

    public static User loginUser(String username, String password) {
        return ApiClient.loginUser(username, password);
    }

    public static User getUserById(int userId) {
        return ApiClient.getUserById(userId);
    }

    public static boolean updateUser(User user) {
        return ApiClient.updateUser(user);
    }

    public static boolean deleteUser(int userId) {

        return false;
    }


    public static List<Product> getAllProducts() {
        return ApiClient.getAllProducts();
    }

    public static List<Product> getProductsByCategory(String category) {
        return ApiClient.getProductsByCategory(category);
    }

    public static Product getProductById(int productId) {
        return ApiClient.getProductById(productId);
    }

    public static List<Product> searchProducts(String searchQuery) {
        return ApiClient.searchProducts(searchQuery);
    }
}