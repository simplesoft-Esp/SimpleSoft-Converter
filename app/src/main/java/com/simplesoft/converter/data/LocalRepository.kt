package com.simplesoft.converter.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.*
import com.simplesoft.converter.domain.*
import kotlinx.coroutines.flow.*

@Entity(tableName="favorites") data class Favorite(@PrimaryKey val pair: String, val position: Int)
@Entity(tableName="usage_stats") data class Usage(@PrimaryKey val pair: String, val count: Long, val score: Double, val last: Long)
@Dao interface LocalDao {
 @Query("SELECT * FROM favorites ORDER BY position, pair") fun favorites(): Flow<List<Favorite>>
 @Query("SELECT * FROM usage_stats ORDER BY last DESC, pair") fun usage(): Flow<List<Usage>>
 @Query("SELECT * FROM favorites ORDER BY position, pair") suspend fun favoriteList(): List<Favorite>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun saveFavorites(items: List<Favorite>)
 @Query("DELETE FROM favorites") suspend fun clearFavorites()
 @Query("SELECT * FROM usage_stats WHERE pair=:id") suspend fun usage(id: String): Usage?
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun saveUsage(item: Usage)
 @Query("DELETE FROM usage_stats") suspend fun clearUsage()
}
@Database(entities=[Favorite::class,Usage::class], version=1, exportSchema=true)
abstract class LocalDatabase: RoomDatabase() { abstract fun dao(): LocalDao }
private val Context.preferences by preferencesDataStore(name="simplesoft_settings")
data class Preferences(val theme: String="system", val learning: Boolean=true, val last: String="m>ft", val categoryPairs: Map<Category,String> = emptyMap(), val family: Family=Family.PHYSICAL, val reduceMotion: Boolean=false)
interface ConverterRepository {
 val favorites: Flow<List<Favorite>>
 val usage: Flow<List<Usage>>
 val preferences: Flow<Preferences>
 suspend fun select(pair: PairKey)
 suspend fun family(value: Family)
 suspend fun reduceMotion(value: Boolean)
 suspend fun theme(value: String)
 suspend fun learning(value: Boolean)
 suspend fun resetPreferences()
 suspend fun toggle(pair: PairKey)
 suspend fun move(id: String,delta: Int)
 suspend fun record(pair: PairKey,now: Long)
 suspend fun clearUsage()
 suspend fun clearFavorites()
}
class LocalRepository(context: Context): ConverterRepository {
 private val db=Room.databaseBuilder(context.applicationContext,LocalDatabase::class.java,"converter.db").build()
 private val dao=db.dao()
 private val settings=context.applicationContext.preferences
 private val themeKey=stringPreferencesKey("theme")
 private val learningKey=booleanPreferencesKey("learning")
 private val lastKey=stringPreferencesKey("last")
 private val familyKey=stringPreferencesKey("family")
 private val reduceMotionKey=booleanPreferencesKey("reduce_motion")
 override val favorites=dao.favorites()
 override val usage=dao.usage()
 override val preferences=settings.data.map { p -> Preferences(p[themeKey]?:"system",p[learningKey]?:true,p[lastKey]?:"m>ft",Category.entries.associateWith { p[stringPreferencesKey("pair_${it.name}")]?:Catalog.default(it).id },Family.entries.firstOrNull {it.name==p[familyKey]}?:Family.PHYSICAL,p[reduceMotionKey]?:false) }
 override suspend fun select(pair: PairKey) { settings.edit { it[lastKey]=pair.id;it[familyKey]=pair.category.family.name;it[stringPreferencesKey("pair_${pair.category.name}")]=pair.id } }
 override suspend fun family(value: Family) { settings.edit {it[familyKey]=value.name} }
 override suspend fun reduceMotion(value: Boolean) { settings.edit {it[reduceMotionKey]=value} }
 override suspend fun theme(value: String) { settings.edit { it[themeKey]=value } }
 override suspend fun learning(value: Boolean) { settings.edit { it[learningKey]=value } }
 override suspend fun resetPreferences() { settings.edit { it.clear() } }
 override suspend fun toggle(pair: PairKey) { db.withTransaction {
  val old=dao.favoriteList(); val ids=old.map { it.pair }.toMutableList()
  if(!ids.remove(pair.id)) ids.add(pair.id)
  dao.clearFavorites();dao.saveFavorites(ids.mapIndexed { i,id -> Favorite(id,i) })
 } }
 override suspend fun move(id: String,delta: Int) { db.withTransaction {
  val ids=dao.favoriteList().map { it.pair }.toMutableList();val i=ids.indexOf(id);val j=i+delta
  if(i>=0 && j in ids.indices) { val tmp=ids[i];ids[i]=ids[j];ids[j]=tmp;dao.clearFavorites();dao.saveFavorites(ids.mapIndexed { n,p -> Favorite(p,n) }) }
 } }
 override suspend fun record(pair: PairKey, now: Long) { db.withTransaction {
  val old=dao.usage(pair.id)
  dao.saveUsage(Usage(pair.id,(old?.count?:0)+1,UsageRanking.decayed(old?.score?:0.0,old?.last?:now,now)+1,now))
 } }
 override suspend fun clearUsage()=dao.clearUsage()
 override suspend fun clearFavorites()=dao.clearFavorites()
}
