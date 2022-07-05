package com.teippa.monijuomapeli

import android.content.Context


object GameController {
    const val defaultGame = ""
    private const val statementNone = "Game is empty"
    var currentGame : String = defaultGame
    private var statements = ArrayList<String>()
    private var statementsIndexesShuffled = (0 until statements.size).shuffled()
    private var statementCursor = 0
    private var db = DBmanager();


    fun reset() {
        currentGame = defaultGame
        statements.clear()
        statementCursor = 0;
        statementsIndexesShuffled = (0 until 0).shuffled()
    }

    fun getStatementCounter() : String {
        return "${statementCursor+1}/${statements.size}"
    }

    fun getAllGames() : ArrayList<String> {
        return db.getTables()
    }


    fun addGame(gameName : String) : Boolean {
        var success = false
        if (gameName.isNotBlank()) {
            db.newTable(gameName)
            success = true
        }
        return success
    }

    fun copyGame(newGameName: String) {
        if (addGame(newGameName)) {
            statements.forEach { str ->
                db.insertRow(newGameName, str)
            }
        }
    }

    fun addStatement(newStatement : String) : Boolean {
        var success = false
        if (db.insertRow(currentGame, newStatement)) {
            statements.add(newStatement)
            generateShuffledIndexes()
            success = true
        }
        return success
    }


    fun getCurrentStatement(): String {
        return if (statements.size > 0)
            parseStatement(statements[statementsIndexesShuffled[statementCursor]])
        else
            statementNone
    }

    private fun parseStatement(str : String) : String {
        val strings = ArrayList<String> ()

        if ("[" in str && "]" in str) {
            val splitted = str.split("[","]")
            splitted[1].split(",").forEach {it ->
                strings.add("${splitted[0].trimEnd()} ${it.trim()} ${splitted[2].trimStart()}")
            }
        } else {
            return str
        }
        return strings.random()
    }

    private fun generateShuffledIndexes() {
        statementsIndexesShuffled = (0 until statements.size).shuffled()
        statementCursor = 0;
    }

    fun getAllStatements() : ArrayList<String> {
        return statements
    }

    fun selectCurrentGame(gameSelection : String) {
        val prevGame = currentGame
        currentGame =   if (gameSelection.isNotBlank()) gameSelection
                        else defaultGame
        db.newTable(currentGame) //Create new table if it doesn't exist

        if (prevGame != currentGame)
            loadStatements()
        //println("Current game set to ${currentGame}.")
    }

    private fun loadStatements() {
        statements.clear()
        statements = db.readRows(currentGame)
        generateShuffledIndexes()
    }


    fun nextStatementRnd() : String {
        val newStatement : String
        if (statements.size > 0) {
            if (++statementCursor >= statements.size) {
                statementCursor = 0
            }
            newStatement = statements[statementsIndexesShuffled[statementCursor]]
        } else {
            newStatement = statementNone
        }
        return parseStatement(newStatement)
    }

    fun removeCurrentSave() {
        db.deleteTable(currentGame)
    }

    fun removeStatementStr(statement: String) : Boolean {
        var rmvSuccess = false;
        if (db.deleteRow(currentGame, statement)) {
            statements.remove(statement)
            generateShuffledIndexes()
            rmvSuccess = true
        }
        return rmvSuccess;
    }

}