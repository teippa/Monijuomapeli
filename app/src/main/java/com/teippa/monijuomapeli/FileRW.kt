package com.teippa.monijuomapeli

import android.os.Environment
import java.io.File

class FileRW {
    private val path = Environment.getExternalStorageDirectory().absolutePath +
            "/Android/data/com.teippa.monijuomapeli/";
    private val gamesLocation = "files/"

    fun getFileNames() : ArrayList<String>{
        val fileNames = ArrayList<String>()
        val cd = File(path + gamesLocation)
        if (cd.exists()) {
            val files = cd.listFiles()
            files.forEach {
                fileNames.add(it.name.toString())
            }
        } else {
            println("Cant get game names, ${cd.absolutePath} does not exist.")
        }
        return fileNames
    }
/*
    fun writer(fileName: String, statement : String) : Boolean {
        var success = true
        val cd = File(path + gamesLocation)
        if (!cd.exists())
            success = cd.mkdirs()
        if (success) {
            val gameFile = File(cd, "${fileName}.txt")
            if (!gameFile.exists())
                success = gameFile.createNewFile()
            try {
                // response is the data written to file
                gameFile.appendText("${statement}\n")
            } catch (e: Exception) {
                println(e.toString())
                // handle the exception
            }
        } else {
            println("directory creation is not successful")
        }
        return success
    }
*/
    fun readLines(fileName: String) : ArrayList<String> {
        val cd = File(path + gamesLocation)
        var success = true
        val strings = ArrayList<String> ()

        if (!cd.exists())
            success = false
        if (success) {
            val gameFile = File(cd, "${fileName}.txt")

            if (!gameFile.exists())
                return strings
            try {
                // response is the data written to file
                gameFile.forEachLine {
                    strings.add(it)
                }
            } catch (e: Exception) {
                println(e.toString())
                // handle the exception
            }
        } else {
            println("readerFail1")
            // directory creation is not successful
        }
        return strings
    }
/*
    fun newFile(fileName: String) : Boolean {
        var fileAdded = true
        val cd = File(path + gamesLocation)
        if (!cd.exists())
            fileAdded = cd.mkdirs()
        if (fileAdded) {
            val gameFile = File(cd, "${fileName}.txt")
            fileAdded =
                if (!gameFile.exists()) gameFile.createNewFile()
                else false
        } else {
            println("could not create directory at ${cd.absolutePath}")
        }
        return fileAdded
    }
*/
    fun removeFile(fileName : String) : Boolean {
        val cd = File(path + gamesLocation)
        val gameFile = File(cd, "${fileName}.txt")
        return if (gameFile.exists()) {
            gameFile.delete()
        } else {
            false
        }
    }
}