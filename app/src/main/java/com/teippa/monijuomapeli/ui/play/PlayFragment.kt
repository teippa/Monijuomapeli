package com.teippa.monijuomapeli.ui.play

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.teippa.monijuomapeli.GameController
import com.teippa.monijuomapeli.R
import java.util.*


class PlayFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_play, container, false)


        val textViewGameName = root.findViewById<TextView>(R.id.textview_game_header)
        textViewGameName.text = if (GameController.currentGame.isNotBlank()) {
            "${GameController.currentGame}..."
        } else {
            "No game selected"
        }
        val statementCounter = root.findViewById<TextView>(R.id.textview_statement_counter)

        val statement = root.findViewById<TextView>(R.id.textview_statement)

        val transitionDrawable = getBackgroundTransition(root.context)
        root.background = transitionDrawable
        transitionDrawable.startTransition(0)

        statementCounter.text = GameController.getStatementCounter()
        statement.text = GameController.getCurrentStatement().toUpperCase(Locale.getDefault())
        root.findViewById<ConstraintLayout>(R.id.statement_container).setOnClickListener {
            statementCounter.text = GameController.getStatementCounter()
            statement.text = GameController.nextStatementRnd().toUpperCase(Locale.ROOT)

            transitionDrawable.startTransition(500)
        }

        return root
    }

    private fun getBackgroundTransition(ctx: Context) : TransitionDrawable {

        val colorEffect = intArrayOf(
            ContextCompat.getColor(ctx, R.color.color_background_light),
            ContextCompat.getColor(ctx, R.color.colorAccent),
            ContextCompat.getColor(ctx, R.color.color_background_light)
        )
        val colorMain = intArrayOf(
            ContextCompat.getColor(ctx, R.color.color_background_light),
            ContextCompat.getColor(ctx, R.color.color_background_light)
        )
        val gd1 = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colorEffect)
        gd1.gradientType = GradientDrawable.LINEAR_GRADIENT
        gd1.setGradientCenter(0.5.toFloat(), 0.5.toFloat())

        val gd2 = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colorMain)

        return TransitionDrawable(
            arrayOf(
                gd1,
                gd2
            )
        )
    }

}
