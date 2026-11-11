# Windows — Android 005 · v0.3.0

1. Conserva las carpetas y archivos de Android 003/004. Extrae `SimpleSoft-Converter-Android-005.zip` en una carpeta nueva, por ejemplo `C:\Users\acido\AndroidStudioProjects\SimpleSoftConverter005`.
2. En Android Studio, File → Open, selecciona la carpeta interna `SimpleSoftConverter` que contiene settings.gradle.kts. Deben aparecer :app y :simplesoft-core. No extraigas encima de 004.
3. Usa Embedded JDK 17 o 21. Instala SDK Platform 36 y las herramientas que solicite Gradle. Android Studio generará local.properties con la ruta de tu equipo; no copies el de otra máquina.
4. Terminal de la raíz:
```
.\gradlew.bat assembleDebug testDebugUnitTest
```
Esta orden compila y ejecuta los tests de ambos módulos. La primera sincronización descarga dependencias; la aplicación y el widget instalados funcionan sin conexión.
5. Conecta tu móvil con depuración USB y pulsa Run. También puedes instalar el APK generado en `app\build\outputs\apk\debug\app-debug.apk`.
6. Para Mini: pantalla de inicio → mantener pulsado → Widgets → SimpleSoft Converter Mini. Reserva espacio amplio, aproximadamente 4 × 6 celdas. No se añade automáticamente al launcher. Consulta WIDGET_005.md para las pruebas de interactividad.

## Conservar firma y datos de 003/004
Package: com.simplesoft.converter, versionCode **5**, versionName **0.3.0**. Es superior al versionCode 4 de Android 004. El esquema Room, nombres de almacenamiento y IDs previos se conservan.

Compila en el mismo Windows con la **misma clave debug** que usaste para instalar 003/004. Android Studio normalmente reutiliza `%USERPROFILE%\.android\debug.keystore`; conserva ese archivo y no lo compartas. Abrir el código en una carpeta nueva no exige una firma nueva. No borres datos ni desinstales la versión anterior para probar la actualización.

Una APK independiente de esta entrega puede estar firmada con otra clave de depuración. Si Android rechaza su actualización por firma, usa la que compiles tú con tu clave habitual. **No desinstales 004 para resolverlo si quieres conservar datos.**

Antes de fusionar modificaciones propias, haz copia o commit. Conserva .idea, configuraciones Run, claves, local.properties y ajustes personales. Esta entrega se abre completa y no modifica tu carpeta de Windows automáticamente.

## Idiomas y pruebas
No hay selector manual de idioma: Android español → es; inglés o cualquier otro idioma principal → en. Cambiar idioma recrea la actividad, manteniendo preferencias. Los widgets reciben actualización de locale y se refrescan también en cada interacción.

Comprueba tras actualizar: favoritos/orden/último par anteriores, buscador, las siete categorías, banner en ambos idiomas y Mini. Las pruebas unitarias no sustituyen esta comprobación en el teléfono. Instrucciones detalladas en WIDGET_005.md y resultados ejecutados en VERIFICACION_005.md.
