package com.example.mappingfloydwarshall

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    var seekBarProgress = 0
    private var selectedFirstButton: Button? = null //used for setting values between pictures
    private var selectedSecondButton: Button? = null //used for setting values between pictures

    private var selectedStartRouteButton: Button? =
        null //first clicked button that will work as the starting point
    private var selectedEndRouteButton: Button? =
        null //last clicked button before clicking set the route button that will work as the ending point


    private lateinit var arrowConnections: MutableList<ArrowConnection>
    private lateinit var buttonConnections: MutableList<ButtonConnection>

    data class ArrowConnection(
        val button1: Button,
        val button2: Button,
        val textView: TextView
    )

    data class ButtonConnection(
        val button: Button,
        val textView1: TextView,
        val textView2: TextView,
        val textView3: TextView,
        val textView4: TextView?
    )


    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //CONNECTIONS INITIALIZATION
        arrowConnections = mutableListOf(
            ArrowConnection(
                findViewById(R.id.forestButton),
                findViewById(R.id.swampsButton),
                findViewById(R.id.forestSwampsText)
            ),
            ArrowConnection(
                findViewById(R.id.forestButton),
                findViewById(R.id.mountainsButton),
                findViewById(R.id.forestMountainsText)
            ),
            ArrowConnection(
                findViewById(R.id.forestButton),
                findViewById(R.id.caveButton),
                findViewById(R.id.forestCaveText)
            ),
            ArrowConnection(
                findViewById(R.id.mountainsButton),
                findViewById(R.id.swampsButton),
                findViewById(R.id.mountainsSwampsText)
            ),
            ArrowConnection(
                findViewById(R.id.mountainsButton),
                findViewById(R.id.caveButton),
                findViewById(R.id.mountainsCaveText)
            ),
            ArrowConnection(
                findViewById(R.id.mountainsButton),
                findViewById(R.id.jungleButton),
                findViewById(R.id.mountainsJungleText)
            ),
            ArrowConnection(
                findViewById(R.id.swampsButton),
                findViewById(R.id.jungleButton),
                findViewById(R.id.swampsJungleText)
            ),
            ArrowConnection(
                findViewById(R.id.caveButton),
                findViewById(R.id.jungleButton),
                findViewById(R.id.caveJungleText)
            )
        )

        buttonConnections = mutableListOf(
            ButtonConnection(
                findViewById(R.id.forestButton),
                findViewById(R.id.forestSwampsText),
                findViewById(R.id.forestMountainsText),
                findViewById(R.id.forestCaveText),
                null
            ),
            ButtonConnection(
                findViewById(R.id.swampsButton),
                findViewById(R.id.forestSwampsText),
                findViewById(R.id.mountainsSwampsText),
                findViewById(R.id.swampsJungleText),
                null
            ),
            ButtonConnection(
                findViewById(R.id.jungleButton),
                findViewById(R.id.swampsJungleText),
                findViewById(R.id.mountainsJungleText),
                findViewById(R.id.caveJungleText),
                null
            ),
            ButtonConnection(
                findViewById(R.id.caveButton),
                findViewById(R.id.forestCaveText),
                findViewById(R.id.mountainsCaveText),
                findViewById(R.id.caveJungleText),
                null
            ),
            ButtonConnection(
                findViewById(R.id.mountainsButton),
                findViewById(R.id.forestMountainsText),
                findViewById(R.id.mountainsSwampsText),
                findViewById(R.id.mountainsJungleText),
                findViewById(R.id.mountainsCaveText),
            ),
        )


        //GETTING DEFAULT FONT COLOR TO SET IT BACK ON RESET
        val defaultTextColor = findViewById<TextView>(R.id.forestMountainsText).currentTextColor

        //START ALERT
        val builder = AlertDialog.Builder(this)
        builder.setTitle("OBSŁUGA PROGRAMU")
        builder.setMessage("1. Naciśnij jeden z obrazków\n2. Wybierz wagę za pomocą slider'a\n3. Naciśnij inny obrazek, który jest połączony strzałką z poprzednio klikniętym obrazkiem\n4. Powtarzaj poprzednie 3 kroki aby ustalić wiecej wag(opcjonalne)\n5. Przycisk kliknięty jako pierwszy będzie użyty jako początek trasy, a ostatni jako koniec trasy\n6. Naciśnij przycisk 'WYZNACZ TRASĘ' aby wyznaczyć najkrótszą trasę - podświetli się ona na zielono\n7. Naciśnij przycisk 'RESET WAGI' aby wyzerować wszystkie ustawione wagi(opcjonalne)")
        builder.setCancelable(false)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.cancel()
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

            override fun onStartTrackingTouch(seek: SeekBar?) {}

            override fun onStopTrackingTouch(seek: SeekBar?) {}
        })

        var startPictureSupport = 0
        //PICTURES BUTTONS FUNCTIONALITY
        val buttonsArray: Array<Button> = arrayOf(
            findViewById(R.id.forestButton),
            findViewById(R.id.swampsButton),
            findViewById(R.id.mountainsButton),
            findViewById(R.id.caveButton),
            findViewById(R.id.jungleButton)
        )
        buttonsArray.forEachIndexed { _, button ->
            button.setOnClickListener {
                if (seekBarProgress != 0) {
                    if (startPictureSupport == 0) {
                        selectedStartRouteButton = button
                    }

                    if (selectedFirstButton == null) {
                        selectedFirstButton = button
                    } else if (selectedSecondButton == null) {
                        selectedSecondButton = button

                        val arrowConnection = arrowConnections.find {
                            (it.button1 == selectedFirstButton && it.button2 == selectedSecondButton) || (it.button1 == selectedSecondButton && it.button2 == selectedFirstButton)
                        }

                        arrowConnection?.textView?.text = seekBarProgress.toString()

                        selectedEndRouteButton = selectedSecondButton

                        selectedFirstButton = null
                        selectedSecondButton = null
                    }
                    startPictureSupport += 1
                }
            }
        }


        val algorithmButton = findViewById<Button>(R.id.algorithmButton)

        algorithmButton.setOnClickListener {
            if (selectedStartRouteButton == selectedEndRouteButton) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Błąd trasy")
                builder.setMessage("Punkt startowy i końcowy muszą być różne.")
                builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                val alertDialog = builder.create()
                alertDialog.show()
                return@setOnClickListener
            }
            else {
                resetTextViewsColor(defaultTextColor)
                highlightBestPath(setTheBestPath(setTheBestPaths(selectedStartRouteButton, selectedEndRouteButton)))
            }
        }

        //RESET BUTTON FUNCTIONALITY
        val resetButton = findViewById<Button>(R.id.resetButton)

        resetButton.setOnClickListener {
            arrowConnections.forEach { connection ->
                connection.textView.text = "0"
                connection.textView.setTextColor(defaultTextColor)
                selectedEndRouteButton = null
                selectedStartRouteButton = null
                selectedFirstButton = null
                selectedSecondButton = null
                startPictureSupport = 0

                weightText.text = "0"
                seek.progress = 0
                seekBarProgress = 0
            }
        }
    }

    //METHODS USED FOR ESTABLISHING THE BEST ROUTE
    private fun resetTextViewsColor(defaultTextColor: Int)
    {
        //changes all of the textViews on defaultColor(used to prevent program from showing current and previous best paths to green)
        val textViews: List<TextView> = listOf(findViewById(R.id.forestSwampsText), findViewById(R.id.forestMountainsText), findViewById(R.id.forestCaveText), findViewById(R.id.mountainsSwampsText), findViewById(R.id.mountainsJungleText), findViewById(R.id.mountainsCaveText), findViewById(R.id.swampsJungleText), findViewById(R.id.caveJungleText))

        for (textView in textViews)
        {
            textView.setTextColor(defaultTextColor)
        }
    }

    private fun setTheBestPaths(startButton: Button?, endButton: Button?): MutableList<MutableList<TextView?>> {
        //variables declarations
        val startButtonConnection = buttonConnections.find { it.button == startButton }
        val endButtonConnection = buttonConnections.find { it.button == endButton }
        var currentButtonConnection: ButtonConnection?
        var fastestTextView: TextView?
        var isRouteFound: Boolean
        var findingPotentialFastestRoute: Boolean
        var findingPotentialFastestRoutes = true
        var previousTextView: TextView?
        val potentialFastestPaths: MutableList<MutableList<TextView?>> = mutableListOf()
        val fastestPath: MutableList<TextView?> = mutableListOf()

        while(findingPotentialFastestRoutes)
        {
            currentButtonConnection = startButtonConnection
            fastestTextView = null
            isRouteFound = false
            findingPotentialFastestRoute = true
            previousTextView = null
            while (findingPotentialFastestRoute)
            {
                val textViews = listOf(
                    currentButtonConnection?.textView1,
                    currentButtonConnection?.textView2,
                    currentButtonConnection?.textView3,
                    currentButtonConnection?.textView4
                )

                if(potentialFastestPaths.size == 0)
                {
                    for (textView in textViews)
                    {
                        if (textView != null && textView.text.toString() != "0")
                        {
                            fastestTextView = textView
                        }
                    }
                }
                else
                {
                    for (textView in textViews)
                    {
                        if (textView != null && textView.text.toString() != "0" && potentialFastestPaths.none { innerList -> textView in innerList })
                        {
                            fastestTextView = textView
                        }
                    }
                }

                for (textView in textViews)
                {
                    if (textView != null)
                    {
                        if(potentialFastestPaths.size == 0)
                        {
                            if (textView.text.toString() != "0" && textView != previousTextView)
                            {
                                isRouteFound = true
                                if (textView.text.toString().toInt() <= fastestTextView?.text.toString().toInt())
                                {
                                    fastestTextView = textView
                                }
                            }
                        }
                        else
                        {
                            if (textView.text.toString() != "0" && textView != previousTextView && potentialFastestPaths.none { innerList -> textView in innerList })
                            {
                                isRouteFound = true
                                if (textView.text.toString().toInt() <= fastestTextView?.text.toString().toInt())
                                {
                                            fastestTextView = textView
                                }
                            }
                        }
                    }
                }

                //adding fastestTextView to mutable list(fastestPath) that will be returned to the final highlightBestPath function
                fastestPath.add(fastestTextView)

                if (isRouteFound)
                {
                    var currentArrowButtonConnection: ArrowConnection?
                    currentArrowButtonConnection =
                        arrowConnections.find { it.textView == fastestTextView && it.button1 == currentButtonConnection?.button }

                    if (currentArrowButtonConnection == null)
                    {
                        currentArrowButtonConnection =
                            arrowConnections.find { it.textView == fastestTextView && it.button2 == currentButtonConnection?.button }
                        currentButtonConnection =
                            buttonConnections.find { it.button == currentArrowButtonConnection?.button1 }
                    }
                    else
                    {
                        currentButtonConnection =
                            buttonConnections.find { it.button == currentArrowButtonConnection.button2 }
                    }

                    if (currentArrowButtonConnection != null && endButtonConnection != null)
                    {
                        if (currentArrowButtonConnection.button1 == endButtonConnection.button || currentArrowButtonConnection.button2 == endButtonConnection.button)
                        {
                            potentialFastestPaths.add(fastestPath.toMutableList())
                            fastestPath.clear()
                            findingPotentialFastestRoute = false
                        }
                        else
                        {
                            //if best path is still not found, change the previousTextView to the previous fastestTextView to prevent algorithm from going backwards
                            previousTextView = fastestTextView
                        }
                    }
                }
                else
                {
                    if(potentialFastestPaths.size == 0)
                    {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Błąd trasy")
                        builder.setMessage("Nie istnieje trasa z punktu startowego do punktu końcowego.")
                        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        val alertDialog = builder.create()
                        alertDialog.show()
                        fastestPath.clear()
                        return potentialFastestPaths
                    }
                    else
                    {
                        findingPotentialFastestRoute = false
                        findingPotentialFastestRoutes = false
                    }
                }
            }
        }
        return potentialFastestPaths
    }

    private fun setTheBestPath(potentialFastestPaths: MutableList<MutableList<TextView?>>): MutableList<TextView?>
    {
        if(potentialFastestPaths.size > 0)
        {
            val sums: MutableList<Int> = mutableListOf()
            var sum = 0

            for (potentialFastestPath in potentialFastestPaths) {
                for (textView in potentialFastestPath) {
                    sum += textView?.text.toString().toInt()
                }
                sums.add(sum)
                sum = 0
            }

            var minSum = sums[0]
            var minSumIndex = 0

            for (i in sums.indices) {
                if (sums[i] < minSum) {
                    minSum = sums[i]
                    minSumIndex = i
                }
            }
            return potentialFastestPaths[minSumIndex]
        }
        else {
            return mutableListOf()
        }
    }

    private fun highlightBestPath(bestPath: MutableList<TextView?>)
    {
        if(bestPath.size > 0)
        {
            for (textView in bestPath)
            {
                textView?.setTextColor(ContextCompat.getColor(this, R.color.green))
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Najlepsza trasa")
            builder.setMessage("Najlepsza trasa została wyznaczona na zielono")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }
}