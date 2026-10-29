const jwt = require('jsonwebtoken');

function verificarToken(req, res, next) {

    const autorizacion = req.headers.authorization;

    if (!autorizacion || !autorizacion.startsWith('Bearer ')) {
        return res.status(401).json({
            mensaje: 'Token no proporcionado'
        });
    }

    const token = autorizacion.split(' ')[1];

    try {
        const datos = jwt.verify(token, process.env.JWT_SECRET);

        req.usuarioId = datos.id;

        next();

    } catch (error) {
        return res.status(403).json({
            mensaje: 'Token inválido o expirado'
        });
    }
}

module.exports = verificarToken;