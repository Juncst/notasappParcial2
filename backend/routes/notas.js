const express = require('express');
const db = require('../db');
const verificarToken = require('../middleware/verificarToken');

const router = express.Router();

// Crear una nota
router.post('/', verificarToken, async (req, res) => {

    const { titulo, contenido } = req.body;

    if (
        typeof titulo !== 'string' ||
        typeof contenido !== 'string' ||
        !titulo.trim() ||
        !contenido.trim() ||
        titulo.trim().length > 200
    ) {
        return res.status(400).json({
            mensaje: 'Ingresa un título y contenido válidos'
        });
    }

    try {
        const resultado = await db.query(
            `INSERT INTO notas (usuario_id, titulo, contenido)
             VALUES ($1, $2, $3)
             RETURNING *`,
            [req.usuarioId, titulo.trim(), contenido.trim()]
        );

        res.status(201).json({
            mensaje: 'Nota guardada correctamente',
            nota: resultado.rows[0]
        });

    } catch (error) {
        console.log(error.message);

        res.status(500).json({
            mensaje: 'Error al guardar la nota'
        });
    }

});

// Consultar las notas del usuario
router.get('/', verificarToken, async (req, res) => {

    try {
        const resultado = await db.query(
            `SELECT id, titulo, contenido, fecha_creacion
             FROM notas
             WHERE usuario_id = $1
             ORDER BY fecha_creacion DESC, id DESC`,
            [req.usuarioId]
        );

        res.json({
            mensaje: 'Notas obtenidas correctamente',
            notas: resultado.rows
        });

    } catch (error) {
        console.log(error.message);

        res.status(500).json({
            mensaje: 'Error al consultar las notas'
        });
    }

});

module.exports = router;