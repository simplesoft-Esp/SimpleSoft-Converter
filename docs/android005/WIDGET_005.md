# SimpleSoft Converter Mini — Android 005

## Cómo utilizarlo
En el launcher, mantén pulsado un espacio vacío → Widgets → SimpleSoft Converter Mini. Colócalo con espacio suficiente; el tamaño inicial recomendado es aproximadamente 4 × 6 celdas, según launcher. No es necesario abrir la aplicación para convertir una vez colocado.

Distribución: nombre, categoría, origen / ⇄ / destino, cantidad, resultado y teclado 7–8–9–⌫ / 4–5–6–C / 1–2–3–± / 0–decimal. La tecla 0 ocupa más anchura. No hay 00 ni operaciones matemáticas.

Al tocar categoría/origen/destino, el propio widget muestra hasta cinco opciones por página, Anterior/Siguiente y Volver. Elegir una opción regresa al teclado. Solo se ofrecen unidades de la categoría actual. Se recuerda el último par utilizado por categoría. No hay actividad intermedia, spinner simulado ni acceso directo que abra el conversor principal.

## Arquitectura y flujo real
- `widget/ConverterMiniProvider.kt`: AppWidgetProvider y render RemoteViews.
- `widget/MiniState.kt`: estado independiente, reductor de teclas/selecciones, resultado usando Engine y codec JSON.
- `widget/MiniStore.kt`: SharedPreferences privado `converter_mini`; una entrada `widget_<appWidgetId>` por instancia.
- `res/layout/converter_mini.xml`: controles RemoteViews compatibles y paneles alternos; `mini_picker_option.xml` para cada opción.
- `res/xml/converter_mini_info.xml`: metadatos, límites de tamaño y updatePeriodMillis=0.

Cada botón usa un PendingIntent explícito **de broadcast**, inmutable, con URI única para ID de widget y comando. Un ejecutor serial procesa la acción con goAsync, valida que el ID siga perteneciendo a este proveedor, lee su estado, aplica el reductor, hace commit local y llama a updateAppWidget. No se lanza ninguna Activity. La cola evita que pulsaciones rápidas lean simultáneamente estados antiguos; no hay red, servicio permanente ni tarea periódica. El hilo de la cola caduca a los 15 segundos de inactividad.

La entrada numérica, elección de unidades y cambio de categoría se ejecutan en ese flujo. No hay duplicación de factores ni cálculo en código JavaScript. Se guarda base canónica/exacto para no reutilizar el resultado visual redondeado al intercambiar repetidamente. La nueva edición explícita de dígitos crea un nuevo valor, igual que en la aplicación.

## Estado, eliminación y restauración
Cada instancia guarda par, cantidad, precisión interna, últimos pares y estado del selector. Persiste tras recreación de proceso y reinicio porque se escribe en disco, no solo en memoria. onUpdate reconstruye las vistas desde el estado guardado; Android/launcher administra la restauración de los widgets al arrancar. onRestored remapea IDs; onDeleted elimina su entrada. Un ID eliminado no puede recrear datos mediante un PendingIntent antiguo: el receptor comprueba el ID registrado antes de actuar.

No se necesita permiso de arranque ni servicio propio. La disponibilidad y el momento del primer refresco al reiniciar dependen del launcher; la siguiente pulsación siempre reconstruye desde los datos locales. No se guarda el widget en una copia de nube: se mantiene allowBackup=false del proyecto anterior.

Los datos del widget son independientes de los favoritos/estadísticas de la aplicación principal. Su cantidad sí persiste localmente, a petición del diseño. No se registra ni se envía a terceros.

## Tamaño y accesibilidad
Tamaño inicial declarado: 280 × 440 dp; mínimo redimensionable: **260 × 420 dp**. Aproximadamente 4 × 6 celdas, variable por launcher. El teclado tiene cuatro filas con asignación mínima de 48 dp y ancho suficiente para cuatro teclas. Al aumentar tamaño, filas y columnas usan el espacio disponible.

Si un launcher impone medidas menores, se muestra una indicación localizada para ampliar el widget, en lugar de recortar o esconder teclas. No se abre la app como sustitución de una minicalculadora. La elección de tamaño mínimo es deliberada: el teclado completo no resulta cómodo en un widget 2 × 2.

Controles con etiquetas de accesibilidad localizadas. Las cantidades largas pueden usar elipsis visual en la entrada; el valor completo queda en la descripción accesible y en el estado. Resultado con hasta diez cifras significativas, notación científica y ≈ cuando corresponda. Los nombres largos en los selectores se muestran en sus paneles de opciones. Es necesaria la revisión manual con tamaño de letra grande y TalkBack.

## Idioma y tema
Idioma principal es → español; cualquier otro → inglés. Resources Android proporcionan todos los textos; los símbolos quedan estables. Decimal coma/punto según idioma. Cambio de idioma: receptor LOCALE_CHANGED y actualizaciones del proveedor vuelven a generar los textos. Las interacciones también utilizan la configuración actual. La actividad usa la misma política. Los mensajes del banner no ocupan espacio del widget.

