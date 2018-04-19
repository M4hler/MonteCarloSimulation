import org.jgrapht.GraphPath;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleGraph;

import java.io.*;
import java.util.Random;

public class Exercise2
{
    private SimpleGraph<String, MultipleWeightsEdge> graph;
    private double [][] testMatrix;
    private boolean connected;

    public Exercise2()
    {
        connected = true;
        testMatrix = new double[][] {
                {0, 5, 0, 3, 4, 0, 8, 1, 6, 1},
                {5, 0, 1, 3, 2, 3, 1, 3, 1, 1},
                {0, 1, 0, 1, 3, 3, 1, 2, 2, 3},
                {3, 3, 1, 0, 1, 3, 1, 0, 2, 4},
                {4, 2, 3, 1, 0, 7, 3, 1, 1, 1},
                {0, 3, 3, 3, 7, 0, 4, 1, 2, 2},
                {8, 1, 1, 1, 3, 4, 0, 4, 1, 2},
                {1, 3, 2, 0, 1, 1, 4, 0, 0, 3},
                {6, 1, 2, 2, 1, 2, 1, 0, 0, 4},
                {1, 1, 3, 4, 1, 2, 2, 3, 4, 0}
        };
    }

    public void createGraph()
    {
        graph = new SimpleGraph<>(MultipleWeightsEdge.class);

        for (int i = 1; i <= 10; i++)
        {
            graph.addVertex("v" + i);
        }

    }

    public void addEdge(String v1, String v2, double weight1, double weight2, double weight3)
    {
        MultipleWeightsEdge e = new MultipleWeightsEdge(weight1, weight2, weight3);
        graph.addEdge(v1, v2, e);
    }

    public void setWeights()
    {
        for(int i = 0; i < 10; i++)
        {
            for(int j = i + 1; j < 10; j++)
            {
                int firstVertex = i + 1;
                int secondVertex = j + 1;

                DijkstraShortestPath dijkstraPath = new DijkstraShortestPath(graph);
                GraphPath path = dijkstraPath.getPath("v" + firstVertex, "v" + secondVertex);

                for(int k = 0; k < path.getLength(); k++)
                {
                    MultipleWeightsEdge e = (MultipleWeightsEdge)path.getEdgeList().get(k);
                    e.setFlow(e.getFlow() + testMatrix[i][j]);
                    double newFlow = e.getFlow();
                    e.setCapacity(newFlow + 0.5 * newFlow);
                }
            }
        }
    }

    public boolean adjustWeights()
    {
        for(MultipleWeightsEdge e : graph.edgeSet())
        {
            e.setFlow(0);
        }

        for(int i = 0; i < 10; i++)
        {
            for(int j = i + 1; j < 10; j++)
            {
                int firstVertex = i + 1;
                int secondVertex = j + 1;

                DijkstraShortestPath dijkstraPath = new DijkstraShortestPath(graph);
                GraphPath path = dijkstraPath.getPath("v" + firstVertex, "v" + secondVertex);

                if(path == null)
                {
                    return false;
                }

                for(int k = 0; k < path.getLength(); k++)
                {
                    MultipleWeightsEdge e = (MultipleWeightsEdge)path.getEdgeList().get(k);
                    e.setFlow(e.getFlow() + testMatrix[i][j]);
                    if(e.getFlow() > e.getCapacity())
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void MonteCarloSimulation()
    {
        Random generator = new Random();

        for(int i = 0; i < 10; i++)
        {
            int firstVertexNumber = generator.nextInt(10) + 1;
            int secondVertexNumber = generator.nextInt(10) + 1;

            while (secondVertexNumber == firstVertexNumber) {
                secondVertexNumber = generator.nextInt(10) + 1;
            }

            DijkstraShortestPath dijkstraPath = new DijkstraShortestPath(graph);
            GraphPath path = dijkstraPath.getPath("v" + firstVertexNumber, "v" + secondVertexNumber);

            for (int j = 0; j < path.getLength(); j++)
            {
                MultipleWeightsEdge e = (MultipleWeightsEdge) path.getEdgeList().get(j);

                int edgeDestroyed = generator.nextInt(101);
                if (edgeDestroyed > e.getEndurance() * 100)
                {
                    graph.removeEdge(e);
                    i--;
                    boolean b = adjustWeights();

                    if(b == false)
                    {
                        connected = false;
                        return;
                    }
                    break;
                }
            }

            ConnectivityInspector inspector = new ConnectivityInspector(graph);
            if (inspector.isGraphConnected() == false)
            {
                connected = false;
                return;
            }
        }
    }

    public void averageDelay()
    {
        double flowMatrixSum = 0;
        int elementCount = 0;

        for(int i = 0; i < 10; i++)
        {
            for(int j = i + 1; j < 10; j++)
            {
                flowMatrixSum += testMatrix[i][j];
                elementCount++;
            }
        }

        double averagePacketSize = flowMatrixSum / elementCount;
        double edgeSum = 0;

        for(MultipleWeightsEdge e : graph.edgeSet())
        {
            edgeSum += e.getFlow() / ((e.getCapacity() / averagePacketSize) - e.getFlow());
        }

        double averageDelay = -(1 / flowMatrixSum) * edgeSum;
    }

    void load()
    {
        File file = new File("C:\\Users\\user\\Desktop\\flowMatrix.txt");
        BufferedReader br;

        try
        {
            br = new BufferedReader(new FileReader(file));

            String st;
            try
            {
                while ((st = br.readLine()) != null)
                {
                    System.out.println(st);
                }
            }
            catch(IOException e)
            {

            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File wasnt found " + e);
        }
    }

    public static void main(String[] args)
    {
        int numberOfConnectedGraphs = 0;
        Exercise2 ex;
        for(int i = 0; i < 1000; i++)
        {
            ex = new Exercise2();
            ex.createGraph();

            ex.addEdge("v1", "v2", 6, 0, 0.95);
            ex.addEdge("v2", "v3", 0, 0, 0.95);
            ex.addEdge("v3", "v4", 0, 0, 0.95);
            ex.addEdge("v4", "v5", 0, 0, 0.95);
            ex.addEdge("v5", "v1", 0, 0, 0.95);
            ex.addEdge("v6", "v7", 0, 0, 0.95);
            ex.addEdge("v7", "v8", 0, 0, 0.95);
            ex.addEdge("v8", "v9", 0, 0, 0.95);
            ex.addEdge("v9", "v10", 0, 0, 0.95);
            ex.addEdge("v10", "v6", 0, 0, 0.95);
            ex.addEdge("v1", "v6", 0, 0, 0.95);
            ex.addEdge("v2", "v7", 0, 0, 0.95);
            ex.addEdge("v3", "v8", 0, 0, 0.95);
            ex.addEdge("v4", "v9", 0, 0, 0.95);
            ex.addEdge("v5", "v10", 0, 0, 0.95);
            ex.addEdge("v1", "v3", 0, 0, 0.95);
            ex.addEdge("v1", "v4", 0, 0, 0.95);
            ex.addEdge("v2", "v5", 0, 0, 0.95);
            ex.addEdge("v7", "v10", 0, 0, 0.95);

            ex.setWeights();
            ex.averageDelay();

            ex.MonteCarloSimulation();

            if(ex.connected == true)
            {
                numberOfConnectedGraphs++;
            }
        }
        System.out.println(numberOfConnectedGraphs);
//        Exercise2 ex = new Exercise2();
//        ex.load();
    }
}
