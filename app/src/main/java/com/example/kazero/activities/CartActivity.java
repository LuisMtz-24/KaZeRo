package com.example.kazero.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kazero.R;
import com.example.kazero.adapters.CartAdapter;
import com.example.kazero.models.CartItem;
import com.example.kazero.utils.CartManager;

import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemListener {

    private RecyclerView rvCart;
    private TextView tvEmptyCart, tvTotal;
    private Button btnCheckout;
    private CartAdapter cartAdapter;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        setupToolbar();
        cartManager = CartManager.getInstance();
        setupRecyclerView();
        updateUI();
        setupListeners();
    }

    private void initViews() {
        rvCart = findViewById(R.id.rvCart);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        tvTotal = findViewById(R.id.tvTotal);
        btnCheckout = findViewById(R.id.btnCheckout);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.cart_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        List<CartItem> cartItems = cartManager.getCartItems();
        cartAdapter = new CartAdapter(this, cartItems, this);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(cartAdapter);
    }

    private void setupListeners() {
        btnCheckout.setOnClickListener(v -> performCheckout());
    }

    private void updateUI() {
        if (cartManager.isEmpty()) {
            rvCart.setVisibility(View.GONE);
            tvEmptyCart.setVisibility(View.VISIBLE);
            btnCheckout.setVisibility(View.GONE);
            tvTotal.setVisibility(View.GONE);
        } else {
            rvCart.setVisibility(View.VISIBLE);
            tvEmptyCart.setVisibility(View.GONE);
            btnCheckout.setVisibility(View.VISIBLE);
            tvTotal.setVisibility(View.VISIBLE);

            double total = cartManager.getTotalPrice();
            tvTotal.setText(String.format("Total: $%.2f MXN", total));
        }
    }

    private void performCheckout() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Compra")
                .setMessage("¿Deseas finalizar tu compra por $" +
                        String.format("%.2f", cartManager.getTotalPrice()) + " MXN?")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    cartManager.clearCart();
                    cartAdapter.notifyDataSetChanged();
                    updateUI();
                    Toast.makeText(this, R.string.purchase_success, Toast.LENGTH_LONG).show();
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onRemoveItem(int position) {
        CartItem item = cartManager.getCartItems().get(position);
        cartManager.removeProduct(item.getProduct().getId());
        cartAdapter.notifyItemRemoved(position);
        updateUI();
        Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onQuantityChanged(int position, int newQuantity) {
        CartItem item = cartManager.getCartItems().get(position);
        cartManager.updateQuantity(item.getProduct().getId(), newQuantity);
        cartAdapter.notifyItemChanged(position);
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}