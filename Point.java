import java.util.*;
import java.lang.*;

class Point implements Comparable<Point>
{
    int x, y;
    List<Integer> rets = new ArrayList<Integer>();
    
  //Construtor
    public Point(int x, int y)
    {
	this.x = x;
	this.y = y;
    }

    public void addRet(int n)
    {
	rets.add(n);
    }

    public int size()
    {
	return rets.size();
    }
    public int get(int i)
    {
	return rets.get(i);
    }
    
    //Compara se dois pontos são iguais
    public boolean equals(Point v)
    {
	if(x == v.x && y == v.y)
	    return true;
	return false;
    }

     @Override public int compareTo(Point v) 
    {
	if( (x>v.x) || (x == v.x  && y > v.y))
	    return 1;
	//if((x<v.x) || (x == v.x  && y < v.y))
	//  return -1;
	if((x == v.x) && (y == v.y))
	    return 0;
	return -1;
    }

    public String listTostring() // retorna a lista de retangulos adjacentes
    {
	String str = "[";
	    for(int i=0;i<rets.size();i++)
	    {
		str += rets.get(i);
		if(rets.size()-i != 1)
		    str+= ",";
	    }
	str += "]";
	return str;
    }
    
    //Printa um ponto na forma (x,y)
    public String toString()
    {
	return "(" + x + "," + y + ")";
    }
}
