// Alta administrativa opcional; las credenciales se reciben del entorno.
const bcrypt = require('bcrypt');
const db = require('./db');

async function crearUsuario() {
    try {
        const nombre = process.env.NOTASAPP_NOMBRE?.trim();
        const email = process.env.NOTASAPP_EMAIL?.trim().toLowerCase();
        const password = process.env.NOTASAPP_PASSWORD;
        if (!nombre || !email || !password || nombre.length > 100 || email.length > 150) {
            throw new Error('Configura NOTASAPP_NOMBRE, NOTASAPP_EMAIL y NOTASAPP_PASSWORD válidos en el entorno');
        }
        const hash = await bcrypt.hash(password, 12);
        const resultado = await db.query(
            'INSERT INTO usuarios (nombre, email, password_hash) VALUES ($1, $2, $3) RETURNING id',
            [nombre, email, hash]
        );
        console.log('Usuario creado. ID:', resultado.rows[0].id);
    } catch (error) {
        console.error(error.code === '23505' ? 'El correo ya está registrado' : 'No se pudo crear el usuario. Verifica las variables de entorno y la conexión.');
        process.exitCode = 1;
    } finally {
        await db.end();
    }
}

crearUsuario();
