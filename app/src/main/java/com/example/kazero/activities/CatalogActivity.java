package com.example.kazero.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kazero.R;
import com.example.kazero.adapters.ProductAdapter;
import com.example.kazero.database.DatabaseHelper;
import com.example.kazero.models.Product;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CatalogActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> allProducts;
    private List<Product> filteredProducts;
    private EditText etSearch;
    private ImageButton btnCart;
    private ProgressBar progressBar;
    private Button btnAll, btnRings, btnNecklaces, btnEarrings, btnBracelets;

    private ExecutorService executorService;
    private Handler mainHandler;
    private String currentCategory = "Todas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        initViews();
        setupToolbar();
        setupRecyclerView();

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        loadProducts();
        setupListeners();
    }

    private void initViews() {
        rvProducts = findViewById(R.id.rvProducts);
        etSearch = findViewById(R.id.etSearch);
        btnCart = findViewById(R.id.btnCart);
        progressBar = findViewById(R.id.progressBar);
        btnAll = findViewById(R.id.btnAll);
        btnRings = findViewById(R.id.btnRings);
        btnNecklaces = findViewById(R.id.btnNecklaces);
        btnEarrings = findViewById(R.id.btnEarrings);
        btnBracelets = findViewById(R.id.btnBracelets);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configurar el menú del toolbar
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (id == R.id.menu_cart) {
                startActivity(new Intent(this, CartActivity.class));
                return true;
            }

            return false;
        });
    }

    private void setupRecyclerView() {
        allProducts = new ArrayList<>();
        filteredProducts = new ArrayList<>();
        productAdapter = new ProductAdapter(this, filteredProducts, this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvProducts.setLayoutManager(layoutManager);
        rvProducts.setAdapter(productAdapter);
    }

    private void setupListeners() {
        btnCart.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        btnAll.setOnClickListener(v -> filterByCategory("Todas"));
        btnRings.setOnClickListener(v -> filterByCategory("Anillos"));
        btnNecklaces.setOnClickListener(v -> filterByCategory("Collares"));
        btnEarrings.setOnClickListener(v -> filterByCategory("Aretes"));
        btnBracelets.setOnClickListener(v -> filterByCategory("Pulseras"));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            List<Product> products = DatabaseHelper.getAllProducts();

            mainHandler.post(() -> {
                progressBar.setVisibility(View.GONE);
                allProducts.clear();
                allProducts.addAll(products);
                filterByCategory(currentCategory);
            });
        });
    }

    private void filterByCategory(String category) {
        currentCategory = category;
        filteredProducts.clear();

        if (category.equals("Todas")) {
            filteredProducts.addAll(allProducts);
        } else {
            for (Product product : allProducts) {
                if (product.getCategory().equals(category)) {
                    filteredProducts.add(product);
                }
            }
        }

        productAdapter.notifyDataSetChanged();
        highlightSelectedCategory(category);
    }

    private void searchProducts(String query) {
        if (query.isEmpty()) {
            filterByCategory(currentCategory);
            return;
        }

        filteredProducts.clear();
        String lowerQuery = query.toLowerCase();

        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(lowerQuery) ||
                    product.getDescription().toLowerCase().contains(lowerQuery)) {
                filteredProducts.add(product);
            }
        }

        productAdapter.notifyDataSetChanged();
    }

    private void highlightSelectedCategory(String category) {
        resetCategoryButtons();
        int highlightColor = ContextCompat.getColor(this, R.color.primary_light);

        switch (category) {
            case "Todas":
                btnAll.setBackgroundColor(highlightColor);
                break;
            case "Anillos":
                btnRings.setBackgroundColor(highlightColor);
                break;
            case "Collares":
                btnNecklaces.setBackgroundColor(highlightColor);
                break;
            case "Aretes":
                btnEarrings.setBackgroundColor(highlightColor);
                break;
            case "Pulseras":
                btnBracelets.setBackgroundColor(highlightColor);
                break;
        }
    }

    private void resetCategoryButtons() {
        int defaultColor = ContextCompat.getColor(this, R.color.background);
        btnAll.setBackgroundColor(defaultColor);
        btnRings.setBackgroundColor(defaultColor);
        btnNecklaces.setBackgroundColor(defaultColor);
        btnEarrings.setBackgroundColor(defaultColor);
        btnBracelets.setBackgroundColor(defaultColor);
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}