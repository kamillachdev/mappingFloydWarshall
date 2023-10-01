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
    data class ArrowConnection(
        val button1: Button,
        val button2: Button,
        val textView: TextView
    )

    data class GraphNode(
        val button: Button,
        val connectedNodes: List<GraphNode>
    )

    class GraphSolver(private val size: Int, private val context: Context) {

        private val INF = Int.MAX_VALUE
        private lateinit var distances: Array<IntArray>

        fun runFloydWarshall(graph: List<GraphNode>, arrowConnections: List<ArrowConnection>): List<GraphNode> {
            initializeDistances(graph, arrowConnections)

            for (k in 0 until size) {
                for (i in 0 until size) {
                    for (j in 0 until size) {
                        if (distances[i][k] != INF && distances[k][j] != INF &&
                            distances[i][k] + distances[k][j] < distances[i][j]
                        ) {
                            distances[i][j] = distances[i][k] + distances[k][j]
                        }
                    }
                }
            }

            // Find the best path based on the distances matrix
            val bestPath = findBestPath(graph)

            // Highlight the best path
            highlightBestPath(graph, arrowConnections, bestPath)

            return bestPath
        }

        private fun initializeDistances(graph: List<GraphNode>, arrowConnections: List<ArrowConnection>) {
            distances = Array(size) { IntArray(size) { INF } }

            for (i in 0 until size) {
                distances[i][i] = 0
                val currentNode = graph[i]
                for (neighbor in currentNode.connectedNodes) {
                    val connection = arrowConnections.find {
                        (it.button1 == currentNode.button && it.button2 == neighbor.button) ||
                                (it.button1 == neighbor.button && it.button2 == currentNode.button)
                    }
                    val neighborIndex = graph.indexOfFirst { it.button == neighbor.button }
                    val weight = connection?.textView?.text?.toString()?.toIntOrNull() ?: 0
                    distances[i][neighborIndex] = weight
                }
            }
        }

        private fun findBestPath(graph: List<GraphNode>): List<GraphNode> {
            // Find the best path based on the distances matrix
            var bestPath: List<GraphNode>? = null
            var minWeight = INF

            for (i in 0 until size) {
                for (j in 0 until size) {
                    if (i != j && distances[i][j] < minWeight) {
                        minWeight = distances[i][j]
                        bestPath = reconstructPath(graph, i, j)
                    }
                }
            }

            return bestPath ?: emptyList()
        }

        private fun reconstructPath(graph: List<GraphNode>, start: Int, end: Int): List<GraphNode> {
            val path = mutableListOf<GraphNode>()
            val stack = Stack<Int>()

            stack.push(end)

            while (stack.isNotEmpty()) {
                val current = stack.pop()
                path.add(0, graph[current])

                if (current == start) {
                    break
                }

                for (i in 0 until size) {
                    if (distances[start][current] == distances[start][i] + distances[i][current]) {
                        stack.push(i)
                        break
                    }
                }
            }

            return path
        }

        private fun highlightBestPath(graph: List<GraphNode>, arrowConnections: List<ArrowConnection>, bestPath: List<GraphNode>) {
            for (i in 0 until bestPath.size - 1) {
                val currentButton = bestPath[i].button
                val nextButton = bestPath[i + 1].button

                val connection = arrowConnections.find {
                    (it.button1 == currentButton && it.button2 == nextButton) ||
                            (it.button1 == nextButton && it.button2 == currentButton)
                }

                connection?.textView?.setTextColor(ContextCompat.getColor(context, R.color.green))
            }
        }
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ARROWCONNECTIONS INITIALIZATION
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
                val graphSolver = GraphSolver(graph.size, this)
                graphSolver.runFloydWarshall(graph, arrowConnections)
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
}