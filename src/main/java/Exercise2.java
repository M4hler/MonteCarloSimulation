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
    private double [][] edgeCapacity;
    private double averagePacketSize;
    private double maxDelay;
    private boolean connected;
    private boolean overflow;
    private boolean delayTooLarge;

    public Exercise2()
    {
        connected = true;
        overflow = false;
        delayTooLarge = false;
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

    public void EdgeBurst()
    {
        Random generator = new Random();

        for(int i = 0; i < 5; i++)
        {
            for(MultipleWeightsEdge e : graph.edgeSet())
            {
                double edgeDestroyed = generator.nextDouble();
                if(edgeDestroyed > e.getEndurance())
                {
                    graph.removeEdge(e);
                    break;
                }
            }

            ConnectivityInspector inspector = new ConnectivityInspector(graph);
            if (inspector.isGraphConnected() == false)
            {
                connected = false;
                return;
            }

            boolean adjustment = adjustWeights();
            if(adjustment == false)
            {
                overflow = true;
                return;
            }
        }
    }

    public void avgPacketSize()
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

        averagePacketSize = flowMatrixSum / elementCount;
    }

    public void averageDelay()
    {
        double edgeSum = 0;
        double flowMatrixSum = 0;

        for(int i = 0; i < 10; i++)
        {
            for(int j = i + 1; j < 10; j++)
            {
                flowMatrixSum += flowMatrix[i][j];
            }
        }

        for(MultipleWeightsEdge e : graph.edgeSet())
        {
            edgeSum += e.getFlow() / ((e.getCapacity() / averagePacketSize) - e.getFlow());
        }

        double averageDelay = Math.abs((1 / flowMatrixSum) * edgeSum);

        if(averageDelay >= maxDelay)
        {
            delayTooLarge = true;
        }
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
                regex = s.split(" ");
                edgeCapacity = new double[l][l];
                avgPacketSize();

                for(int i = 0; i < l; i++)
                {
                    for(int j = 0; j < l; j++)
                    {
                        edgeCapacity[i][j] = Double.valueOf(regex[j]);
                    }

                    s = br.readLine();
                    regex = s.split(" ");
                }

                for(int i = 0; i < l; i++)
                {
                    for(int j = i + 1; j < l; j++)
                    {
                        if(edgeCapacity[i][j] != 0)
                        {
                            graph.getEdge(String.valueOf(i + 1), String.valueOf(j + 1)).setCapacity(edgeCapacity[i][j]);
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
        double numberOfConnectedGraphs = 0;
        double numberOfCGandProperFlow = 0;
        double success = 0;
        Exercise2 ex;

        int n = 1000;
        for(int i = 0; i < n; i++)
        {
            ex = new Exercise2();
            ex.createGraph();
            ex.load();

            ex.setWeights();
            ex.EdgeBurst();

            if(ex.connected == true /*&& delay < ex.maxDelay */)
            {
                numberOfConnectedGraphs++; //only graphs that are connected

                if(ex.overflow == false)
                {
                    numberOfCGandProperFlow++; //connected graphs with proper flow after edge burst

                    ex.averageDelay();
                    if(ex.delayTooLarge == false)
                    {
                        success++; //connected graphs with proper flow and average delay fewer than max delay
                    }
                }
            }
        }
        System.out.println(numberOfConnectedGraphs / n);
        System.out.println(numberOfCGandProperFlow / n);
        System.out.println(success / n);
    }
}
