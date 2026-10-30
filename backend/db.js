require('dotenv').config({ path: require('node:path').join(__dirname, '.env'), quiet: true });

const { Pool } = require('pg');

const db = new Pool({
    host: process.env.DB_HOST,
    port: process.env.DB_PORT,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    connectionTimeoutMillis: 5000,
    query_timeout: 10000
});

// Evita que una desconexión de un cliente inactivo termine el servidor.
db.on('error', () => console.error('Se perdió una conexión inactiva con PostgreSQL'));

module.exports = db;