El widget sigue el tema claro/oscuro del sistema mediante recursos night. El selector manual de tema de la app no cambia su tema; la siguiente interacción o actualización del host aplica la configuración actual. La actualización inmediata por cambios de tema sin interacción depende de los eventos que entregue el sistema/launcher. No se añade una alarma para forzarla.

## Límites de las APIs y de la comprobación
RemoteViews no permite reutilizar directamente la pantalla Compose ni un ComboBox normal. La alternativa implementada es un selector paginado **dentro del widget**, sin actividad, servicio de lista ni conexión.

La colocación, dimensiones exactas, cuadrícula y representación de widgets están controladas por cada launcher. Las pruebas JVM comprueban lógica, datos y formato, no simulan un launcher real. Consulta VERIFICACION_005.md: no se declara una comprobación real en dispositivo si no se ha ejecutado.

## Pruebas manuales detalladas
1. Abre una vez la instalación si tu launcher lo requiere para listar widgets. Coloca Mini y, desde este punto, permanece en Inicio para todas las operaciones.
2. Elige Longitud → origen km → destino m, usando los paneles internos y Volver/Anterior/Siguiente si hace falta. Teclea 2,5 en español o 2.5 en inglés: 2500 m.
3. C borra todo; teclea 123, ⌫ deja 12. ± cambia el signo. El separador no se duplica.
4. Temperatura: C → F, escribe −40: −40 °F. C → K, escribe −300: error localizado, sin intercambio válido.
5. Presión: bar → Pa, escribe 1: 100000 Pa. Elige mmHg convencional y comprueba 133,322387415 Pa antes del redondeo de presentación.
6. Longitud m → ft, escribe 1 e intercambia 20 veces: retorna al par y valor original sin redondeo acumulado. Repite tras apagar/reiniciar la pantalla.
7. Cambia entre categorías y confirma que no aparecen unidades de otra categoría; vuelve a presión y comprueba su último par.
8. Añade un segundo Mini con kg → lb y cantidad 99. Cambiar o borrar el primer widget no debe alterar el segundo.
9. Reinicia el móvil y verifica cantidad/par de ambos. Si el launcher demora su refresco, toca una tecla y confirma que usa el estado persistente. No pulses «Forzar detención»: Android suspende componentes de aplicaciones forzadas hasta su apertura.
10. Cambia el idioma principal a es, en y después eu/fr/de; revisa botones, selector, errores y decimal del widget. Para probar prioridad, configura eu como primero y es como segundo: la interfaz del conversor debe seguir en inglés.
11. Alterna modo claro/oscuro y tamaño de letra; pulsa una tecla para forzar refresco si tu launcher no lo hace al cambiar tema. Activa TalkBack: verifica nombres y orden de lectura, sin perder controles.
12. Redimensiona dentro de límites, gira el teléfono si tu launcher admite landscape y prueba dos launchers cuando sea posible. Si queda por debajo del mínimo, debe pedir ampliar sin controles cortados.
13. Activa modo avión. Repite conversión, selector y teclado: ninguna acción debe necesitar Internet.
14. Verifica que ninguna pulsación del teclado ni selectores abre la aplicación principal. Para documentarlo, mantén la app fuera de primer plano y revisa Recientes después de convertir.


## Ejecutar la prueba instrumentada incluida
Hay un host **solo de depuración**, `WidgetTestHostActivity`, sin icono ni enlace desde la app y no exportado. Es un arnés de pruebas con AppWidgetHost, no una pantalla necesaria para utilizar Mini. No se incluye en release. La prueba `MiniHostInstrumentedTest` pulsa vistas RemoteViews reales y comprueba que no se lance MainActivity; usa dos IDs de test y los elimina al finalizar. No sustituye las pruebas en tu launcher.

Con un único teléfono o emulador operativo, usando la misma firma debug de Windows:
```
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest
```
Instala el APK debug generado. El host de pruebas necesita que el sistema le autorice a enlazar widgets; ese permiso es únicamente para ejecutar el arnés y no se añade al manifiesto de la aplicación. En PowerShell, con adb en la ruta:
```
adb shell appwidget grantbind --package com.simplesoft.converter --user 0
.\gradlew.bat :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.simplesoft.converter.widget.MiniHostInstrumentedTest
```
Si adb no está en PATH, usa la terminal de Android Studio o la ruta a platform-tools de tu SDK. La prueba permite confirmar la interacción del componente oficial AppWidgetHost (no un mock) sin la actividad principal, además de la lista de comprobaciones del launcher real.

En esta entrega el arnés y su APK de pruebas **compilan**, pero no se ejecutaron: el emulador API 26 sin aceleración no completó el arranque tras 300 segundos. No se afirma resultado aprobado de esta prueba.
