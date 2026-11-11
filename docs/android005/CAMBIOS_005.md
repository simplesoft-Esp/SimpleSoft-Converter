# Cambios Android 005 · v0.3.0

Actualización sobre el proyecto Android 004, conservado como entrega independiente. VersionCode 5, package y mínimo SDK 26 sin cambios.

## Buscador
Campo integrado en la pantalla principal. Búsqueda instantánea y local por categoría, nombre localizado, símbolo, ID estable y alias relevantes. Normaliza mayúsculas y diacríticos, admite palabras múltiples y prioriza símbolos exactos. Resultados agrupados por categoría. Tocar una unidad establece su categoría y la unidad de origen, conservando un destino válido de la misma categoría; tocar una categoría restaura su par. Favoritos y frecuentes siguen visibles y permiten volver al conversor. No existe un segundo catálogo de factores: los resultados se generan desde Catalog y los recursos.

## Inglés y español
values/strings.xml define inglés; values-es/strings.xml español. Incluye unidades, categorías, familias futuras, pantallas, acciones, errores, accesibilidad, buscador y widget. Los símbolos científicos permanecen iguales. Los IDs de unidades no cambian.

La política LanguagePolicy selecciona español únicamente si el idioma principal del sistema es es (incluidas variantes regionales). Todo otro idioma produce inglés, incluso si es aparece en una posición secundaria de LocaleList. AppLanguage crea un Context de recursos estándar con es/en para aplicar esa regla a la actividad y al render del widget. No hay selector manual ni traducción online. Resultados y tecla decimal usan coma en es y punto en en; la entrada de la app sigue aceptando ambos.

El JSON de SimpleSoft Core conserva cinco IDs de mensajes con variantes en y es. La clave única de una variante es (id, language). forLanguage elige el idioma activo y tiene fallback inglés por ID. Cambiar mensajes solo exige editar el archivo fuente JSON y recompilar.

## SimpleSoft Converter Mini
Widget RemoteViews realmente orientado a interacción dentro del launcher: selectores abren paneles de opciones paginados en el mismo rectángulo, y el teclado numérico envía PendingIntent.getBroadcast al AppWidgetProvider. No llama a MainActivity ni utiliza PendingIntent.getActivity para categorías, unidades, teclado o intercambio.

Teclas 0–9, decimal, C, retroceso y ±; no hay operaciones matemáticas ni 00. ⇄ está entre origen y destino. Mismo Engine/Catalog que la app; no se duplican tablas ni fórmulas. Mantiene valor canónico al intercambiar y lo guarda por instancia.

Estado local por appWidgetId en converter_mini. La app principal continúa sin guardar cantidades numéricas, mientras Mini sí las guarda porque así se requiere para el widget. No se mezclan widgets ni se añaden sus pulsaciones a los contadores de la pantalla principal.

Sin servicio, adaptador remoto de servicio, actualización periódica ni banner. Una cola de ejecución corta procesa acciones serialmente; el hilo expira cuando queda inactivo. updatePeriodMillis=0. Soporta eliminación, remapeo de IDs, cambios de tamaño y actualización por idioma. Los colores del widget siguen el tema del sistema; no el selector manual de apariencia de la actividad.

## Conservado
Siete categorías y precisión del motor, favoritos ordenables, ranking por frecuencia/recencia, DataStore, Room v1, temas, icono, splash y banner de Core. Los nombres de DB/archivos/keys y los IDs anteriores permanecen estables. No se usa migración destructiva. Se añaden preferencias independientes del widget.

## Arquitectura
No se ha extraído otro módulo del motor: tanto Activity como AppWidgetProvider pertenecen a :app y comparten directamente domain. Core continúa siendo una biblioteca de identidad independiente. Las definiciones de unidades/categorías ahora enlazan IDs de recursos de nombres/alias; sus valores numéricos siguen en las mismas reglas. UnitSearch y MiniReducer/MiniCodec son lógica determinista probada en JVM.

## Alcance y límites
No se implementan categorías nuevas ni equivalencias de tallas. No se publica ni se añade publicidad real, red o seguimiento. El widget utiliza listas internas paginadas, no ComboBox nativo ni ventana externa. La validación instrumental y en launcher se distingue de tests JVM en VERIFICACION_005.md.
