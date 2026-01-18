package com.example.kazero.utils;

import com.example.kazero.models.CartItem;
import com.example.kazero.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addProduct(Product product, int quantity) {

        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        cartItems.add(new CartItem(product, quantity));
    }

    public void removeProduct(int productId) {
        cartItems.removeIf(item -> item.getProduct().getId() == productId);
    }

    public void updateQuantity(int productId, int newQuantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                if (newQuantity <= 0) {
                    removeProduct(productId);
                } else {
                    item.setQuantity(newQuantity);
                }
                break;
            }
        }
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public int getItemCount() {
        return cartItems.size();
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
}