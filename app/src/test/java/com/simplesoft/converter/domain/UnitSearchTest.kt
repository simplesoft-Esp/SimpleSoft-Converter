package com.simplesoft.converter.domain
import org.junit.Test
import org.junit.Assert.*
import com.simplesoft.converter.localization.ResourceFixtures
class UnitSearchTest {
 @Test fun symbols() {for(id in listOf("psi","bar","km")) assertEquals(id,UnitSearch.search(id,ResourceFixtures.units("en")).first().id)}
 @Test fun spanishNamesAndAccents() {val units=ResourceFixtures.units("es");assertEquals("l",UnitSearch.search("litros",units).first().id);assertEquals("mi",UnitSearch.search("millas",units).first().id);assertTrue(UnitSearch.search("presion",units).all {it.category==Category.PRESSURE});assertEquals("km",UnitSearch.search("KILOMETROS",units).first().id)}
 @Test fun englishNamesAndSynonyms() {val u=ResourceFixtures.units("en");assertEquals("F",UnitSearch.search("fahrenheit",u).first().id);assertEquals("l",UnitSearch.search("litres",u).first().id);assertEquals("ft",UnitSearch.search("foot",u).first().id)}
 @Test fun categoriesAreGroupedFromExistingCatalog() {val results=UnitSearch.search("volume",ResourceFixtures.units("en"));assertEquals(Catalog.units(Category.VOLUME).map {it.id}.toSet(),results.map {it.id}.toSet())}
 @Test fun noResults() {assertTrue(UnitSearch.search("unicornxyz",ResourceFixtures.units("en")).isEmpty());assertTrue(UnitSearch.search(" ",ResourceFixtures.units("en")).isEmpty())}
 @Test fun selectingUnitChangesCategoryAndSource() {val p=SearchSelection.forOrigin("psi","bar>Pa");assertEquals(Category.PRESSURE,p.category);assertEquals("psi",p.from);assertEquals("Pa",p.to);assertEquals(Category.LENGTH,SearchSelection.forOrigin("km","kg>lb").category)}
}
