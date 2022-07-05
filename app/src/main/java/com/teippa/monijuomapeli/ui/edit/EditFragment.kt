package com.teippa.monijuomapeli.ui.edit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.teippa.monijuomapeli.GameController
import com.teippa.monijuomapeli.R
import java.util.*

class EditFragment : Fragment() {

    var selectedStatement : String = "-1";

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit, container, false)
        val statementSelection = root.findViewById(R.id.statement_selection_box) as LinearLayout

        val headerTextView = root.findViewById<EditText>(R.id.textview_edit_head)

        headerTextView.hint = GameController.currentGame.toUpperCase(Locale.getDefault())

        //GameController.loadStatements()

        var statementIndex = 0
        GameController.getAllStatements().forEach {
            displayStatement(root, statementSelection, it, statementIndex++)
        }

        val statementInput = root.findViewById<EditText>(R.id.input_add_statement)
        val buttonAddStatement = root.findViewById<Button>(R.id.button_add_statement)
        buttonAddStatement.setOnClickListener {
            val newStatement = statementInput.text.toString()

            if (!newStatement.isBlank()) {
                if (GameController.addStatement(newStatement)) {
                    displayStatement(root, statementSelection, newStatement, statementIndex++)
                    statementInput.text.clear()
                } else {
                    Toast.makeText(root.context, "No game selected", Toast.LENGTH_LONG).show()
                }
            }
        }
        statementInput.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    buttonAddStatement.callOnClick()
                    true
                }
                else -> false
            }
        }


        headerTextView.setOnEditorActionListener { view, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    updateGameName(headerTextView)
                    view.clearFocus()
                    true
                }
                else -> false
            }
        }


        headerTextView.setOnFocusChangeListener { view, b ->
            if (!b) {
                println("FOCUS CLEARED")
                updateGameName(view as EditText)
            }
        }

        val buttonRemoveStatement = root.findViewById<Button>(R.id.button_remove_statement)
        buttonRemoveStatement.tag = "0"
        buttonRemoveStatement.setOnClickListener {
            //Toast.makeText(root.context,"Not implemented yet",Toast.LENGTH_SHORT).show();

            if (GameController.getAllStatements().size > 0 && selectedStatement != "-1") {
                val canRemove = buttonRemoveStatement.tag.toString()
                if (canRemove == "0") {
                    buttonRemoveStatement.tag = "1"
                    buttonRemoveStatement.text = getString(R.string.button_remove_confirm)
                } else {
                    val removedTextview = root.findViewWithTag<TextView>(selectedStatement)
                    if (removedTextview != null) {
                        val layout = removedTextview.parent as ViewGroup?

                        if (GameController.removeStatementStr(removedTextview.tag.toString())) {
                            layout?.removeView(removedTextview)
                            selectedStatement = "-1"

                        }

                    }
                    try {
                    } finally {

                    }
                    buttonRemoveStatement.tag = "0"
                    buttonRemoveStatement.text = getString(R.string.button_remove_statement)
                }
            }
        }

        return root
    }

    private fun displayStatement(root: View, parent : LinearLayout, statement : String, index : Int) {
        val ctx = root.context
        val myTextView = TextView(ctx)
        val textViewText = "${index+1}. $statement"
        myTextView.text = textViewText
        myTextView.tag = statement
        myTextView.setTextColor(ContextCompat.getColor(ctx, R.color.color_text_main))

        myTextView.setOnClickListener{

            val childCount = parent.childCount;
            for (i in 0 until childCount) {
                parent.getChildAt(i).setBackgroundColor(ContextCompat.getColor(ctx, R.color.color_background_light))
            }
            myTextView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.color_background_dark))
            selectedStatement = myTextView.tag.toString()

            val buttonRemoveStatement = root.findViewById<Button>(R.id.button_remove_statement)
            buttonRemoveStatement.tag = "0"
            buttonRemoveStatement.text = getString(R.string.button_remove_statement)
        }


        parent.addView(myTextView)
    }



    private fun hideKeyboard(v: View) {
        val inputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.applicationWindowToken, 0)
    }

    private fun updateGameName(view: EditText) {
        val newName = view.text.toString()
        if (newName.isNotBlank()) {
            GameController.copyGame(newName)
            GameController.selectCurrentGame(newName)
        }

        view.clearFocus()
        hideKeyboard(view)
        view.hint = GameController.currentGame.toUpperCase(Locale.getDefault())
        view.text.clear()
    }
}
