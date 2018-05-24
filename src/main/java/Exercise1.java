import com.sun.javafx.geom.Edge;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.Random;

public class Exercise1
{
    private SimpleWeightedGraph<String, DefaultWeightedEdge> graph;
    private boolean connected;

    public Exercise1()
    {
        connected = true;
    }

    public void createGraph()
    {
        graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        for(int i = 1; i <= 20; i++)
        {
            graph.addVertex(String.valueOf(i));
        }

        for(int i = 1; i < 20; i++)
        {
            DefaultWeightedEdge edge = new DefaultWeightedEdge();
            graph.addEdge(String.valueOf(i), String.valueOf(i + 1), edge);
            graph.setEdgeWeight(edge, 0.95);
        }
    }

    public void MonteCarloSimulation()
    {
        Random generator = new Random();

        for(int i = 0; i < 5; i++)
        {
/*            int firstVertexNumber = generator.nextInt(20) + 1;
            int secondVertexNumber = generator.nextInt(20) + 1;

            while(secondVertexNumber == firstVertexNumber)
            {
                secondVertexNumber = generator.nextInt(20) + 1;
            }

            DijkstraShortestPath dijkstraPath = new DijkstraShortestPath(graph);
            GraphPath path = dijkstraPath.getPath(firstVertexNumber, secondVertexNumber);

            for(int j = 0; j < path.getLength(); j++)
            {
                DefaultWeightedEdge e = (DefaultWeightedEdge)path.getEdgeList().get(j);

                int edgeDestroyed = generator.nextInt(101);
                if(edgeDestroyed > graph.getEdgeWeight(e) * 100)
                {
                    graph.removeEdge(e);
                    i--;
                    break;
                }
            }*/
            for(DefaultWeightedEdge e : graph.edgeSet())
            {
                double edgeDestroyed = generator.nextDouble();
                if(edgeDestroyed > graph.getEdgeWeight(e))
                {
                    graph.removeEdge(e);
                    break;
                }
            }

            ConnectivityInspector inspector = new ConnectivityInspector(graph);
            if(inspector.isGraphConnected() == false)
            {
                connected = false;
                return;
            }
        }
    }

    public void addEdge(String v1, String v2, double weight)
    {
        DefaultWeightedEdge e = new DefaultWeightedEdge();
        graph.addEdge(v1, v2, e);
        graph.setEdgeWeight(e, weight);
    }

    public void addRandomEdge()
    {
        while(true)
        {
            Random generator = new Random();

            int firstVertexNumber = generator.nextInt(20) + 1;
            int secondVertexNumber = generator.nextInt(20) + 1;

            while(secondVertexNumber == firstVertexNumber)
            {
                secondVertexNumber = generator.nextInt(20) + 1;
            }

            if(graph.getEdge(String.valueOf(firstVertexNumber), String.valueOf(secondVertexNumber)) == null)
            {
                addEdge(String.valueOf(firstVertexNumber), String.valueOf(secondVertexNumber), 0.4);
                break;
            }
        }
    }

    public static void main(String[] args)
    {
        int numberOfConnectedGraphs = 0;
        Exercise1 ex;
        for(int i = 0; i < 1000; i++)
        {
            ex = new Exercise1();
            ex.createGraph();
            ex.addEdge(String.valueOf(1), String.valueOf(20), 0.95);
            ex.addEdge(String.valueOf(1), String.valueOf(10), 0.8);
            ex.addEdge(String.valueOf(5), String.valueOf(15), 0.7);

            for(int j = 0; j < 4; j++)
            {
                ex.addRandomEdge();
            }

            ex.MonteCarloSimulation();

            if(ex.connected == true)
            {
                numberOfConnectedGraphs++;
            }
        }
        System.out.println(numberOfConnectedGraphs);
    }
}
