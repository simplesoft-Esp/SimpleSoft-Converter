# SimpleSoft Core

Biblioteca Android local y reutilizable `:simplesoft-core`. Depende de Compose/Foundation/Material 3 y Lifecycle para observar visibilidad; minSdk 26. No conoce el motor del conversor, Room, DataStore, el package de la app ni una red de publicidad. No declara permisos. La aplicación anfitriona conserva las preferencias del usuario y las pasa como argumentos.

## Incorporarlo en otra aplicación
Mantener Core como una sola carpeta fuente en un repositorio compartido o referenciar esa carpeta desde varios proyectos Gradle; no duplicar los archivos Kotlin ni el JSON.

En settings.gradle.kts de una aplicación hermana:
```
include(":simplesoft-core")
project(":simplesoft-core").projectDir = file("../SimpleSoftConverter/simplesoft-core")
```
La ruta debe ajustarse a tu estructura local. En este proyecto no hace falta asignar projectDir porque el módulo es hermano de app.

El build raíz debe declarar el plugin com.android.library y los plugins Kotlin/Compose compatibles (esta entrega usa AGP 8.13.0 y Kotlin 2.2.0). En el módulo de la app:
```
implementation(project(":simplesoft-core"))
```
No se publica ningún artefacto remoto. En el futuro se puede distribuir un AAR con un repositorio de versiones, sin cambiar el API de composición. Un AAR incorpora su snapshot de assets; cambiar mensajes exige recompilar cada APK.

En Compose:
```
val brand = rememberBrandMessages()
Scaffold(bottomBar = {
    if (!keyboardVisible) {
        SimpleSoftFooter(
            messages = brand.messages,
            reduceMotion = userReduceMotion,
            modifier = Modifier.navigationBarsPadding()
        )
    }
}) { padding -> /* contenido con padding, sin superponer el pie */ }
```
El anfitrión decide si se oculta con teclado y dónde aplicar los insets. El componente no muestra UI de consentimiento ni pide permisos.

## Mensajes: una sola fuente
Archivo: `simplesoft-core/src/main/assets/simplesoft_messages.json`. Desde Android 005 contiene variantes en y es para los mismos cinco IDs.
Estructura:
```
{
  "schemaVersion": 1,
  "catalogVersion": "1.0.0",
  "language": "en",
  "messages": [
    {"id": "identificador-estable", "enabled": true, "text": "MENSAJE NUEVO"}
  ]
}
```
Añadir objetos o cambiar enabled no exige modificar Kotlin. Se admite language por mensaje; si falta hereda el idioma del catálogo. La versión de catálogo es editorial; schemaVersion controla compatibilidad de formato. La revisión 005 selecciona automáticamente es/en con forLanguage; un anfitrión puede solicitar otra selección antes de llamar al banner.

Reglas: hasta 64 KiB UTF-8, 100 mensajes, IDs de 1–64 caracteres, únicos por idioma, `[a-z0-9._-]` empezando por letra o número, texto de 1–200 caracteres sin controles, idioma válido y enabled booleano explícito. Mensajes incorrectos, desactivados o duplicados se omiten; una cabecera/estructura inválida invalida el catálogo. Esquemas futuros no conocidos se ocultan de forma segura. Archivo ausente, vacío o ilegible: lista vacía, banner oculto y ninguna espera añadida. No hay frases promocionales de respaldo incrustadas en el componente.

Los cinco mensajes iniciales están únicamente en el JSON de producción. Las repeticiones en tests son aserciones; el APK no las incluye como fuente de datos. Pruebas/documentación no son un segundo archivo de configuración.

## Movimiento, representación y accesibilidad
`rememberBrandMotionAllowed` observa la escala de animación del sistema con ContentObserver, la exploración táctil con AccessibilityManager y el estado RESUMED. `reduceMotion=true` siempre vence. No se activa un bucle de frames si no hay movimiento. El estado de desplazamiento vive en la capa de dibujo, evitando recomponer toda la aplicación en cada frame; solo cambia el mensaje al completar su salida.

Glifos 5×7 para A–Z, dígitos y puntuación básica, con normalización de acentos para presentación. Si otro alfabeto no puede representarse correctamente, se usa texto nativo estático. Texto accesible completo, sin anunciar cada frame. En modo estático no se trunca deliberadamente a una línea: ajusta el texto a la anchura. La validación real con TalkBack y tamaños de fuente debe hacerse en dispositivo.

## Preparación para contenido futuro
`SimpleSoftFooter(..., replacement = { ContenidoDiscreto() })` sustituye la promoción propia por contenido suministrado explícitamente por el anfitrión. No se incluye un proveedor real. Ese contenido deberá respetar altura, insets, teclado y la filosofía de no interrumpir; el slot no concede acceso a la red ni introduce SDK.

## Extender el conversor
`domain/Categories.kt`: categoría implementada, familia y descriptores de futuras categorías.
`domain/Units.kt`: IDs estables, nombres, símbolos y catálogo.
`domain/ConversionRules.kt`: reglas lineales y afines y sus parámetros.
`domain/Conversion.kt`: entrada, formato, validación y ranking.

Registrar una categoría, sus unidades, el par inicial y la regla; añadir pruebas antes de exponerla. La pantalla obtiene las categorías implementadas de CategoryFamilies.available. No convertir tallas mediante un factor: necesitan país/sistema, contexto y rangos empíricos con fuentes. No reciclar IDs de unidades existentes: se guardan en preferencias y Room.


## Actualización Android 005 — catálogo bilingüe
El mismo JSON contiene cinco IDs con variantes en inglés y español. La unicidad es (id, language), no solo id. `MessageCatalog.forLanguage(language)` conserva el orden, elige es solo para idioma principal es y usa fallback en por ID. `rememberBrandMessages` vuelve a seleccionar mensajes cuando cambia LocalConfiguration. La fuente sigue siendo un único asset; no se han incrustado traducciones promocionales en RetroBanner.

Los nombres/unidades de la aplicación se localizan con recursos Android y los símbolos no se traducen. Tanto Activity como widget usan el mismo dominio en :app; no se ha copiado el motor al módulo Core.


## Android 006
Velocidad del banner: BannerPolicy.SCROLL_SPEED_DP_PER_SECOND = 45f (antes 30f), +50 %. JSON, traducciones, cadencia de frames y reducción de movimiento no cambian.
