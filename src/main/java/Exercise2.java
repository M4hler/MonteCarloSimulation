import org.jgrapht.GraphPath;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleGraph;

import java.io.*;
import java.util.Random;

public class Exercise2
{
    private SimpleGraph<String, MultipleWeightsEdge> graph;
    private double [][] flowMatrix;
    private double [][] edgeReliability;
    private double maxDelay;
    private boolean connected;

    public Exercise2()
    {
        connected = true;
    }

    public void createGraph()
    {
        graph = new SimpleGraph<>(MultipleWeightsEdge.class);

        for (int i = 1; i <= 10; i++)
        {
            graph.addVertex(String.valueOf(i));
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
                GraphPath path = dijkstraPath.getPath(String.valueOf(firstVertex), String.valueOf(secondVertex));

                for(int k = 0; k < path.getLength(); k++)
                {
                    MultipleWeightsEdge e = (MultipleWeightsEdge)path.getEdgeList().get(k);
                    e.setFlow(e.getFlow() + flowMatrix[i][j]);
                    e.setCapacity(25);
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
                GraphPath path = dijkstraPath.getPath(String.valueOf(firstVertex), String.valueOf(secondVertex));

                if(path == null)
                {
                    return false;
                }

                for(int k = 0; k < path.getLength(); k++)
                {
                    MultipleWeightsEdge e = (MultipleWeightsEdge)path.getEdgeList().get(k);
                    e.setFlow(e.getFlow() + flowMatrix[i][j]);
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

        for(int i = 0; i < 5; i++)
        {
/*            int firstVertexNumber = generator.nextInt(10) + 1;
            int secondVertexNumber = generator.nextInt(10) + 1;

            while (secondVertexNumber == firstVertexNumber) {
                secondVertexNumber = generator.nextInt(10) + 1;
            }

            DijkstraShortestPath dijkstraPath = new DijkstraShortestPath(graph);
            GraphPath path = dijkstraPath.getPath(String.valueOf(firstVertexNumber), String.valueOf(secondVertexNumber));

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
            }*/

            for(MultipleWeightsEdge e : graph.edgeSet())
            {
                double edgeDestroyed = generator.nextDouble();
                if(edgeDestroyed > e.getEndurance())
                {
                    graph.removeEdge(e);
                    break;
                }
            }

            boolean b = adjustWeights();
            if(b == false)
            {
                connected = false;
                return;
            }

            ConnectivityInspector inspector = new ConnectivityInspector(graph);
            if (inspector.isGraphConnected() == false)
            {
                connected = false;
                return;
            }
        }
    }

    public double averageDelay()
    {
        double flowMatrixSum = 0;
        int elementCount = 0;

        for(int i = 0; i < 10; i++)
        {
            for(int j = i + 1; j < 10; j++)
            {
                flowMatrixSum += flowMatrix[i][j];
                elementCount++;
            }
        }

        double averagePacketSize = flowMatrixSum / elementCount;
        double edgeSum = 0;

        for(MultipleWeightsEdge e : graph.edgeSet())
        {
            edgeSum += e.getFlow() / ((e.getCapacity() / averagePacketSize) - e.getFlow());
        }

        double averageDelay = Math.abs((1 / flowMatrixSum) * edgeSum);
        return averageDelay;
    }

    void load()
    {
        File file = new File("input.txt");
        BufferedReader br;

        try
        {
            br = new BufferedReader(new FileReader(file));

            String s;
            try
            {
                s = br.readLine();

                String[] regex = s.split(" ");
                String[][] graphMatrix = new String[regex.length][regex.length];
                int l = regex.length;

                for(int i = 0; i < l; i++)
                {
                    for(int j = 0; j < l; j++)
                    {
                        graphMatrix[i][j] = regex[j];
                    }
                    s = br.readLine();
                    regex = s.split(" ");
                }

                for(int i = 0; i < l; i++)
                {
                    for(int j = i + 1; j < l; j++)
                    {
                        if(graphMatrix[i][j].equals("1"))
                        {
                            addEdge(String.valueOf(i + 1), String.valueOf(j + 1), 0, 0, 0.99);
                        }
                    }
                }

                s = br.readLine();
                regex = s.split(" ");
                flowMatrix = new double[l][l];

                for(int i = 0; i < l; i++)
                {
                    for(int j = 0; j < l; j++)
                    {
                        flowMatrix[i][j] = Double.valueOf(regex[j]);
                    }

                    s = br.readLine();
                    regex = s.split(" ");
                }

                s = br.readLine();
                regex = s.split(" ");
                edgeReliability = new double[l][l];

                for(int i = 0; i < l; i++)
                {
                    for(int j = 0; j < l; j++)
                    {
                        edgeReliability[i][j] = Double.valueOf(regex[j]);
                    }

                    s = br.readLine();
                    regex = s.split(" ");
                }

                for(int i = 0; i < l; i++)
                {
                    for(int j = i + 1; j < l; j++)
                    {
                        if(edgeReliability[i][j] != 0)
                        {
                            graph.getEdge(String.valueOf(i + 1), String.valueOf(j + 1)).setEndurance(edgeReliability[i][j]);
                        }
                    }
                }

                s = br.readLine();
                maxDelay = Double.valueOf(s);
            }
            catch(IOException e)
            {
                System.out.println("Can`t read line " + e);
            }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File wasn't found " + e);
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
            ex.load();

            ex.setWeights();
            double delay = ex.averageDelay();

            ex.MonteCarloSimulation();

            if(ex.connected == true && delay < ex.maxDelay)
            {
                numberOfConnectedGraphs++;
            }
        }
        System.out.println(numberOfConnectedGraphs);
    }
}
