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
            graph.addVertex("v" + i);
        }

        for(int i = 1; i < 20; i++)
        {
            DefaultWeightedEdge edge = new DefaultWeightedEdge();
            graph.addEdge("v" + i, "v" + (i + 1), edge);
            graph.setEdgeWeight(edge, 0.95);
        }
    }

    public void MonteCarloSimulation()
    {
        Random generator = new Random();

        for(int i = 0; i < 10; i++)
        {
            int firstVertexNumber = generator.nextInt(20) + 1;
            int secondVertexNumber = generator.nextInt(20) + 1;

            while(secondVertexNumber == firstVertexNumber)
            {
                secondVertexNumber = generator.nextInt(20) + 1;
            }

            DijkstraShortestPath dijkstraPath = new DijkstraShortestPath(graph);
            GraphPath path = dijkstraPath.getPath("v" + firstVertexNumber, "v" + secondVertexNumber);

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
            }

            ConnectivityInspector inspector = new ConnectivityInspector(graph);
            if(inspector.isGraphConnected() == false)
            {
                connected = false;
                return;
            }
        }

/*        ConnectivityInspector finalInspector = new ConnectivityInspector(graph);
        if(finalInspector.isGraphConnected() == true)
        {
            connected = true;
        }*/
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

            if(graph.getEdge("v" + firstVertexNumber, "v" + secondVertexNumber) == null)
            {
                addEdge("v" + firstVertexNumber, "v" + secondVertexNumber, 0.4);
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
            ex.addEdge("v1", "v20", 0.95);
            ex.addEdge("v1", "v10", 0.8);
            ex.addEdge("v5", "v15", 0.7);

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
