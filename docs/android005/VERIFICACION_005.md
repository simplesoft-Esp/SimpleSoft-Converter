# Verificación Android 005 · v0.3.0
10 de noviembre de 2026. Actualización sobre Android 004.

## Ejecutado realmente
- Compilación Android con JDK 17, Gradle 8.13, AGP 8.13.0, SDK Platform 36 y Build Tools 35.
- **BUILD SUCCESSFUL** para assembleDebug y testDebugUnitTest de ambos módulos. También se compila :app:assembleDebugAndroidTest. La última pasada offline recompila los recursos finales y reutiliza tests sin cambios; los registros se incluyen.
- **74 pruebas JVM, 0 fallos, 0 errores, 0 omitidas**:
  - 45 casos anteriores conservados (22 motor/ranking, 6 presión/familias, 4 ViewModel y 13 Core). La aserción de cinco mensajes se aplica ahora a la selección inglesa para mantener el mismo contrato ante el JSON bilingüe.
  - 6 de buscador: símbolos, nombres/alias, tildes y mayúsculas, categorías, vacío y selección de origen/categoría.
  - 6 de localización: es/en/fallback, paridad de recursos, nombres de todo el catálogo, decimales, cambio de formato sin alterar valor y argumentos de traducciones.
  - 15 de widget: dígitos, decimal, signo, C y retroceso, selección de categoría/unidades, paginación, memoria de pares, precisión tras intercambio y serialización, estado independiente, tamaño, temperatura, datos inválidos y motor compartido.
  - 2 de mensajes bilingües, IDs estables y fallback inglés.
- **APK debug generada**: 12373715 bytes, archivo independiente SimpleSoft-Converter-0.3.0-debug.apk.
- AAPT confirma package com.simplesoft.converter, versionCode **5**, versionName **0.3.0**, mínimo **26**, target **36**, recursos del widget y JSON con diez variantes de cinco mensajes.
- APK sin permiso INTERNET ni AD_ID. Solo protección de receptores internos de AndroidX DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION.
- Logo oficial idéntico en bytes a Android 004; esquema Room v1 idéntico. Nombres de archivos, claves existentes e IDs de unidades conservados. Sin migración destructiva.
- Revisión de rutas de acción: las teclas/selectores de ConverterMiniProvider usan PendingIntent.getBroadcast; no hay getActivity/startActivity en ese proveedor. Los paneles de opciones se añaden como RemoteViews dentro del widget. Esto verifica la estructura del código y compilación, no una interacción real de launcher.

## Prueba instrumentada: compilada, no ejecutada
Se preparó un host Android AppWidgetHost de depuración y MiniHostInstrumentedTest para pulsar RemoteViews reales y observar si se abre MainActivity. Su APK compila. Se instaló un emulador Android 8/API 26 x86 con emulación por software, pero **no completó el arranque dentro de 300 segundos**. El estado registrado es: Software-emulated API 26 did not boot within 300 seconds.

Por tanto: **0 pruebas instrumentadas ejecutadas/aprobadas**. No se ha instalado esta revisión en teléfono ni comprobado el widget end-to-end en un launcher real. No se afirma que las 74 pruebas JVM validen RemoteViews, PendingIntents, reinicio, accesibilidad, tamaños o cambios reales de configuración del sistema. Las pruebas de varias instancias/persistencia son del reductor y codec; Room/DataStore/SharedPreferences reales y callbacks del launcher requieren comprobación en dispositivo.

El arnés se limita al source set debug, no es parte del flujo de usuario ni se exporta. Instrucciones de ejecución incluidas en WIDGET_005.md. La APK independiente y la guía permiten realizar la validación en tu Windows con la firma existente.

## Pendiente manual
Colocar Mini en Inicio, categorías y unidades paginadas sin abrir MainActivity, teclado/negativos/decimales/borrados, intercambio repetido, presión/temperatura, dos instancias, eliminación, reinicio y cambio de idioma principal (incluido idioma no soportado con es secundario). Revisar letra grande/TalkBack, redimensionado, modo oscuro y actualización conservando datos y firma. WIDGET_005.md proporciona pasos detallados.

## Limitaciones conocidas
- Selectores son paneles paginados internos, no ComboBox convencional. Es una alternativa real RemoteViews, sin actividad de selección externa.
- Tamaño inicial 280 × 440 dp; mínimo declarado 260 × 420 dp. Un host que entregue menos espacio muestra una petición de ampliar; la cuadrícula y el tamaño físico dependen del launcher.
- Widget sigue el tema del sistema, no la selección manual de tema de la app. La actualización visual inmediata por tema depende del host; cada interacción vuelve a establecer colores y textos actuales.
- La entrada larga puede usar elipsis; el texto completo se conserva en estado y descripción accesible. No hay banner en Mini.
- El widget guarda su cantidad localmente por instancia, a diferencia de la pantalla principal que solo guarda pares y estadísticas. No se envía nada a servidores.
- El estado se restaura al recibir los eventos estándar del host y en cada interacción. No se promete uso antes de desbloquear el teléfono ni funcionamiento tras «Forzar detención» sin reabrir la aplicación.
- Advertencias de API deprecada heredadas: portapapeles y ScrollableTabRow; resourceConfigurations Gradle deprecado; opciones kapt de tests y símbolos nativos sin retirar. No impiden la compilación.

## Integridad y distribución
SHA-256 APK: `ea81b21f0291b35a880df4b7defb736c76e43d5811dcc0ca60c7b561817da150`.
ZIP con wrapper, fuentes, recursos en/es, módulo Core, JSON, código de widget, pruebas JVM e instrumentada, esquema Room, documentación e informes. Sin caches/builds, local.properties ni claves. Android 004 y entregas anteriores no se sobrescriben. Para actualizar conservando datos, compilar con la misma clave debug de Windows; la APK suministrada puede tener otra firma.
