# Revisión de NotasApp

La aplicación está preparada para las pruebas finales en el emulador Android de esta computadora. Se comprobó el flujo real de login, Dashboard, creación, consulta y cierre de sesión contra la API del puerto 3000 y PostgreSQL. La versión final quedó instalada en el emulador.

## Causa y correcciones

- El emulador ejecuta Android 17/API 37 y la app apunta a SDK 37. Faltaba declarar y solicitar `ACCESS_LOCAL_NETWORK` para conectar a `http://10.0.2.2:3000/`. Se conserva esa URL. Se comprobó que aparece el diálogo del sistema. Referencia: https://developer.android.com/privacy-and-security/local-network-permission
- Los ViewModels convertían cualquier excepción en un error de conexión. Ahora distinguen conexión rechazada, tiempo agotado, permisos y problemas de formato; respetan la cancelación de corrutinas y muestran los mensajes JSON del backend cuando corresponde.
- Se validan respuestas incompletas de login, creación y listado antes de anunciar éxito. Se evitan solicitudes simultáneas repetidas desde el mismo ViewModel.
- El interceptor lee el token persistido en cada petición protegida. Una respuesta 401/403 limpia la sesión y las pantallas protegidas regresan al login. La sesión se excluye de las copias de seguridad.
- El listado permite reintentar y se actualiza al volver a la pantalla. Se restablece correctamente el mensaje de lista vacía después de un error.
- Se simplificó el bloque de dependencias y se retiró la declaración redundante de `okhttp-android`; se conserva OkHttp y las versiones que compilaron correctamente.
- El backend valida peticiones sin cuerpo y responde con JSON ante cuerpos JSON malformados o demasiado grandes. Tiene límites de espera de PostgreSQL y maneja desconexiones de clientes inactivos.
- `.env` se carga mediante una ruta relativa al backend, independientemente de la carpeta desde la que se invoque Node. No se modificaron ni mostraron sus valores.
- El servidor puede importarse para pruebas sin ocupar el puerto 3000; `npm.cmd start` lo inicia normalmente. No se cambiaron los endpoints.
- El SQL ya no contiene contraseñas ni usuarios predeterminados. No se ejecutó ese SQL ni se modificaron las tablas. Las cuentas existentes permanecen disponibles. Para un alta administrativa opcional se añadió `npm.cmd run crear-usuario`, que requiere `NOTASAPP_NOMBRE`, `NOTASAPP_EMAIL` y `NOTASAPP_PASSWORD` en el entorno y guarda un hash bcrypt. Este comando no se ejecutó contra las cuentas existentes.

Se confirmó que `Nota.kt` define la entidad y `NotaDatos.kt` define los cuerpos y respuestas HTTP; no son clases duplicadas. Se conserva MVVM con Repository, ViewModel y LiveData. Las pantallas mantienen Kotlin/XML, ConstraintLayout para login y creación, LinearLayout para Dashboard y listado, y RecyclerView con CardView/ConstraintLayout para las notas. No se agregaron credenciales ni listas simuladas a la aplicación.

## Archivos modificados o añadidos en esta revisión

Las rutas Java/Kotlin siguientes son relativas a `app/src/main/java/com/example/notasapp/`:

| Archivo | Cambio |
| --- | --- |
| `MainActivity.kt` | Solicitud del permiso local antes del login |
| `network/RetrofitClient.kt` | Tiempos de espera y limpieza de sesiones rechazadas |
| `network/ErroresApi.kt` (nuevo) | Mensajes de errores de red y HTTP |
| `model/LoginDatos.kt` | Token y usuario ausentes detectables |
| `model/NotaDatos.kt` | Nota o lista ausente detectable |
| `viewmodel/LoginViewModel.kt` | Validación, errores y cancelación |
| `viewmodel/NotasViewModel.kt` | Validación, errores y cancelación |
| `view/ActivityConSesion.kt` (nuevo) | Control compartido de acceso y navegación al login |
| `view/DashboardActivity.kt` | Control y cierre de sesión |
| `view/CrearNotaActivity.kt` | Control y recuperación de sesión vencida |
| `view/ListadoNotasActivity.kt` | Sesión, recarga y reintento |

Otros archivos:

