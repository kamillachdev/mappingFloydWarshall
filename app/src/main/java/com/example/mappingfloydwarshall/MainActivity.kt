package com.example.mappingfloydwarshall

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Stack

class MainActivity : AppCompatActivity() {

    var seekBarProgress = 0
    var selectedFirstButton: Button? = null //used for setting values between pictures
    var selectedSecondButton: Button? = null //used for setting values between pictures

    var selectedStartRouteButon: Button? = null //first clicked button that will work as the starting point
    var selectedEndRouteButon: Button? = null //last clicked button before clicking set the route button that will work as the ending point


    lateinit var arrowConnections: MutableList<ArrowConnection>
    lateinit var graph: List<GraphNode>
    data class ArrowConnection(
        val button1: Button,
        val button2: Button,
        val textView: TextView
    )

    data class GraphNode(
        val button: Button,
        val connectedNodes: List<GraphNode>
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //CONNECTIONS INITIALIZATION
        arrowConnections = mutableListOf(
            ArrowConnection(findViewById(R.id.forestButton), findViewById(R.id.swampsButton), findViewById(R.id.forestSwampsText)),
            ArrowConnection(findViewById(R.id.forestButton), findViewById(R.id.mountainsButton), findViewById(R.id.forestMountainsText)),
            ArrowConnection(findViewById(R.id.forestButton), findViewById(R.id.caveButton), findViewById(R.id.forestCaveText)),
            ArrowConnection(findViewById(R.id.mountainsButton), findViewById(R.id.swampsButton), findViewById(R.id.mountainsSwampsText)),
            ArrowConnection(findViewById(R.id.mountainsButton), findViewById(R.id.caveButton), findViewById(R.id.mountainsCaveText)),
            ArrowConnection(findViewById(R.id.mountainsButton), findViewById(R.id.jungleButton), findViewById(R.id.mountainsJungleText)),
            ArrowConnection(findViewById(R.id.swampsButton), findViewById(R.id.jungleButton), findViewById(R.id.swampsJungleText)),
            ArrowConnection(findViewById(R.id.caveButton), findViewById(R.id.jungleButton), findViewById(R.id.caveJungleText))
        )

        val graph: List<GraphNode> = listOf(
            GraphNode(findViewById(R.id.forestButton), listOf(
                GraphNode(findViewById(R.id.swampsButton), emptyList()),
                GraphNode(findViewById(R.id.mountainsButton), emptyList()),
                GraphNode(findViewById(R.id.caveButton), emptyList())
            )),
            GraphNode(findViewById(R.id.swampsButton), listOf(
                GraphNode(findViewById(R.id.forestButton), emptyList()),
                GraphNode(findViewById(R.id.mountainsButton), emptyList()),
                GraphNode(findViewById(R.id.jungleButton), emptyList())
            )),
            GraphNode(findViewById(R.id.mountainsButton), listOf(
                GraphNode(findViewById(R.id.forestButton), emptyList()),
                GraphNode(findViewById(R.id.swampsButton), emptyList()),
                GraphNode(findViewById(R.id.caveButton), emptyList()),
                GraphNode(findViewById(R.id.jungleButton), emptyList())
            )),
            GraphNode(findViewById(R.id.caveButton), listOf(
                GraphNode(findViewById(R.id.forestButton), emptyList()),
                GraphNode(findViewById(R.id.mountainsButton), emptyList()),
                GraphNode(findViewById(R.id.jungleButton), emptyList())
            )),
            GraphNode(findViewById(R.id.jungleButton), listOf(
                GraphNode(findViewById(R.id.swampsButton), emptyList()),
                GraphNode(findViewById(R.id.mountainsButton), emptyList()),
                GraphNode(findViewById(R.id.caveButton), emptyList())
            ))
        )



        //GETTING DEFAULT FONT COLOR TO SET IT BACK ON RESET
        val defaultTextColor = findViewById<TextView>(R.id.forestMountainsText).currentTextColor

        //START ALERT
        val builder = AlertDialog.Builder(this)
        builder.setTitle("OBSŁUGA PROGRAMU")
        builder.setMessage("1. Naciśnij jeden z obrazków\n2. Wybierz wagę za pomocą slider'a\n3. Naciśnij inny obrazek, który jest połączony strzałką z poprzednio klikniętym obrazkiem\n4. Powtarzaj poprzednie 3 kroki aby ustalić wiecej wag(opcjonalne)\n5. Przycisk kliknięty jako pierwszy będzie użyty jako początek trasy, a ostatni jako koniec trasy\n6. Naciśnij przycisk 'WYZNACZ TRASĘ' aby wyznaczyć najkrótszą trasę - podświetli się ona na zielono\n7. Naciśnij przycisk 'RESET WAGI' aby wyzerować wszystkie ustawione wagi(opcjonalne)")
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

            override fun onStartTrackingTouch(seek: SeekBar?) {}

            override fun onStopTrackingTouch(seek: SeekBar?) {}
        })

        var startPictureSupport = 0
        //PICTURES BUTTONS FUNCTIONALITY
        val buttonsArray: Array<Button> = arrayOf(findViewById<Button>(R.id.forestButton), findViewById<Button>(R.id.swampsButton), findViewById<Button>(R.id.mountainsButton), findViewById<Button>(R.id.caveButton), findViewById<Button>(R.id.jungleButton))
        buttonsArray.forEachIndexed { index, button ->
            button.setOnClickListener {
                if(seekBarProgress != 0) {
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
            val isRouteValid = isRouteValid(graph, selectedStartRouteButon, selectedEndRouteButon)

            if (selectedStartRouteButon == selectedEndRouteButon) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Błąd trasy")
                builder.setMessage("Punkt startowy i końcowy muszą być różne.")
                builder.setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
                val alertDialog = builder.create()
                alertDialog.show()
                return@setOnClickListener
            }

            if (isRouteValid) {
                runFloydWarshall(graph, arrowConnections)
                highlightShortestPath(graph, arrowConnections)
            }
            else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Błąd trasy")
                builder.setMessage("Nie istnieje trasa pomiędzy punktem startowym a końcowym.")
                builder.setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
                val alertDialog = builder.create()
                alertDialog.show()
            }
        }

        //RESET BUTTON FUNCTIONALITY
        val resetButton = findViewById<Button>(R.id.resetButton)

        resetButton.setOnClickListener {
            arrowConnections.forEach { connection ->
                connection.textView.text = "0"
                connection.textView.setTextColor(defaultTextColor)
                selectedEndRouteButon = null
                selectedStartRouteButon = null
                startPictureSupport = 0

                weightText.text = "0"
                seek.progress = 0
                seekBarProgress = 0
            }
        }
    }

    //METHODS USED FOR CHECKING IF ANY ROUTE EXISTS
    private fun isRouteValid(graph: List<GraphNode>, start: Button?, end: Button?): Boolean {
        if (start == null || end == null) return false

        val visited = mutableSetOf<Button>()
        return dfs(graph, start, end, visited)
    }

    private fun dfs(graph: List<GraphNode>, current: Button, end: Button, visited: MutableSet<Button>): Boolean {
        if (current == end) {
            return true
        }

        visited.add(current)

        val currentNode = graph.find { it.button == current }

        currentNode?.connectedNodes?.forEach { neighbor ->
            val connection = arrowConnections.find {
                (it.button1 == current && it.button2 == neighbor.button) || (it.button1 == neighbor.button && it.button2 == current)
            }

            if (connection?.textView?.text?.toString()?.toIntOrNull() != 0 && neighbor.button !in visited) {
                if (dfs(graph, neighbor.button, end, visited)) {
                    return true
                }
            }
        }

        return false
    }

    //METHODS USED FOR ESTABLISHING THE BEST ROUTE
    private lateinit var distances: Array<IntArray>
    private val MAXINT = Int.MAX_VALUE
    private fun runFloydWarshall(graph: List<GraphNode>, arrowConnections: List<ArrowConnection>) {
        val numberOfVertices = graph.size
        distances = Array(numberOfVertices) { IntArray(numberOfVertices) { MAXINT } }

        for (i in 0 until numberOfVertices) {
            distances[i][i] = 0
        }

        for (connection in arrowConnections) {
            val (button1, button2, textView) = connection
            val button1Index = graph.indexOfFirst { it.button == button1 }
            val button2Index = graph.indexOfFirst { it.button == button2 }
            distances[button1Index][button2Index] = textView.text.toString().toInt()
            distances[button2Index][button1Index] = textView.text.toString().toInt()
        }

        for (k in 0 until numberOfVertices) {
            for (i in 0 until numberOfVertices) {
                for (j in 0 until numberOfVertices) {
                    if (distances[i][k] != MAXINT && distances[k][j] != MAXINT &&
                        distances[i][k] + distances[k][j] < distances[i][j]
                    ) {
                        distances[i][j] = distances[i][k] + distances[k][j]
                    }
                }
            }
        }
    }

    private fun highlightShortestPath(graph: List<GraphNode>, arrowConnections: List<ArrowConnection>) {
        val start = selectedStartRouteButon
        val end = selectedEndRouteButon

        if (start != null && end != null) {
            val startNode = graph.find { it.button == start }
            val endNode = graph.find { it.button == end }

            val path = getShortestPath(startNode, endNode)

            for (i in path.indices) {
                if (i < path.size - 1) {
                    val connection = arrowConnections.find {
                        (it.button1 == path[i].button && it.button2 == path[i + 1].button) ||
                                (it.button1 == path[i + 1].button && it.button2 == path[i].button)
                    }
                    connection?.textView?.setTextColor(ContextCompat.getColor(this, R.color.green))
                }
            }
        }
    }

    private fun getShortestPath(start: GraphNode?, end: GraphNode?): List<GraphNode> {
        val path = mutableListOf<GraphNode>()

        if (start != null && end != null) {
            path.add(start)
            val startNodeIndex = graph.indexOf(start)
            val endNodeIndex = graph.indexOf(end)

            var current = endNodeIndex

            while (current != startNodeIndex) {
                for (i in graph.indices) {
                    if (distances[startNodeIndex][current] == distances[startNodeIndex][i] + distances[i][current]) {
                        path.add(graph[i])
                        current = i
                        break
                    }
                }
            }
        }

        return path.reversed()
    }
}
