# Windows — Android 004 · v0.2.0

## Abrir sin sobrescribir Android 003
1. Conserva tu carpeta actual `C:\Users\acido\AndroidStudioProjects\SimpleSoftConverter`. No extraigas el ZIP encima.
2. Descarga SimpleSoft-Converter-Android-004.zip y extráelo en una carpeta nueva, por ejemplo `C:\Users\acido\AndroidStudioProjects\SimpleSoftConverter004`.
3. Android Studio → File → Open → selecciona la carpeta interna `SimpleSoftConverter` que contiene settings.gradle.kts. Verifica que aparecen los módulos `app` y `simplesoft-core`.
4. Gradle JDK: Embedded JDK 17 o 21. SDK Manager: Android SDK Platform 36. Permite descargar dependencias y herramientas. El proyecto no incluye local.properties; Android Studio debe generarlo con tu ruta local. No copies el de otra máquina.
5. En la terminal de la raíz, donde está gradlew.bat:
```
.\gradlew.bat assembleDebug testDebugUnitTest
```
La orden ejecuta las pruebas de ambos módulos. El primer build necesita Internet para dependencias; la app y sus mensajes funcionan sin conexión.
6. Selecciona tu móvil o emulador API 26+ y pulsa Run. El APK se genera en `app\build\outputs\apk\debug\app-debug.apk`.

## Conservar los datos al actualizar
Package=com.simplesoft.converter, versionCode=4, versionName=0.2.0. Room v1 y nombres de archivos/keys anteriores se conservan. No borres los datos, no desinstales 003 y no uses migración destructiva.

Para actualizar, compila 004 en tu Android Studio usando **la misma clave debug** con la que instalaste 003. Android Studio suele reutilizar `%USERPROFILE%\.android\debug.keystore`; conserva ese archivo. Una carpeta fuente nueva no exige una clave nueva. No compartas claves ni las añadas al ZIP.

La APK independiente de esta entrega está firmada con la clave de depuración del entorno de compilación; puede no coincidir con la de tu Windows. Si Android rechaza la actualización por firma, utiliza el APK que compiles tú con tu clave existente. **No desinstales 003 como solución si quieres conservar sus datos.** La APK suministrada sirve para dispositivos sin una instalación de firma diferente o para una actualización compatible con su firma.

El nombre visible 0.2.0 es el pedido para esta entrega, aunque 003 mostraba 0.3.0; Android usa el versionCode creciente para el orden de actualización.

## Integración manual opcional
Se recomienda probar el proyecto completo en carpeta nueva. Si deseas fusionarlo con el anterior, haz primero una copia o commit, compara archivos y conserva .idea, local.properties, claves, configuraciones de Run y cualquier modificación propia.

Además de los cambios de app, copia el módulo `simplesoft-core`, incluye el módulo en settings.gradle.kts y añade el plugin library en el build raíz. No mezcles versiones de Gradle/AGP incompatibles ni dupliques plugins si tu proyecto usa libs.versions.toml. Usa los archivos Gradle del ZIP como conjunto de referencia.

No se ha accedido a tu disco C: ni se ha cambiado tu instalación de Android Studio.

## Comprobaciones en el teléfono
- Tras actualizar con la misma firma: favoritos, orden, última conversión y preferencias de 003 permanecen.
- Físicas: las seis categorías anteriores funcionan y Presión añade siete unidades.
- 1 bar=100 kPa; 1 atm=101325 Pa; 1 mmHg conv=133,322387415 Pa (la presentación puede redondear y marcar ≈).
- Escribir 1 m → ft y cambiar dos veces: vuelve a 1 m sin pérdida por redondeo visible.
- Familias futuras: no se ofrecen conversiones falsas; un favorito retorna a la familia operativa.
- Favoritos/frecuentes separados, orden manual, borrados independientes y aprendizaje desactivable.
- Banner: mensajes desde JSON, desplazamiento derecha→izquierda y rotación; modo avión no afecta.
- Reducir movimiento en Ajustes: texto estático; Android «Quitar animaciones» y TalkBack con exploración táctil también lo detienen. La franja desaparece con teclado abierto y no tapa la navegación del sistema.
- Tema claro/oscuro, letra grande, rotación, TalkBack, lanzamiento frío y retorno desde segundo plano.
- Icono propio en launcher, icono temático Android 13+ si el launcher lo admite y splash sin demora artificial. El sistema puede no mostrar splash al volver a una actividad ya abierta.

Consulta VERIFICACION_004.md para distinguir pruebas ejecutadas de comprobaciones manuales pendientes.
