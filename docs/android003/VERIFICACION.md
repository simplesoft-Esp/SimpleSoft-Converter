# Verificación Android 003 — 10 de noviembre de 2026

## Ejecutado realmente
- Entorno Linux con JDK Temurin 17, Gradle 8.13, Android SDK Platform 36 y Build Tools instalados.
- Orden: `gradle testDebugUnitTest assembleDebug` (el equivalente Windows es `.\gradlew.bat testDebugUnitTest assembleDebug`).
- Resultado: **BUILD SUCCESSFUL**.
- **22 pruebas JUnit, 0 fallos, 0 errores**. Incluyen definiciones de factores, las seis categorías, límites de temperatura, separadores, redondeo, base canónica y frecuencia/recencia. Una prueba recorre todos los pares de unidades de cada categoría, con tolerancia 1E-25 en ida/vuelta.
- APK debug generado, 12,614,568 bytes. No se incluye en el ZIP de fuentes; se regenerará con assembleDebug.
- Room ha generado su esquema v1, incluido en `app/schemas/`.
- Se incluye el informe XML de JUnit y el registro de compilación.

## Límites de la verificación
No se ha instalado ni ejecutado la interfaz en emulador o teléfono. No se afirma validación visual, accesibilidad, IME, rotación, persistencia en dispositivo ni funcionamiento en todos los fabricantes. La guía contiene pruebas manuales para esos casos. Los tests unitarios cubren el motor y el ranking, no la integración Room/DataStore ni el ciclo de vida Android.

Advertencias no bloqueantes: API de portapapeles Compose deprecada pero compatible; opciones kapt no utilizadas en el target de pruebas; librerías nativas de dependencias empaquetadas sin eliminar símbolos. No hay errores de compilación.

## Empaquetado
Se excluyen caches, builds, configuración de máquina, local.properties y claves. Las fuentes corresponden a la compilación verificada. El wrapper oficial Gradle 8.13 incluye checksum de distribución. Los ajustes específicos de proxy/certificados del entorno de validación no se incluyen en la configuración del proyecto.
