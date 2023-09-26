package com.example.mappingfloydwarshall

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    var seekBarProgress = 0
    var selectedFirstButton: Button? = null
    var selectedSecondButton: Button? = null

    data class ArrowConnection(
        val button1: Button,
        val button2: Button,
        val textView: TextView
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ARROWCONNECTIONS INITIALIZATION
        val arrowConnections = mutableListOf(
            ArrowConnection(findViewById(R.id.forestButton), findViewById(R.id.swampsButton), findViewById(R.id.forestSwampsText)),
            ArrowConnection(findViewById(R.id.forestButton), findViewById(R.id.mountainsButton), findViewById(R.id.forestMountainsText)),
            ArrowConnection(findViewById(R.id.forestButton), findViewById(R.id.caveButton), findViewById(R.id.forestCaveText)),
            ArrowConnection(findViewById(R.id.mountainsButton), findViewById(R.id.swampsButton), findViewById(R.id.mountainsSwampsText)),
            ArrowConnection(findViewById(R.id.mountainsButton), findViewById(R.id.caveButton), findViewById(R.id.mountainsCaveText)),
            ArrowConnection(findViewById(R.id.mountainsButton), findViewById(R.id.jungleButton), findViewById(R.id.mountainsJungleText)),
            ArrowConnection(findViewById(R.id.swampsButton), findViewById(R.id.jungleButton), findViewById(R.id.swampsJungleText)),
            ArrowConnection(findViewById(R.id.caveButton), findViewById(R.id.jungleButton), findViewById(R.id.caveJungleText))
        )

        //GETTING DEFAULT FONT COLOR TO SET IT BACK ON RESET
        val defaultTextColor = findViewById<TextView>(R.id.forestMountainsText).currentTextColor

        //START ALERT
        val builder = AlertDialog.Builder(this)
        builder.setTitle("OBSŁUGA PROGRAMU")
        builder.setMessage("1. Naciśnij jeden z obrazków\n2. Wybierz wagę za pomocą slider'a\n3. Naciśnij inny obrazek, który jest połączony strzałką z poprzednio klikniętym obrazkiem\n4. Powtarzaj poprzednie 3 kroki aby ustalić wiecej wag(opcjonalne)\n5. Naciśnij przycisk 'WYZNACZ TRASĘ' aby wyznaczyć najkrótszą trasę - podświetli się ona na zielono\n6. Naciśnij przycisk 'RESET WAGI' aby wyzerować wszystkie ustawione wagi(opcjonalne)")
        builder.setCancelable(false)
        builder.setPositiveButton("OK") {
            dialog, which -> dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()

        //SEEKBAR FUNCTIONALITY
        val weightText = findViewById<TextView>(R.id.weightText)
        val seek = findViewById<SeekBar>(R.id.weightSeekBar)

        seek?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                weightText.text = progress.toString()
                seekBarProgress = progress

            }

            override fun onStartTrackingTouch(seek: SeekBar?) {

            }

            override fun onStopTrackingTouch(seek: SeekBar?) {
            }
        })

        //PICTURES BUTTONS FUNCTIONALITY
        val buttonsArray: Array<Button> = arrayOf(findViewById<Button>(R.id.forestButton), findViewById<Button>(R.id.swampsButton), findViewById<Button>(R.id.mountainsButton), findViewById<Button>(R.id.caveButton), findViewById<Button>(R.id.jungleButton))
        buttonsArray.forEachIndexed { index, button ->
            button.setOnClickListener {
                if(selectedFirstButton == null) {
                    selectedFirstButton = button
                }
                else if(selectedSecondButton == null) {
                    selectedSecondButton = button

                    val arrowConnection = arrowConnections.find {
                        (it.button1 == selectedFirstButton && it.button2 == selectedSecondButton) || (it.button1 == selectedSecondButton && it.button2 == selectedFirstButton)
                    }

                    arrowConnection?.textView?.text = seekBarProgress.toString()

                    selectedFirstButton = null
                    selectedSecondButton = null
                }
            }
        }


        val algorithmButton = findViewById<Button>(R.id.algorithmButton)

        //CHECK IF ROUTE ON THE MAP IS SET CORRECTLY
        algorithmButton.setOnClickListener {

        }

        //RESET BUTTON FUNCTIONALITY
        val resetButton = findViewById<Button>(R.id.resetButton)

        resetButton.setOnClickListener {
            arrowConnections.forEach { connection ->
                connection.textView.text = "0"
                connection.textView.setTextColor(defaultTextColor)
            }
        }
    }
}