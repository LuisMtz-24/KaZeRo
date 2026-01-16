// server.js - API REST para KaZeRo con mejor manejo de conexiones
const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');
const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// Configuración mejorada de la conexión con timeouts más largos
const pool = mysql.createPool({
  host: 'tramway.proxy.rlwy.net',
  port: 3306,
  user: 'root',
  password: 'TjaZprBUqjXhNXihY',
  database: 'kazero_db',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
  // Timeouts aumentados para conexiones lentas
  connectTimeout: 60000, // 60 segundos
  acquireTimeout: 60000,
  timeout: 60000,
  // Configuraciones adicionales para estabilidad
  enableKeepAlive: true,
  keepAliveInitialDelay: 0
});

// Test de conexión mejorado
app.get('/api/test', async (req, res) => {
  let connection;
  try {
    console.log('🔍 Probando conexión a base de datos...');
    console.log('📡 Host:', pool.config.connectionConfig.host);
    console.log('🔌 Puerto:', pool.config.connectionConfig.port);

    connection = await pool.getConnection();
    console.log('✅ Conexión obtenida del pool');

    const [result] = await connection.execute('SELECT 1 as test, NOW() as time');
    console.log('✅ Query ejecutado exitosamente:', result);

    res.json({
      success: true,
      message: 'Database connected!',
      timestamp: result[0].time,
      host: pool.config.connectionConfig.host
    });
  } catch (error) {
    console.error('❌ Error detallado:', {
      message: error.message,
      code: error.code,
      errno: error.errno,
      sqlState: error.sqlState
    });
    res.status(500).json({
      success: false,
      error: error.message,
      code: error.code
    });
  } finally {
    if (connection) connection.release();
  }
});

// ========== USUARIOS ==========

app.post('/api/users/register', async (req, res) => {
  let connection;
  try {
    const { username, password, email, address, postal_code } = req.body;
    console.log('📝 Registrando usuario:', username);

    connection = await pool.getConnection();
    const [result] = await connection.execute(
      'INSERT INTO users (username, password, email, address, postal_code) VALUES (?, ?, ?, ?, ?)',
      [username, password, email, address, postal_code]
    );

    console.log('✅ Usuario registrado, ID:', result.insertId);
    res.json({ success: true, userId: result.insertId });
  } catch (error) {
    console.error('❌ Error registrando usuario:', error.message);
    res.status(500).json({ success: false, error: error.message });
  } finally {
    if (connection) connection.release();
  }
});

app.post('/api/users/login', async (req, res) => {
  let connection;
  try {
    const { username, password } = req.body;
    console.log('🔐 Login intento:', username);

    connection = await pool.getConnection();
    const [rows] = await connection.execute(
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
  } finally {
    if (connection) connection.release();
  }
});

app.get('/api/users/:id', async (req, res) => {
  let connection;
  try {
    connection = await pool.getConnection();
    const [rows] = await connection.execute(
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
  } finally {
    if (connection) connection.release();
  }
});

app.put('/api/users/:id', async (req, res) => {
  let connection;
  try {
    const { email, address, postal_code } = req.body;
    console.log('📝 Actualizando usuario ID:', req.params.id);

    connection = await pool.getConnection();
    await connection.execute(
      'UPDATE users SET email = ?, address = ?, postal_code = ? WHERE id = ?',
      [email, address, postal_code, req.params.id]
    );

    console.log('✅ Usuario actualizado');
    res.json({ success: true });
  } catch (error) {
    console.error('❌ Error actualizando usuario:', error.message);
    res.status(500).json({ success: false, error: error.message });
  } finally {
    if (connection) connection.release();
  }
});

// ========== PRODUCTOS ==========

app.get('/api/products', async (req, res) => {
  let connection;
  try {
    console.log('📦 Obteniendo todos los productos...');

    connection = await pool.getConnection();
    const [rows] = await connection.execute(
      'SELECT * FROM products ORDER BY created_at DESC'
    );

    console.log(`✅ ${rows.length} productos encontrados`);
    res.json({ success: true, products: rows });
  } catch (error) {
    console.error('❌ Error obteniendo productos:', error.message);
    res.status(500).json({ success: false, error: error.message });
  } finally {
    if (connection) connection.release();
  }
});

app.get('/api/products/category/:category', async (req, res) => {
  let connection;
  try {
    connection = await pool.getConnection();
    const [rows] = await connection.execute(
      'SELECT * FROM products WHERE category = ? ORDER BY name',
      [req.params.category]
    );
    res.json({ success: true, products: rows });
  } catch (error) {
    console.error('❌ Error obteniendo productos por categoría:', error.message);
    res.status(500).json({ success: false, error: error.message });
  } finally {
    if (connection) connection.release();
  }
});

app.get('/api/products/:id', async (req, res) => {
  let connection;
  try {
    connection = await pool.getConnection();
    const [rows] = await connection.execute(
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
  } finally {
    if (connection) connection.release();
  }
});

app.get('/api/products/search/:query', async (req, res) => {
  let connection;
  try {
    const searchTerm = `%${req.params.query}%`;
    connection = await pool.getConnection();
    const [rows] = await connection.execute(
      'SELECT * FROM products WHERE name LIKE ? OR description LIKE ? ORDER BY name',
      [searchTerm, searchTerm]
    );
    res.json({ success: true, products: rows });
  } catch (error) {
    console.error('❌ Error buscando productos:', error.message);
    res.status(500).json({ success: false, error: error.message });
  } finally {
    if (connection) connection.release();
  }
});

// Manejo de errores global
app.use((err, req, res, next) => {
  console.error('❌ Error no manejado:', err);
  res.status(500).json({
    success: false,
    error: 'Internal server error',
    message: err.message
  });
});

// Puerto
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`\n🚀 ========================================`);
  console.log(`   KaZeRo API corriendo en puerto ${PORT}`);
  console.log(`   http://localhost:${PORT}/api/test`);
  console.log(`========================================\n`);

  // Test de conexión inicial
  pool.getConnection()
    .then(connection => {
      console.log('✅ Pool de conexiones inicializado correctamente');
      connection.release();
    })
    .catch(err => {
      console.error('❌ Error inicializando pool de conexiones:');
      console.error('   Código:', err.code);
      console.error('   Mensaje:', err.message);
    });
});