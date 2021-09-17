import java.io.Serializable;

public class commande implements Serializable{
    String action = "";
    String parameter = "";
    String option = "";
    
    commande(String a)
    {
        this.action = a;
    }

    commande(String a, String p)
    {
        this.action = a;
        this.parameter = p;
    }

    commande(String a, String p, String o)
    {
        this.action = a;
        this.parameter = p;
        this.option = o;
    }

    @Override
    public String toString()
    {
        return this.action + " " + this.parameter + " " + this.option;
    }
}
