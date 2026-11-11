# Cambios Android 006 · v0.3.1

Base: Android 005, sin reconstruir la aplicación. versionName 0.3.1, versionCode 6 (antes 5). Package com.simplesoft.converter y mínimo API 26 conservados.

## Categorías en el widget
Las siete categorías se obtienen de Category.entries y se muestran en orden de lectura en una cuadrícula de dos columnas y cuatro filas. La última fila contiene Presión con todo el ancho. No hay otra lista de categorías ni nombres incrustados: se usan los mismos recursos español/inglés.

Cuando caben todas, se ocultan el indicador de página, la fila Anterior/Siguiente y ambos botones. Volver permanece disponible. La selección continúa mediante PendingIntent de broadcast dentro del widget, sin abrir MainActivity.

PickerLayouts calcula un plan compartido entre render y manejo de acciones según anchura, altura, tamaño de letra y cantidad de categorías. Con las siete actuales, en el mínimo 260 × 420 dp y escalas de letra habituales de 1.0–2.0, caben sin paginación. Los botones no se reducen por debajo de 48 dp; hay variantes de fila de 48, 64 y 96 dp y ajuste de texto hasta dos líneas. Se reserva espacio para título, Volver y márgenes.

Si un catálogo futuro o un tamaño de letra excepcional supera el espacio disponible, el mismo selector activa paginación únicamente cuando hace falta. No se añade desplazamiento no soportado ni una actividad externa.

Los selectores de unidades mantienen una columna, cinco unidades por página y su navegación anterior. Se conservan los IDs de widget, cantidades y precisión. Una página antigua de categorías se normaliza al rango visible sin borrar la cantidad ni sus preferencias. El formato JSON del estado sigue siendo versión 1 con los mismos campos.

Archivos principales: widget/PickerLayouts.kt, MiniState.kt y ConverterMiniProvider.kt; layouts mini_category_row y mini_category_cell_48/64/96; identificador de la fila de navegación en converter_mini.xml.

## Banner
Único cambio de comportamiento: 30 → **45 dp/s**, exactamente +50 %. El parámetro está en BannerPolicy.SCROLL_SPEED_DP_PER_SECOND y lo utiliza RetroBanner. No cambia el bucle de animación, su cadencia, el dibujo matricial, los colores, los mensajes, las traducciones, la pausa por visibilidad ni la reducción de movimiento. No se ha añadido ningún SDK ni publicidad real.

## Compatibilidad
Sin cambios en motor, factores, catálogo de unidades, buscador, favoritos, ranking, DataStore, Room, MiniStore, icono o PNG oficial. El XML de metadatos del widget es idéntico: tamaño mínimo, redimensionado y updatePeriodMillis no cambian. El esquema Room v1 y el JSON compartido de mensajes son idénticos en bytes a 005. No se borran preferencias.

Las 74 pruebas JVM anteriores se conservan; una se adapta porque el comportamiento que esperaba paginación de categorías ha cambiado, y sigue comprobando la paginación de unidades. Se añaden diez pruebas de planes/categorías y dos del aumento de velocidad. El test instrumentado existente se amplía para comprobar siete botones totalmente visibles, navegación oculta y selección directa de Velocidad y Presión.
