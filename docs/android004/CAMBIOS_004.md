# Cambios Android 004 · SimpleSoft Converter 0.2.0

## Base y conservación
Actualización directa del código de Android 003. Mismo package, mínimo API 26, seis categorías, favoritos, orden manual, estadísticas, frecuentes por frecuencia/recencia, preferencias, copia de resultados y temas. Se conserva la base canónica en intercambios, sin analizar de nuevo el texto redondeado.

El nombre de base de datos `converter.db`, el esquema Room v1 y el DataStore `simplesoft_settings` no cambian. Las claves existentes se conservan; se añaden `family` y `reduce_motion`, con valores seguros por defecto. No se usa migración destructiva. Al actualizar una instalación firmada con la misma clave, los datos deben mantenerse. La comprobación de persistencia real tras actualización en tu teléfono sigue pendiente.

## Añadido
- Módulo Android reutilizable `:simplesoft-core`, sin permisos ni SDK de anuncios.
- Único JSON fuente de mensajes en assets de Core. Cada APK recibe su copia al compilar; no hay sincronización ni almacenamiento compartido entre aplicaciones.
- Banner verde de matriz 5×7, desplazamiento de derecha a izquierda y rotación al salir el mensaje. 30 dp/segundo, redibujado limitado aproximadamente a 30 Hz. Se pausa fuera de RESUMED y se oculta mientras está abierto el teclado.
- Modo estático con fuente monoespaciada y ajuste de línea si el usuario reduce movimiento, Android desactiva animaciones o está activa la exploración táctil de accesibilidad. Sin cambios automáticos de texto en ese modo. Los alfabetos no soportados por la matriz también usan representación estática nativa.
- Contenedor con slot explícito para contenido futuro, sin integrar publicidad ni conexiones.
- Icono adaptativo propio: monograma vectorizado y flechas, fondo blanco, variante monocromática para Android 13+. SVG editable y recursos nativos incluidos. PNG oficial intacto, usado en Ajustes y Splash.
- API SplashScreen compatible con API 26. Imagen original centrada en un contenedor de 144 dp dentro del espacio de icono del sistema. Fondo claro/oscuro según sistema; el PNG conserva su superficie blanca. Solo se mantiene durante la carga local real, hasta datos listos o error; no hay temporizador ni pantalla adicional.
- Familias Físicas/Técnicas/Cotidianas en pestañas desplazables. Las familias futuras muestran información, no conversiones ni botones inoperantes. Favoritos y frecuentes siguen accesibles; tocarlos vuelve a su familia y categoría. Última familia y último par por categoría persistentes.
- Presión: Pa, kPa, bar, mbar, atm estándar, psi y mmHg convencional.
- Catálogo, categorías/familias, reglas lineales/afines y presentación en archivos separados. Las equivalencias futuras de tallas no se tratan como factores universales.

## Presión y precisión
Base: pascal. 1 bar=100000 Pa; 1 mbar=100 Pa; 1 atm=101325 Pa. Psi se calcula de 0.45359237 kg × 9.80665 m/s² / (0.0254 m)² con DECIMAL128. mmHg convencional=13595.1 kg/m³ × 9.80665 m/s² × 0.001 m=133.322387415 Pa. No es torr: 760 mmHg convencionales no se fuerza a 1 atm. No se cambia el significado a presión manométrica ni se convierte entre presión absoluta y relativa.

## Dependencias y compatibilidad
Se mantienen AGP 8.13.0, Gradle 8.13, Kotlin 2.2.0 y BOM Compose 2025.10.01. Se añade core-splashscreen 1.0.1 (compatibilidad de arranque), no un framework pesado. org.json se usa desde Android y se añade una implementación solo a los tests JVM del módulo Core. Room v1 no cambia.

VersionName=0.2.0 solicitado; versionCode=4 para actualizar sobre 003 (cuyo nombre visible era 0.3.0). La firma, no el nombre de versión, determina si Android permite actualizar conservando datos.

## Pendiente deliberadamente
Energía, potencia, tiempo, almacenamiento digital, cocina y tallas; publicidad real; traducciones completas; publicación. Ninguna aparece como conversión disponible. La revisión visual/end-to-end en teléfono, incluido arranque y TalkBack, se distingue de compilación/tests en VERIFICACION_004.md.
