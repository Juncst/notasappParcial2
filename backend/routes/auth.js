const express = require('express');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const db = require('../db');

const router = express.Router();

router.post('/login', async (req, res) => {

    const { email, password } = req.body || {};

    if (!email || !password) {
        return res.status(400).json({
            mensaje: 'Ingresa tu correo y contraseña'
        });
    }

    if (typeof email !== 'string' || typeof password !== 'string') {
        return res.status(400).json({
            mensaje: 'Datos incorrectos'
        });
    }

    try {
        // Buscamos al usuario en PostgreSQL
        const resultado = await db.query(
            'SELECT * FROM usuarios WHERE email = $1',
            [email.trim().toLowerCase()]
        );

        if (resultado.rows.length === 0) {
            return res.status(401).json({
                mensaje: 'Correo o contraseña incorrectos'
            });
        }

        const usuario = resultado.rows[0];

        // Comparamos la contraseña con el hash guardado
        const contraseñaValida = await bcrypt.compare(
            password,
            usuario.password_hash
        );

        if (!contraseñaValida) {
            return res.status(401).json({
                mensaje: 'Correo o contraseña incorrectos'
            });
        }

        // Generamos el token JWT
        const token = jwt.sign(
            { id: usuario.id },
            process.env.JWT_SECRET,
            { expiresIn: '2h' }
        );

        res.json({
            mensaje: 'Inicio de sesión correcto',
            token: token,
            usuario: {
                id: usuario.id,
                nombre: usuario.nombre,
                email: usuario.email
            }
        });

    } catch (error) {
        console.log(error.message);

        res.status(500).json({
            mensaje: 'Error interno del servidor'
        });
    }

});

module.exports = router;
