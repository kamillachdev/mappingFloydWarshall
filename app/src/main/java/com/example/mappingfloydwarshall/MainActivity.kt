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

class MainActivity : AppCompatActivity() {

    var seekBarProgress = 0
    var selectedFirstButton: Button? = null //used for setting values between pictures
    var selectedSecondButton: Button? = null //used for setting values between pictures

    var selectedStartRouteButon: Button? = null //first clicked button that will work as the starting point
    var selectedEndRouteButon: Button? = null //last clicked button before clicking set the route button that will work as the ending point


    lateinit var arrowConnections: MutableList<ArrowConnection>
    lateinit var buttonConnections: MutableList<ButtonConnection>
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


    @SuppressLint("MissingInflatedId")
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
        builder.setPositiveButton("OK") { dialog, which ->
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
            findViewById<Button>(R.id.forestButton),
            findViewById<Button>(R.id.swampsButton),
            findViewById<Button>(R.id.mountainsButton),
            findViewById<Button>(R.id.caveButton),
            findViewById<Button>(R.id.jungleButton)
        )
        buttonsArray.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (seekBarProgress != 0) {
                    if (startPictureSupport == 0) {
                        selectedStartRouteButon = button
                    }

                    if (selectedFirstButton == null) {
                        selectedFirstButton = button
                    } else if (selectedSecondButton == null) {
                        selectedSecondButton = button

                        val arrowConnection = arrowConnections.find {
                            (it.button1 == selectedFirstButton && it.button2 == selectedSecondButton) || (it.button1 == selectedSecondButton && it.button2 == selectedFirstButton)
                        }

                        arrowConnection?.textView?.text = seekBarProgress.toString()

                        selectedEndRouteButon = selectedSecondButton

                        selectedFirstButton = null
                        selectedSecondButton = null
                    }
                    startPictureSupport += 1
                }
            }
        }


        val algorithmButton = findViewById<Button>(R.id.algorithmButton)

        algorithmButton.setOnClickListener {
            if (selectedStartRouteButon == selectedEndRouteButon) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Błąd trasy")
                builder.setMessage("Punkt startowy i końcowy muszą być różne.")
                builder.setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
                val alertDialog = builder.create()
                alertDialog.show()
                return@setOnClickListener
            }

            val potentialRoutes = CheckPotentialRoutes(selectedStartRouteButon, selectedEndRouteButon)
            val bestPath = CheckBestPath(potentialRoutes)
            HighlightBestPath(bestPath)
        }

            //RESET BUTTON FUNCTIONALITY
            val resetButton = findViewById<Button>(R.id.resetButton)

            resetButton.setOnClickListener {
                arrowConnections.forEach { connection ->
                    connection.textView.text = "0"
                    connection.textView.setTextColor(defaultTextColor)
                    selectedEndRouteButon = null
                    selectedStartRouteButon = null
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
    private fun CheckPotentialRoutes(startButton: Button?, endButton: Button?): MutableList<MutableList<TextView>>
    {
        var potentialRoutes = mutableListOf<MutableList<TextView>>()
        //first grade routes
        var potentialGradeOneRoute = mutableListOf<TextView>()

        var correctConnection = arrowConnections.find { (it.button1 == startButton && it.button2 == endButton) || (it.button1 == endButton && it.button2 == startButton)}
        if (correctConnection != null) {
            potentialGradeOneRoute.add(correctConnection.textView)
            if (potentialGradeOneRoute != null && correctConnection.textView.text.toString() != "0") {
                potentialRoutes.add(potentialGradeOneRoute)
            }
        }
        //second grade routes    // work here - find and add potential routes for the second grade routes
        val startButtonTextViews = buttonConnections.find { it.button == startButton }
        val endButtonTextViews = buttonConnections.find { it.button == endButton }

        if (startButtonTextViews != null && endButtonTextViews != null)
        {
            for (buttonConnection in buttonConnections)
            {
                if (buttonConnection.button != startButtonTextViews.button && buttonConnection.button != endButtonTextViews.button)
                {
                    val potentialGradeTwoRoute = mutableListOf<TextView>()

                    // Check if any TextView from the current ButtonConnection exists in startButtonTextViews
                    if ((buttonConnection.textView1 == startButtonTextViews.textView1 || buttonConnection.textView1 == startButtonTextViews.textView2 || buttonConnection.textView1 == startButtonTextViews.textView3 || buttonConnection.textView1 == startButtonTextViews.textView4) && buttonConnection.textView1.text.toString() != "0")
                    {
                        potentialGradeTwoRoute.add(buttonConnection.textView1)
                    }
                    else if ((buttonConnection.textView2 == startButtonTextViews.textView1 || buttonConnection.textView2 == startButtonTextViews.textView2 || buttonConnection.textView2 == startButtonTextViews.textView3 || buttonConnection.textView2 == startButtonTextViews.textView4) && buttonConnection.textView2.text.toString() != "0")
                    {
                        potentialGradeTwoRoute.add(buttonConnection.textView2)
                    }
                    else if ((buttonConnection.textView3 == startButtonTextViews.textView1 || buttonConnection.textView3 == startButtonTextViews.textView2 || buttonConnection.textView3 == startButtonTextViews.textView3 || buttonConnection.textView3 == startButtonTextViews.textView4) && buttonConnection.textView3.text.toString() != "0")
                    {
                        potentialGradeTwoRoute.add(buttonConnection.textView3)
                    }
                    else if ((buttonConnection.textView4 != null && (buttonConnection.textView4 == startButtonTextViews.textView1 || buttonConnection.textView4 == startButtonTextViews.textView2 || buttonConnection.textView4 == startButtonTextViews.textView3 || buttonConnection.textView4 == startButtonTextViews.textView4)) && buttonConnection.textView4.text.toString() != "0")
                    {
                        potentialGradeTwoRoute.add(buttonConnection.textView4)
                    }


                    if ((buttonConnection.textView1 == endButtonTextViews.textView1 || buttonConnection.textView1 == endButtonTextViews.textView2 || buttonConnection.textView1 == endButtonTextViews.textView3 || buttonConnection.textView1 == endButtonTextViews.textView4) && buttonConnection.textView1.text.toString() != "0")
                    {
                        potentialGradeTwoRoute.add(buttonConnection.textView1)
                    }
                    else if ((buttonConnection.textView2 == endButtonTextViews.textView1 || buttonConnection.textView2 == endButtonTextViews.textView2 || buttonConnection.textView2 == endButtonTextViews.textView3 || buttonConnection.textView2 == endButtonTextViews.textView4) && buttonConnection.textView2.text.toString() != "0")
                    {
                        potentialGradeTwoRoute.add(buttonConnection.textView2)
                    }
                    else if((buttonConnection.textView3 == endButtonTextViews.textView1 || buttonConnection.textView3 == endButtonTextViews.textView2 || buttonConnection.textView3 == endButtonTextViews.textView3 || buttonConnection.textView3 == endButtonTextViews.textView4) && buttonConnection.textView3.text.toString() != "0")
                    {
                        potentialGradeTwoRoute.add(buttonConnection.textView3)
                    }
                    else if((buttonConnection.textView4 != null && (buttonConnection.textView4 == endButtonTextViews.textView1 || buttonConnection.textView4 == endButtonTextViews.textView2 || buttonConnection.textView4 == endButtonTextViews.textView3 || buttonConnection.textView4 == endButtonTextViews.textView4)) && buttonConnection.textView4.text.toString() != "0")
                    {
                        potentialGradeTwoRoute.add(buttonConnection.textView4)
                    }

                    if(potentialGradeTwoRoute.size == 2)
                    {
                        potentialRoutes.add(potentialGradeTwoRoute)
                    }
                }
            }
        }
        //third grade routes    // work here - find potential routes for the third grade routes

        return potentialRoutes
    }

    private fun CheckBestPath(potentialRoutes: MutableList<MutableList<TextView>>): MutableList<TextView>?
    {
        if(potentialRoutes.isNullOrEmpty())
        {
            return null
        }
        else if(potentialRoutes.size == 1)
        {
            return potentialRoutes[0]
        }
        else
        {
            val sumsOfConnectionsWeights = mutableListOf<Int>()
            var sum = 0
            for (potentialRoute in potentialRoutes) {
                for (textView in potentialRoute) {
                    sum += textView.text.toString().toInt()
                }
                sumsOfConnectionsWeights.add(sum)
                sum = 0
            }
            var minIndex = 0
            var min = sumsOfConnectionsWeights[0]
            for (i in 1 until sumsOfConnectionsWeights.size) {
                if (sumsOfConnectionsWeights[i] < min) {
                    min = sumsOfConnectionsWeights[i]
                    minIndex = i
                }
            }
            return potentialRoutes[minIndex]
        }
    }
    private fun HighlightBestPath(bestPath: MutableList<TextView>?)
    {
        val defaultTextColor = findViewById<TextView>(R.id.forestMountainsText).currentTextColor
        if(bestPath.isNullOrEmpty())
        {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Błąd trasy")
            builder.setMessage("Nie istnieje trasa z punktu startowego do punktu końcowego.")
            builder.setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
            val alertDialog = builder.create()
            alertDialog.show()
        }
        else {
            val textViews: List<TextView> = listOf(findViewById(R.id.forestSwampsText), findViewById(R.id.forestMountainsText), findViewById(R.id.forestCaveText), findViewById(R.id.mountainsSwampsText), findViewById(R.id.mountainsJungleText), findViewById(R.id.mountainsCaveText), findViewById(R.id.swampsJungleText), findViewById(R.id.caveJungleText))
            for (textView in textViews)
            {
                textView.setTextColor(defaultTextColor)
            }
            for(bestPathTextView in bestPath)
            {
                bestPathTextView.setTextColor(ContextCompat.getColor(this, R.color.green))
            }
        }

    }
}

/*

Jak ustalic najlepsza trase?
Na postawie punktu startowego i koncowego trzeba ustalic
wszystkie *potencjalne trasy*, i sprawdzic, czy ktoras z nich jest
mozliwa do przejscia za pomoca listy activeConnections, ktora
przechowuje aktywne polaczenia, jezeli trasa istnieje, to
zapisuje ja do listy list, kazda lista listy bedzie zawierac
textViews ktore tworza trase od pierwszego polaczenia do
ostatniego, jezeli mozliwa trasa bedzie tylko jedna, to od razu
wyswietlam ja na zielono i gotowe, jezeli nie ma zadnej
mozliwej trasy, to wyswietlam powiadomienie ze zadna trasa nie
jest mozliwa, a jezeli mozliwych tras jest wiecej niz jedna, to
przechodze petlami przez kazda liste i obliczam sume wartosci
ktore posiadaja textViews, ta lista ktora bedzie miec
najmniejsza sume podswietlana jest na zielono, KONIEC

Jak ustalic wszystkie *potencjalne trasy*?
Najpierw sprawdzam czy jest mozliwe polaczenie 1-stopniowe(tylko
jedno laczenie pomiedzy pierwszym i ostatnim punktem)(moze
wystapic tylko raz, jezeli jest znalezione to omijane
jest szukanie 3-stopniowych), jezeli
jest to dodaje je do potencjalncych sciezek, potem sprawdzam
polaczenia 2-stopniowe, czyli takie ktore sa polaczone z obrazkiem,
ktory laczy obrazek startowy i koncowy(laczenie
tego typu moze wystapic trzy razy),na koncu sprawdzamy
polaczenia 3-stopniowe, czyli takie ktore wystepuja tylko
jezeli punkt startowy i koncowy sa na przeciwko siebie po bokach
grafu, trasa ta zawsze przechodzi przez bok, potem srodek, i do
celu koncowego, sa one zawsze dwie

Jak ustalic trasy 2-stopnia?
przechodze przez kazdego buttona i sprawdzam za pomoca listy,
ktory posiada textViews nalezace do start i end Buttons,
jezeli takie posiada, to dodaje je do listy

 */