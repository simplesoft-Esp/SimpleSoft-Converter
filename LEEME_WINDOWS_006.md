# Windows — Android 006 · v0.3.1

1. Conserva Android 005. Extrae SimpleSoft-Converter-Android-006.zip en otra carpeta, por ejemplo C:\Users\acido\AndroidStudioProjects\SimpleSoftConverter006.
2. Android Studio → File → Open → carpeta interna SimpleSoftConverter, donde está settings.gradle.kts. Mantén Embedded JDK 17 o 21 y SDK Platform 36. Android Studio generará local.properties con la ruta de tu equipo.
3. En la terminal de la raíz:
```
.\gradlew.bat assembleDebug testDebugUnitTest
```
4. Instala con Run o con app\build\outputs\apk\debug\app-debug.apk. Usa la misma clave debug de Windows de 003/004/005 (normalmente %USERPROFILE%\.android\debug.keystore). El package es el mismo y versionCode pasa de 5 a 6, permitiendo actualizar conservando datos y widgets.

No desinstales la anterior ni borres datos para actualizar. No copies caches, local.properties, claves o configuraciones de otro equipo. El ZIP no modifica automáticamente tu carpeta actual y no incluye claves.

## Comprobación en el teléfono
- Abre el selector de categorías de Mini: deben aparecer Longitud/Masa, Temperatura/Volumen, Superficie/Velocidad y Presión en la última fila. No deben aparecer Anterior/Siguiente ni el contador de página; Volver sigue disponible.
- Selecciona Velocidad y Presión directamente, sin salir del widget. Confirma las unidades y el teclado.
- En Longitud, abre origen/destino y verifica que las ocho unidades mantienen paginación de cinco y tres elementos. No se ha eliminado la navegación de unidades.
- Prueba el tamaño mínimo, un tamaño mayor, español e inglés. Con letra grande excepcional puede aparecer paginación necesaria para evitar recortar opciones; con letra normal deben verse las siete juntas.
- Si el launcher tarda en refrescar la vista al actualizar, toca el selector o cambia ligeramente el tamaño y vuelve al anterior. No elimines el widget para refrescarlo: eliminar una instancia borra su estado.
- Comprueba cantidad/par de cada widget, favoritos y preferencias tras actualizar. El esquema y los nombres de almacenamiento no cambian.
- En la aplicación, el banner recorre el mismo mensaje en aproximadamente dos tercios del tiempo anterior (45 dp/s frente a 30). Modo estático/reducción de movimiento debe seguir funcionando.

## Prueba instrumentada opcional
El test de host Android incluido está actualizado y compila. Para ejecutarlo con un dispositivo/emulador operativo y un único destino conectado, compila e instala la app con tu firma habitual, y autoriza solo el host de pruebas:
```
adb shell appwidget grantbind --package com.simplesoft.converter --user 0
.\gradlew.bat :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.simplesoft.converter.widget.MiniHostInstrumentedTest
```
Este arnés de depuración no es MainActivity ni un launcher real. La revisión en tu pantalla de inicio sigue siendo necesaria; los resultados ejecutados de esta entrega se describen en VERIFICACION_006.md.
