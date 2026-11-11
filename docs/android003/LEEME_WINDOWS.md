# SimpleSoft Converter — Android 003

Aplicación Android nativa, Kotlin + Jetpack Compose. Identificador `com.simplesoft.converter`, mínimo Android 8/API 26. No necesita servidor, cuenta ni conexión. Sin anuncios y sin permiso INTERNET.

## Abrir sin tocar el proyecto existente (recomendado)
1. Cierra Android Studio. Haz una copia completa de `C:\Users\acido\AndroidStudioProjects\SimpleSoftConverter` a una carpeta de respaldo con fecha. No borres el proyecto original.
2. Descarga el ZIP y extráelo, por ejemplo, en `C:\Users\acido\AndroidStudioProjects\SimpleSoftConverter003`. Abre la carpeta que contiene `settings.gradle.kts` (hay una carpeta SimpleSoftConverter dentro del ZIP).
3. Android Studio → File → Open. Selecciona esta nueva carpeta. Acepta la sincronización de Gradle.
4. En SDK Manager instala Android SDK Platform 36 y las herramientas solicitadas por Gradle. Para Gradle JDK selecciona Embedded JDK 17 o 21. No se incluye `local.properties`: Android Studio lo genera con tu ruta de SDK.
5. En PowerShell, dentro de la carpeta raíz del proyecto:
   `.\gradlew.bat testDebugUnitTest assembleDebug`.
6. El APK de depuración se genera en `app\build\outputs\apk\debug\app-debug.apk`. Conecta el teléfono con depuración USB y pulsa Run en Android Studio, o utiliza un emulador API 26 o superior.
7. La primera sincronización descarga dependencias; la aplicación instalada funciona sin conexión.

No cambies firmas ni desinstales una versión con datos sin copia. El package es el mismo: una instalación con firma distinta puede pedir desinstalar la anterior. No se incluye clave de firma de publicación.

## Integrar en tu proyecto local conservando configuraciones
No puedo inspeccionar desde aquí tu carpeta de Windows: la entrega es un proyecto independiente, no un parche automático a archivos locales no vistos.

Después de probar la copia:
- Guarda un respaldo o commit de tu proyecto original. Conserva `.idea`, `local.properties`, configuraciones de Run, claves/keystores, credenciales, `gradle.properties` local y cualquier código propio.
- Compara los dos proyectos con Android Studio o una herramienta de diferencias. Lleva los archivos Kotlin bajo `app/src/main/java/com/simplesoft/converter/`, las pruebas y los recursos; elimina únicamente clases de ejemplo que tú verifiques que han sido sustituidas (p. ej. el MainActivity de plantilla). No mantengas dos clases con el mismo package y nombre.
- Fusiona el AndroidManifest: actividad principal, tema y política de copia. No sobreescribas permisos/componentes propios sin revisarlos.
- Fusiona los plugins y dependencias de `app/build.gradle.kts` y raíz. Esta entrega usa versiones directas y no necesita `libs.versions.toml`; si tu proyecto usa catálogo, traduce estas entradas al catálogo, sin declarar cada plugin dos veces. Room necesita el plugin kapt y su dependencia de procesador.
- Mantén el wrapper y versiones del proyecto como un conjunto compatible. No mezcles el AGP de una copia con un Gradle antiguo. Para reproducir esta entrega usa AGP 8.13.0, Gradle 8.13 y Kotlin/Compose plugin 2.2.0; guarda los anteriores antes de sustituirlos.
- No copies carpetas `.gradle`, `.idea`, `build` ni `local.properties` desde otra máquina. Ejecuta las pruebas y compila otra vez.
- Si hay conflictos, trabaja en la copia y conserva el original hasta verificar el resultado.

## Funciones
- Longitud, masa, temperatura, volumen, superficie y velocidad. Galones US e imperial etiquetados; onzas y libras avoirdupois.
- Conversión inmediata, coma o punto decimal, errores de entrada, cero absoluto y resultado con hasta 10 cifras significativas. El símbolo ≈ señala redondeo de presentación.
- Intercambio trasladando resultado sin redondear; base canónica en memoria para evitar pérdida en intercambios sucesivos. Editar la entrada crea un nuevo valor.
- Favoritos persistentes en Room, sin duplicados direccionales y con orden manual Subir/Bajar. Nunca se reordenan por uso.
- Frecuentes separados, ranking por frecuencia y recencia (semivida 14 días). Confirmar uso con Hecho, copiar, cambiar par/categoría, cerrar teclado o pasar a segundo plano. Cada edición se cuenta como máximo una vez; rotación/restauración no suma usos.
- Recientes: últimos 20 pares en Ajustes. No se guardan valores introducidos ni resultados en disco.
- DataStore conserva tema, aprendizaje, último par global y último par de cada categoría. Modo sistema/claro/oscuro.
- Controles de borrado separados con confirmación. Las copias automáticas de Android están deshabilitadas. Sin telemetría.

Los cambios de datos se guardan con una cola serial y transacciones para estadísticas y reordenación. La rotación conserva el borrador mediante ViewModel; tras muerte de proceso, la entrada vuelve vacía y el par se restaura del almacenamiento.

## Estructura
- `domain/Conversion.kt`: catálogo, BigDecimal/DECIMAL128, validación, formato y ranking, sin dependencias Android.
- `data/LocalRepository.kt`: Room, DataStore, actualizaciones atómicas.
- `feature/ConverterViewModel.kt`: StateFlow y coordinación de eventos.
- `ui/ConverterApp.kt`: Compose, temas monocromos, selectores, favoritos y ajustes.
- `app/src/test/`: pruebas JUnit del motor y ranking.
- `REFERENCIAS.md`: fuentes y definiciones de factores.
- `VERIFICACION.md`: resultados reales de la validación de esta entrega.

## Comprobación manual recomendada en tu teléfono
1. Primera apertura vacía, metros → pies. Probar 2,5 km → m = 2500.
2. Escribir 1 m → ft e intercambiar repetidas veces; al volver a m se conserva 1.
3. Celsius/Fahrenheit/Kelvin: 0 °C = 32 °F; −273,15 °C = 0 K; rechazar valores inferiores.
4. Distinguir 1 gal US = 3,785411784 L de 1 gal imp = 4,54609 L.
5. Fijar pares, cambiar orden, cerrar/reabrir: persisten. Los frecuentes no duplican favoritos.
6. Escribir varios dígitos y pulsar Hecho/cambiar de par: cuenta una operación. Rotar no cuenta. Desactivar aprendizaje impide nuevos registros.
7. Probar ambos temas, letra grande, TalkBack, rotación y teclado abierto.
8. Borrar estadísticas conserva favoritos; vaciar favoritos conserva estadísticas; reiniciar preferencias conserva ambos.
9. Activar modo avión: todas las funciones siguen operativas.

No se ha publicado nada. El PNG oficial se conserva íntegro en Ajustes. No se ha diseñado un nuevo icono de launcher ni se ha alterado el logotipo.
