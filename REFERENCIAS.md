# Referencias técnicas y factores
- Especificaciones 001, revisión 002: https://docs.google.com/document/d/1s4loyolqQkyfNXUi-dLX1dNyAuAJ4RyjYOZXDC2r1Jg/edit
- Prototipo: https://drive.google.com/file/d/1UAUUeOAnzatq_ay-q1rreyBr4-k4W8Y1/view
- NIST SP 811, apéndices B.8/B.9: https://www.nist.gov/pml/special-publication-811/nist-guide-si-appendix-b-conversion-factors
- Temperaturas: https://www.nist.gov/pml/owm/si-units-temperature
- Android/Gradle: https://developer.android.com/build/releases/about-agp
- Kotlin: https://kotlinlang.org/docs/gradle-configure-project.html

Factores derivados: pie=12 pulgadas; yarda=3 pies; milla=1760 yardas; onza=libra/16; acre=43560 pies²; galón US=231 pulgadas³; galón imperial=4.54609 L; nudo=1852 m/h. Prefijos SI exactos. Fahrenheit se transforma mediante (F−32)×5/9+273.15; velocidad usa metros/hora como base común para no precalcular factores periódicos. DECIMAL128 en divisiones, 10 cifras significativas en presentación. El estado retiene la base canónica al intercambiar.

Dependencias fijadas para reproducibilidad, no se afirma que sean las últimas disponibles. Kapt se usa como procesador de Room para evitar añadir otra matriz de versiones KSP. Se conserva el logotipo PNG original sin transformaciones, en Acerca de. Sin dependencias publicitarias ni permiso INTERNET.


# Referencias Android 005
- RemoteViews y AppWidgetProvider: https://developer.android.com/develop/ui/views/appwidgets
- Actualizaciones y restauración: https://developer.android.com/develop/ui/views/appwidgets/advanced
- Recursos e idioma: https://developer.android.com/guide/topics/resources/localization
- LocaleList y resolución: https://developer.android.com/guide/topics/resources/multilingual-support

Se usa la alternativa de paneles RemoteViews con botones de broadcast; no se incorpora Glance, un servicio de colección o un catálogo de factores diferente.
