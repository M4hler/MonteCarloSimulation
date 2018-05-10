import org.jgrapht.graph.DefaultEdge;

public class MultipleWeightsEdge extends DefaultEdge
{
    private double capacity;
    private double flow;
    private double endurance;

    public MultipleWeightsEdge(double capacity, double flow, double endurance)
    {
        this.capacity = capacity;
        this.flow = flow;
        this.endurance = endurance;
    }

    public void setCapacity(double capacity)
    {
        this.capacity = capacity;
    }

    public void setFlow(double flow)
    {
        this.flow = flow;
    }

    public void setEndurance(double endurance)
    {
        this.endurance = endurance;
    }

    public double getCapacity()
    {
        return capacity;
    }

    public double getFlow()
    {
        return flow;
    }

    public double getEndurance()
    {
        return endurance;
    }
}
