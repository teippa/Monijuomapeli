package com.teippa.monijuomapeli

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.openOrCreateDatabase
import android.os.Environment
import androidx.core.database.sqlite.transaction
import java.io.File
import java.lang.Exception


class DBmanager() {
    private val path = Environment.getExternalStorageDirectory().absolutePath +
            "/Android/data/com.teippa.monijuomapeli/";
    private val DB_NAME = "/myGames.db"
    private val COL_STATEMENT = "statement"

    private var canUse : Boolean
    private val myDatabase: SQLiteDatabase

    init {
        val isAvailable: Boolean
        val isWritable: Boolean
        val isReadable: Boolean
        val state: String = Environment.getExternalStorageState()
        when {
            Environment.MEDIA_MOUNTED == state -> { // Read and write operation possible
                isAvailable = true
                isWritable = true
                isReadable = true
            }
            Environment.MEDIA_MOUNTED_READ_ONLY == state -> { // Read operation possible
                isAvailable = true
                isWritable = false
                isReadable = true
            }
            else -> { // SD card not mounted
                isAvailable = false
                isWritable = false
                isReadable = false
            }

        }
        canUse = isAvailable && isWritable && isReadable

        if (!canUse)
            println("SD card not operable!!")

        val cd = File(path)
        if (!cd.exists())
            cd.mkdirs()
        myDatabase = openOrCreateDatabase(cd.absolutePath + DB_NAME, null, null)

        //println("avail:$isAvailable    write:$isWritable      read:$isReadable")
        //println(cd.absolutePath)
    }

    fun getTables() : ArrayList<String> {
        val tables = ArrayList<String>()
        myDatabase.transaction {
            val cursor = this.rawQuery(
                """
                SELECT name FROM sqlite_master 
                WHERE type = 'table' 
                AND name NOT LIKE 'sqlite_%' 
                AND name NOT LIKE 'android_%'
                ORDER BY 1;
                """.trimIndent(), null
            )
            if (cursor.moveToFirst()) {
                do {
                    tables.add(cursor.getString(0))
                } while (cursor.moveToNext());
            }
            cursor.close()
        }
        return tables
    }

    fun newTable(tblName: String) {
        myDatabase.transaction {
            this.execSQL("""
                CREATE TABLE IF NOT EXISTS '$tblName' (
                "id"	INTEGER,
                "$COL_STATEMENT"	TEXT NOT NULL UNIQUE,
                PRIMARY KEY("id" ASC)
                );""".trimIndent()
            )
        }
    }

    fun deleteTable(tblName: String) {
        myDatabase.transaction {
            this.execSQL("DROP TABLE IF EXISTS '$tblName'");
        }
    }

    /**
     * Returns false primarily if the constraint UNIQUE fails
     */
    fun insertRow(tblName: String, newStr: String) : Boolean {
        var success = true
        try {
            myDatabase.transaction {
                this.execSQL("""
                    INSERT INTO '$tblName' ('$COL_STATEMENT') VALUES ('$newStr')
                    """.trimIndent()
                )
            }
        } catch (e: Exception) {
            success = false
        }
        return success
    }


    fun readRows(tblName: String) : ArrayList<String> {
        val allRows = ArrayList<String>()
        try {
            myDatabase.transaction {
                val cursor = this.rawQuery(
                    """
                    SELECT ($COL_STATEMENT) FROM '$tblName'
                    """, null
                )
                if (cursor.moveToFirst()) {
                    do {
                        allRows.add(cursor.getString(0))
                    } while (cursor.moveToNext());
                }

                cursor.close()
            }
        } catch (e : Exception) {
            println(e)
        }

        return allRows
    }

    fun deleteRow(tblName: String, delStr: String) : Boolean {
        var success = true
        try {
            myDatabase.transaction {
                this.execSQL("""
                    DELETE FROM '$tblName'
                    WHERE $COL_STATEMENT = "$delStr";
                    """.trimIndent()
                )
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
            success = false
        }
        return success
    }


}