| Archivo | Cambio |
| --- | --- |
| `app/build.gradle.kts` | Simplificación de dependencias |
| `app/src/main/AndroidManifest.xml` | Permiso de red local |
| `app/src/main/res/layout/activity_listado_notas.xml` | Botón de reintento |
| `app/src/main/res/xml/backup_rules.xml` | Exclusión de sesión |
| `app/src/main/res/xml/data_extraction_rules.xml` | Exclusión de sesión en copias y transferencias |
| `backend/db.js` | Ruta de configuración, tiempos de espera y errores del pool |
| `backend/server.js` | Configuración, errores JSON, escucha IPv4 y exportación para pruebas |
| `backend/routes/auth.js` | Validación de cuerpo ausente |
| `backend/routes/notas.js` | Validación de cuerpo ausente |
| `backend/package.json` | Comandos de inicio, pruebas y alta administrativa |
| `backend/crearUsuario.js` (nuevo) | Alta con credenciales externas y bcrypt |
| `backend/test/api.test.js` (nuevo) | Integración con PostgreSQL y reversión transaccional |
| `database/notasapp.sql` | Retiro de credenciales predeterminadas, sin cambios de esquema |
| `app/src/test/java/com/example/notasapp/ContratoJsonTest.kt` (nuevo) | Contratos JSON y respuestas incompletas |
| `app/src/androidTest/java/com/example/notasapp/FlujoRealTest.kt` (nuevo) | Flujo real y rechazo de token inválido |
| `REVISION_NOTASAPP.md` (nuevo) | Este informe |

El repositorio ya tenía cambios preparados, archivos nuevos y rutas antiguas `NotasApp/` marcadas como eliminadas antes de esta revisión. No se restauraron ni eliminaron esos archivos y no se hicieron commits ni pushes. La raíz del proyecto Android activo es la carpeta actual, con el módulo `app/`.

## Pruebas ejecutadas

| Comprobación | Resultado |
| --- | --- |
| API `http://localhost:3000/` | Responde correctamente |
| `node backend/probarconexion.js` | Conecta a `notasapp`, también desde la raíz |
| `npm.cmd test` en `backend/` | Correcto: login bcrypt real, credenciales incorrectas, JWT válido/inválido/vencido, validaciones, nota persistida y aislamiento de usuarios |
| Limpieza de datos de la prueba backend | Los registros temporales se revierten con `ROLLBACK`; no se borran datos existentes |
| `node --check` | Correcto para servidor, rutas modificadas y alta administrativa |
| `:app:assembleDebug` | Correcto |
| `:app:assembleDebugAndroidTest` | Correcto |
| `:app:testDebugUnitTest` | 4 pruebas, 0 fallos: 3 contratos JSON y la prueba de ejemplo existente |
| `:app:lintDebug` | 0 errores, 46 advertencias; principalmente textos en XML, presentación y mantenimiento |
| Instalación con ADB | Correcta, conservando datos |
| Diálogo de permiso de red local | Visible al pulsar iniciar sesión |
| Instrumentación: login, Dashboard, guardar, listar, cerrar sesión | Correcta contra el backend existente y PostgreSQL reales |
| Instrumentación final: JWT inválido y contexto Android | 2 pruebas correctas; sesión eliminada y regreso al login |
| Consulta directa posterior en PostgreSQL | Se confirmó la nota `Verificación Android 86f1244e` creada desde Android |

La prueba del flujo completo guarda una nota real identificada como verificación. Esa nota se conserva: no se borró información de la base de datos. El test recibe email y contraseña mediante argumentos de instrumentación; no tiene credenciales incorporadas. Si se ejecuta sin esos argumentos, omite únicamente el recorrido que requiere un usuario real. Las muestras JSON de pruebas unitarias no se utilizan en la aplicación.

## Pruebas finales y límites

1. Reiniciar el proceso del backend para que cargue los ajustes adicionales, usando `npm.cmd start` desde `backend/`. El proceso que ya estaba funcionando no se detuvo.
2. Abrir la app instalada, permitir el acceso a dispositivos cercanos/red local y entrar con una cuenta existente.
3. Crear una nota, consultar su contenido, cerrar sesión y comprobar el regreso al login.

No se probaron dispositivos físicos, otras redes, todas las versiones Android desde API 24 ni la compilación release firmada. Tampoco se comprobó visualmente cada combinación de orientación y tamaño de pantalla, ni se ejecutó el alta administrativa para evitar crear una cuenta persistente adicional. Las advertencias de Lint no impiden las pruebas finales.

En un teléfono físico, `10.0.2.2` no apunta a Windows: la URL actual está configurada para el emulador Android estándar.
