package com.azeemba.pancakes

import androidx.room.*
import java.net.URL
import java.time.Instant

@Fts4
@Entity
data class Visit(
    val url: String,
    val community: String,
    val title: String,
    val timestamp: Long,
)

fun makeNowVisit(url: String, title: String): Visit {
    val u = URL(url)
    val community = u.host.split('.')[0]

    return Visit(url, community, title, Instant.now().epochSecond)
}

@Dao
interface VisitDao {
    @Query("SELECT * FROM visit ORDER BY timestamp DESC")
    fun getAll(): List<Visit>

    @Query("SELECT * FROM visit WHERE title LIKE :keyword")
    fun searchTitle(keyword: String): List<Visit>

    @Insert
    fun insert(visit: Visit)
}

@Database(entities = [Visit::class], version = 1)
abstract class Storage: RoomDatabase() {
    abstract fun visitDao(): VisitDao
}