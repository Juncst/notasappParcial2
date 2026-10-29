require('dotenv').config();

const express = require('express');
const authRoutes = require('./routes/auth');
const notasRoutes = require('./routes/notas');

const app = express();
const PORT = process.env.PORT || 3000;

if (!process.env.JWT_SECRET) {
    console.error('Falta configurar JWT_SECRET');
    process.exit(1);
}

app.use(express.json());

// Ruta principal
app.get('/', (req, res) => {
    res.json({
        mensaje: 'API de NotasApp funcionando'
    });
});

// Rutas de la API
app.use('/api/auth', authRoutes);
app.use('/api/notas', notasRoutes);

// Iniciar servidor
app.listen(PORT, () => {
    console.log(`Servidor iniciado en http://localhost:${PORT}`);
});