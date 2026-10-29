const db = require('./db');

async function probarConexion() {
    try {
        const resultado = await db.query(
            'SELECT current_database() AS base'
        );

        console.log('Conectado a:', resultado.rows[0].base);

    } catch (error) {
        console.log('Error de conexión:', error.message);

    } finally {
        await db.end();
    }
}

probarConexion();