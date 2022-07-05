package com.teippa.monijuomapeli.ui.select

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.teippa.monijuomapeli.GameController
import com.teippa.monijuomapeli.R


class SelectFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_select, container, false)
        val gameSelection = root.findViewById(R.id.game_selection_box) as LinearLayout


        /*
            Adding existing games and new games to the list
         */
        val gamesInMemory = GameController.getAllGames()
        gamesInMemory.forEach {
            addButton(root, gameSelection, it)
        }
        if (gamesInMemory.size > 0 && GameController.currentGame == GameController.defaultGame)
            root.findViewWithTag<Button>(gamesInMemory[0]).callOnClick()

        val buttonAddGame = root.findViewById<Button>(R.id.button_add_game)
        val inputText = root.findViewById<EditText>(R.id.input_add_game)
        buttonAddGame.setOnClickListener {
            val buttonName = inputText.text.toString()
            if (buttonName.isNotBlank()) {
                if (GameController.addGame(buttonName)) {

                    inputText.text.clear()
                    addButton(root, gameSelection, buttonName)
                    GameController.selectCurrentGame(buttonName)
                    highlightCurrentButton(root)
                    inputText.clearFocus()
                    hideKeyboard(root)

                }
            }
        }

        inputText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    buttonAddGame.callOnClick()
                    true
                }
                else -> false
            }
        }
        highlightCurrentButton(root)
        println("current game = "+GameController.currentGame)

        val buttonRemoveGame = root.findViewById<Button>(R.id.button_remove_game)
        buttonRemoveGame.tag = "0"
        buttonRemoveGame.setOnClickListener {
            var allGames = GameController.getAllGames()
            if (allGames.size > 0) {
                val canRemove = buttonRemoveGame.tag.toString()
                if (canRemove == "0") {
                    buttonRemoveGame.tag = "1"
                    buttonRemoveGame.text = getString(R.string.button_remove_confirm)
                } else {
                    val removedButton = root.findViewWithTag<Button>(GameController.currentGame)
                    if (removedButton != null) {
                        val layout = removedButton.parent as ViewGroup?

                        layout?.removeView(removedButton)
                        GameController.removeCurrentSave()

                        allGames = GameController.getAllGames()
                        if(allGames.size > 0){
                            GameController.selectCurrentGame(allGames[0])
                            //GameController.loadStatements()
                            highlightCurrentButton(root)
                        } else {
                            GameController.reset()
                        }

                    }
                    try {
                    } finally {

                    }
                    buttonRemoveGame.tag = "0"
                    buttonRemoveGame.text = getString(R.string.button_remove_game)
                }
            }

        }

        return root
    }


    /**
     * Create new button for inputted game name
     */
    private fun addButton(root : View, parent : LinearLayout, gameName : String) {
        val ctx = root.context
        val myButton = Button(ctx)
        myButton.text = gameName
        myButton.tag = gameName
        val buttonMargin = root.context.resources.getDimensionPixelSize(R.dimen.button_margin)
        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0,buttonMargin,0,buttonMargin)
        myButton.layoutParams = params;
        myButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.color_button_unfocused))



        myButton.setOnClickListener {
            val childCount = parent.childCount;
            for (i in 0 until childCount) {
                parent.getChildAt(i).setBackgroundColor(ContextCompat.getColor(ctx, R.color.color_button_unfocused))
            }
            myButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.color_button_focused))

            GameController.selectCurrentGame(gameName)

            val buttonRemoveGame = root.findViewById<Button>(R.id.button_remove_game)
            buttonRemoveGame.tag = "0"
            buttonRemoveGame.text = getString(R.string.button_remove_game)


        }
        //val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        parent.addView(myButton)
    }





    private fun highlightCurrentButton(root : View) {
        val btn = root.findViewWithTag<Button>(GameController.currentGame)
        try{
            btn.callOnClick()
        } catch (e : Exception) {
            println("Button highlight failed")
        }
    }

    private fun hideKeyboard(v: View) {
        val inputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.applicationWindowToken, 0)
    }

}
