// server.js - API REST para KaZeRo
const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');
const app = express();

// Middleware
app.use(cors());
app.use(express.json());

const pool = mysql.createPool({
  host: 'tramway.proxy.rlwy.net',
  port: 3306,
  user: 'root',
  password: 'TjaZprBUqjXhNXihYezGiNZyRywSIGKS',
  database: 'kazero_db',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
});

app.post('/api/users/register', async (req, res) => {
  try {
    const { username, password, email, address, postal_code } = req.body;

    console.log('📝 Registrando usuario:', username);

    const [result] = await pool.execute(
      'INSERT INTO users (username, password, email, address, postal_code) VALUES (?, ?, ?, ?, ?)',
      [username, password, email, address, postal_code]
    );

    console.log('✅ Usuario registrado, ID:', result.insertId);
    res.json({ success: true, userId: result.insertId });
  } catch (error) {
    console.error('❌ Error registrando usuario:', error.message);
    res.status(500).json({ success: false, error: error.message });
  }
});

app.post('/api/users/login', async (req, res) => {
  try {
    const { username, password } = req.body;

    console.log('🔐 Login intento:', username);

    const [rows] = await pool.execute(
      'SELECT id, username, email, address, postal_code FROM users WHERE username = ? AND password = ?',
      [username, password]
    );

    if (rows.length > 0) {
      console.log('✅ Login exitoso:', username);
      res.json({ success: true, user: rows[0] });
    } else {
      console.log('❌ Credenciales inválidas');
      res.json({ success: false, error: 'Invalid credentials' });
    }
  } catch (error) {
    console.error('❌ Error en login:', error.message);
    res.status(500).json({ success: false, error: error.message });
  }
});

app.get('/api/users/:id', async (req, res) => {
  try {
    const [rows] = await pool.execute(
      'SELECT id, username, email, address, postal_code FROM users WHERE id = ?',
      [req.params.id]
    );

    if (rows.length > 0) {
      res.json({ success: true, user: rows[0] });
    } else {
      res.status(404).json({ success: false, error: 'User not found' });
    }
  } catch (error) {
    console.error('❌ Error obteniendo usuario:', error.message);
    res.status(500).json({ success: false, error: error.message });
  }
});

// Actualizar usuario
app.put('/api/users/:id', async (req, res) => {
  try {
    const { email, address, postal_code } = req.body;

    console.log('📝 Actualizando usuario ID:', req.params.id);

    await pool.execute(
      'UPDATE users SET email = ?, address = ?, postal_code = ? WHERE id = ?',
      [email, address, postal_code, req.params.id]
    );

    console.log('✅ Usuario actualizado');
    res.json({ success: true });
  } catch (error) {
    console.error('❌ Error actualizando usuario:', error.message);
    res.status(500).json({ success: false, error: error.message });
  }
});

// ========== ENDPOINTS PRODUCTOS ==========

// Obtener todos los productos
app.get('/api/products', async (req, res) => {
  try {
    console.log('📦 Obteniendo todos los productos...');

    const [rows] = await pool.execute(
      'SELECT * FROM products ORDER BY created_at DESC'
    );

    console.log(`✅ ${rows.length} productos encontrados`);
    res.json({ success: true, products: rows });
  } catch (error) {
    console.error('❌ Error obteniendo productos:', error.message);
    res.status(500).json({ success: false, error: error.message });
  }
});

// Obtener productos por categoría
app.get('/api/products/category/:category', async (req, res) => {
  try {
    const [rows] = await pool.execute(
      'SELECT * FROM products WHERE category = ? ORDER BY name',
      [req.params.category]
    );
    res.json({ success: true, products: rows });
  } catch (error) {
    console.error('❌ Error obteniendo productos por categoría:', error.message);
    res.status(500).json({ success: false, error: error.message });
  }
});

// Obtener producto por ID
app.get('/api/products/:id', async (req, res) => {
  try {
    const [rows] = await pool.execute(
      'SELECT * FROM products WHERE id = ?',
      [req.params.id]
    );

    if (rows.length > 0) {
      res.json({ success: true, product: rows[0] });
    } else {
      res.status(404).json({ success: false, error: 'Product not found' });
    }
  } catch (error) {
    console.error('❌ Error obteniendo producto:', error.message);
    res.status(500).json({ success: false, error: error.message });
  }
});

// Buscar productos
app.get('/api/products/search/:query', async (req, res) => {
  try {
    const searchTerm = `%${req.params.query}%`;
    const [rows] = await pool.execute(
      'SELECT * FROM products WHERE name LIKE ? OR description LIKE ? ORDER BY name',
      [searchTerm, searchTerm]
    );
    res.json({ success: true, products: rows });
  } catch (error) {
    console.error('❌ Error buscando productos:', error.message);
    res.status(500).json({ success: false, error: error.message });
  }
});

// Test endpoint
app.get('/api/test', async (req, res) => {
  try {
    console.log('🔍 Probando conexión a base de datos...');
    await pool.execute('SELECT 1');
    console.log('✅ Conexión a base de datos exitosa');
    res.json({ success: true, message: 'Database connected!' });
  } catch (error) {
    console.error('❌ Error de conexión a base de datos:', error.message);
    res.status(500).json({ success: false, error: error.message });
  }
});

// Puerto
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`\n🚀 ========================================`);
  console.log(`   KaZeRo API corriendo en puerto ${PORT}`);
  console.log(`   http://localhost:${PORT}/api/test`);
  console.log(`========================================\n`);
});