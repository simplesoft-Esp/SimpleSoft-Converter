# Verificación Android 004 · v0.2.0
10 de noviembre de 2026. Actualización sobre código Android 003.

## Resultados ejecutados realmente
- Linux, JDK Temurin 17, Gradle 8.13, AGP 8.13.0, SDK Platform 36, Build Tools 35.
- `assembleDebug testDebugUnitTest`: **BUILD SUCCESSFUL**. Una primera pasada compiló ambos módulos y ejecutó las pruebas; una segunda pasada final recompiló recursos modificados y confirmó el proyecto, reutilizando tareas de tests sin cambios. Ambos registros se incluyen en docs/verificacion004.
- **45 pruebas, 0 fallos, 0 errores, 0 omitidas**:
  - 22 del motor/ranking de 003, conservadas. El recorrido de todos los pares incluye también presión.
  - 6 de presión/familias: prefijos, atm estándar, mmHg convencional distinto de torr, psi por definición, intercambio canónico y familias futuras no funcionales.
  - 13 de Core: archivo JSON real, ausencia, archivo vacío/malformado, esquema futuro, mensajes desactivados/duplicados, altas sin cambio Kotlin, tamaño y entradas inválidas, ciclo de mensajes, política de movimiento y glifos.
  - 4 de ViewModel con repositorio en memoria: confirmar una vez, intercambio repetido, preferencias, orden de favoritos y exclusión de frecuentes, borrado separado, reapertura conservando par/familia sin valores y desactivar aprendizaje.
- **APK debug generada**, 13801082 bytes; archivo independiente `SimpleSoft-Converter-0.2.0-debug.apk`.
- AAPT confirma package `com.simplesoft.converter`, versionCode **4**, versionName **0.2.0**, minSdk **26**, targetSdk **36**.
- APK inspeccionada: JSON con cinco mensajes, iconos adaptativos v26 y monocromáticos v33, recursos de splash y PNG. Sin permiso INTERNET ni AD_ID. La única uses-permission es la protección de receptores internos DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION generada por AndroidX, sin acceso a red ni datos personales.
- Logotipo: bytes SHA-256 idénticos al PNG de Android 003. Se movió a drawable-nodpi para evitar decodificación ampliada por densidad; no se modificó el archivo original.
- Esquema Room v1 JSON idéntico a 003. Mismos nombres de DB, DataStore y claves anteriores; sin migración destructiva.
- SVG del icono renderizado e inspeccionado como imagen; manifiesto configura launcher propio. Esto no sustituye la comprobación del enmascarado en launchers reales.

## Límites y comprobaciones pendientes
**No se ha instalado ni ejecutado esta revisión en teléfono o emulador.** No se afirma validación end-to-end de splash, pantalla, banner, TalkBack, teclado, rotación ni actualización con datos reales. Los tests de ViewModel usan repositorio en memoria; no prueban físicamente Room/DataStore ni la actualización de la instalación de Windows. La comparación de esquema reduce el riesgo de incompatibilidad, pero la conservación real de datos debe comprobarse con la misma firma en el teléfono.

El ciclo del banner y la decisión de movimiento se prueban como lógica; no se ha medido fluidez, FPS, batería o consumo en hardware. La imagen del icono se ha revisado fuera de un launcher Android. Las comprobaciones manuales están en LEEME_WINDOWS_004.md.

## Comportamiento conocido y decisiones
- Modo estático muestra el primer mensaje, con fuente monoespaciada y ajuste de línea; no rota para no forzar atención. Alfabetos no soportados por la matriz usan texto nativo estático.
- JSON inválido/ausente: se oculta el espacio, sin frases de respaldo ni bloqueo.
- PNG original con fondo blanco sobre superficie de arranque clara/oscura. El splash usa el espacio y máscara del sistema; el launcher usa el icono específico del conversor. Sin espera artificial.
- Familias futuras informativas, no conversiones implementadas. No se inventan equivalencias de tallas.
- VersionName 0.2.0 es el pedido; versionCode 4 permite actualizar sobre el 3 de Android 003.
- APK independiente firmada con clave debug de este entorno: para conservar datos de una instalación firmada en Windows, compilar allí con la misma clave de 003. No desinstalar la anterior por un conflicto de firma.

Advertencias de compilación no bloqueantes: API de portapapeles Compose heredada de 003, opciones kapt de tests no utilizadas y símbolos nativos sin retirar en dependencias. No se han incorporado anuncios, analítica ni servicios.

## Integridad
SHA-256 APK: `c5345b0a541ae9e47ad5a137adf6d3cbfb2317f645cb4fddddb2739ffc3046d5`.
El ZIP incluye SHA256SUMS.txt de fuentes, wrapper Gradle con checksum de distribución, recursos, esquema Room, JSON, SVG editable, documentación e informes. Excluye builds/caches, local.properties y claves. Android 003 no se sobrescribe. La APK se entrega por separado.
