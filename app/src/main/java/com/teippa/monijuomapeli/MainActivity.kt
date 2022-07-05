package com.teippa.monijuomapeli

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.PopupWindow
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_select_game, R.id.navigation_play_game, R.id.navigation_edit_game))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val f = File(navView.context.getExternalFilesDir("")!!.absolutePath + "/")
        if (!f.exists()) {
            f.mkdirs()
        }

        copyOldGames()
    }


    private fun copyOldGames() {
        val fileRW = FileRW()
        val allGames = fileRW.getFileNames()

        if (allGames.size>0 ) {

            allGames.forEach { fileName ->
                val gameName = fileName.split('.')[0]
                GameController.addGame(gameName)
                GameController.selectCurrentGame(gameName)

                val statements = fileRW.readLines(gameName)
                statements.forEach { statement ->
                    GameController.addStatement(statement)
                }
                fileRW.removeFile(gameName)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                setPopup()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setPopup() {
        val mainLayout = findViewById<ConstraintLayout>(R.id.mainLayout)

        val layoutInflater =
            this@MainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView: View = layoutInflater.inflate(R.layout.popup, null)
        //instantiate popup window
        val popupWindow =
            PopupWindow(customView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        //display the popup window
        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0)
        //close the popup window on button click
        customView.setOnClickListener{
            popupWindow.dismiss()
        }



    }

}
