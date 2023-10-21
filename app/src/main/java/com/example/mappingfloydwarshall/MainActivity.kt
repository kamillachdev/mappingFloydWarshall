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
    data class ArrowConnection(
        val button1: Button,
        val button2: Button,
        val textView: TextView
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

            //here will be called method that will set the best path
            val activeConnectionsList = CheckConnections()
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
    private fun CheckConnections(): List<TextView>
    {
        val activeConnections = mutableListOf<TextView>()
        for(connection in arrowConnections) {
            val text = connection.textView.text.toString()
            if (text != "0") {
                activeConnections.add(connection.textView)
            }
        }
        return activeConnections
    }

    private fun CheckPotentialRoutes(startButton: Button?, endButton: Button?): MutableList<MutableList<TextView>>
    {
        var potentialRoutes = mutableListOf<MutableList<TextView>>()
        //first grade routes
        var potentialGradeOneRoutes = mutableListOf<TextView>()
        var potentialGradeOneRoute = arrowConnections.find { (it.button1 == startButton && it.button2 == endButton) || (it.button1 == endButton && it.button2 == startButton)}
        if (potentialGradeOneRoute != null) {
            potentialGradeOneRoutes.add(potentialGradeOneRoute.textView)
        }
        potentialRoutes.add(potentialGradeOneRoutes)
        //second grade routes    // work here - find and add potential routes for the second grade routes

        //third grade routes    // work here - find potential routes for the second grade routes

        return potentialRoutes
    }

    private fun CheckBestPath(potentialRoutes: MutableList<MutableList<TextView>>): MutableList<TextView>
    {
        val bestPath = mutableListOf<TextView>()
        for(textView in potentialRoutes[0]) { //work here - if there is only one potentialRoute, return it instantly, if there is no potentialRoutes, return null, and then if bestPath in highlight method is null, show alert, that there are no paths, and if there are more the one paths, count their weight and return the one that have the least weight
            bestPath.add(textView)
        }
        return bestPath
    }
    private fun HighlightBestPath(bestPath: MutableList<TextView>)
    {
        for(textView in bestPath) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.green))
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

 */