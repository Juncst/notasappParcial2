require('dotenv').config({ path: require('node:path').join(__dirname, '.env'), quiet: true });

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

app.use((error, req, res, next) => {
    if (error.type === 'entity.parse.failed') {
        return res.status(400).json({ mensaje: 'El cuerpo debe ser JSON válido' });
    }
    if (error.type === 'entity.too.large') {
        return res.status(413).json({ mensaje: 'La petición es demasiado grande' });
    }
    console.error('Error interno al procesar la petición');
    res.status(500).json({ mensaje: 'Error interno del servidor' });
});

// Importar la app permite probar las rutas sin ocupar el puerto de desarrollo.
if (require.main === module) {
    app.listen(PORT, '0.0.0.0', () => {
        console.log(`Servidor iniciado en http://localhost:${PORT}`);
    });
}

module.exports = app;
