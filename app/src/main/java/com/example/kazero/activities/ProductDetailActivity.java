package com.example.kazero.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.kazero.R;
import com.example.kazero.database.DatabaseHelper;
import com.example.kazero.models.Product;
import com.example.kazero.utils.CartManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivProductImage;
    private TextView tvProductName, tvProductPrice, tvProductDescription, tvProductStock, tvProductCategory;
    private Button btnAddToCart, btnIncrease, btnDecrease;
    private TextView tvQuantity;

    private Product product;
    private int quantity = 1;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        setupToolbar();

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        int productId = getIntent().getIntExtra("product_id", -1);
        if (productId != -1) {
            loadProduct(productId);
        }

        setupListeners();
    }

    private void initViews() {
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductStock = findViewById(R.id.tvProductStock);
        tvProductCategory = findViewById(R.id.tvProductCategory);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        tvQuantity = findViewById(R.id.tvQuantity);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.product_detail);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnIncrease.setOnClickListener(v -> {
            if (product != null && quantity < product.getStock()) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Stock máximo alcanzado", Toast.LENGTH_SHORT).show();
            }
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            if (product != null) {
                CartManager.getInstance().addProduct(product, quantity);
                Toast.makeText(this, R.string.added_to_cart, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProduct(int productId) {
        executorService.execute(() -> {
            Product loadedProduct = DatabaseHelper.getProductById(productId);

            mainHandler.post(() -> {
                if (loadedProduct != null) {
                    product = loadedProduct;
                    displayProduct();
                } else {
                    Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void displayProduct() {
        tvProductName.setText(product.getName());
        tvProductPrice.setText(String.format("$%.2f MXN", product.getPrice()));
        tvProductDescription.setText(product.getDescription());
        tvProductStock.setText(getString(R.string.stock_available) + " " + product.getStock());
        tvProductCategory.setText(product.getCategory());

        // Cargar imagen (usando placeholder por ahora)
        ivProductImage.setImageResource(R.drawable.placeholder_jewelry);

        // Si el stock es 0, deshabilitar botón
        if (product.getStock() == 0) {
            btnAddToCart.setEnabled(false);
            btnAddToCart.setText("Agotado");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}