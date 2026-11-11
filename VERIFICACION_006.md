# Verificación Android 006 · v0.3.1
10 de noviembre de 2026.

## Resultados ejecutados
- Linux, JDK 17, Gradle 8.13, AGP 8.13.0, compile/target SDK 36.
- **BUILD SUCCESSFUL** en assembleDebug y testDebugUnitTest de ambos módulos. También compila :app:assembleDebugAndroidTest.
- **86 pruebas JVM, 0 fallos, 0 errores, 0 omitidas**. Se conservan los 74 casos de 005, adaptando la expectativa antigua de paginación de categorías y manteniendo su comprobación de unidades. Se añaden 10 casos PickerLayoutsTest y 2 BannerSpeedTest.
- Categorías: inventario/orden de las siete, presencia en la primera página en tamaños mínimo y normal, selección de Velocidad/Presión, acciones de paginación sin página extra, traducciones y conservación de unidades en páginas de cinco.
- Adaptación: cálculo de filas/tamaño de letra y espacio reservado; paginación cuando un catálogo futuro o letra excepcional la necesite; normalización de una página antigua sin perder cantidad.
- Banner: constante 45 dp/s = 30 × 1.5; distancia recorrida un 50 % mayor para el mismo intervalo de frame. No se cambia el ritmo de redibujado.
- APK debug generada (12346878 bytes), AAPT confirma com.simplesoft.converter, versionCode **6**, versionName **0.3.1**, minSdk **26**, target **36**. El ZIP contiene fuentes para regenerarla; no se entrega una APK adicional.
- Comparación byte a byte con 005: esquema Room v1, XML de metadatos/minimum size del widget, PNG oficial, LocalRepository, MiniStore, motor, unidades/reglas y JSON de mensajes sin cambios. Diez variantes de mensajes (cinco IDs en es/en) presentes en APK.
- Sin permiso INTERNET ni AD_ID; se conserva únicamente la protección de receptores internos de AndroidX.

## Instrumentación y límites
**No se han ejecutado pruebas instrumentadas ni pruebas en un launcher real en Android 006.** ADB no tenía dispositivos conectados y no había emulador operativo. No se declara como aprobada la prueba de Android 005 cuyo emulador no arrancó.

La prueba instrumentada actualizada compila y comprueba, si se ejecuta, siete opciones visibles sin recorte, navegación de categorías oculta y selección directa de Velocidad/Presión. Es un host Android de prueba; no sustituye la verificación en el launcher del teléfono.

Los tests JVM verifican inventario, lógica, índices, geometría calculada y textos; **no demuestran por sí solos el resultado visual de RemoteViews, el enmascarado de un launcher, TalkBack, ni fluidez/consumo en hardware**. La altura modelada reserva 120 dp para título, Volver y márgenes, y otros 76 dp cuando existe navegación. Al mínimo y letra normal, cuatro filas × 48 dp + reserva = 312 dp de los 420 disponibles, sin bajar el tamaño táctil. Con dos columnas queda anchura suficiente para controles de 48 dp. Los controles ajustan texto a dos líneas.

## Comprobaciones manuales pendientes
Ver las siete categorías a la vez en tu launcher, ambas traducciones, tamaños y letra grande; tocar Velocidad y Presión; conservar paginación de unidades; revisar Volver; comparar desplazamiento del banner y modo estático. Confirmar persistencia después de actualizar con la misma firma de Windows. LEEME_WINDOWS_006.md incluye los pasos.

No se ha cambiado el esquema de datos ni borrado preferencias. La página de selector guardada puede ajustarse al nuevo rango de páginas al interactuar, conservando par y cantidad. Para un número futuro de categorías o un tamaño de letra que no permita mostrarlas todas, la paginación se activa por necesidad.

## Trazabilidad
Informes XML JUnit y registros de las dos compilaciones en docs/verificacion006. SHA256SUMS.txt lista los archivos fuente. SHA-256 APK verificada: `86507ce78dc6342e2ad7db712a0163b83e4162a7ae3ad619567c6e96be224404`.

Advertencias no bloqueantes heredadas: APIs Compose de portapapeles/ScrollableTabRow, resourceConfigurations Gradle deprecado y opciones kapt de tests. Sin errores de compilación. Android 005 y otras entregas se conservan sin sobrescritura.
