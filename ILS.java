import java.util.*;
import java.lang.*;

public class ILS{
  
  static HashMap<Integer,List<Integer>> pontos = new HashMap<>();
  static boolean rets[]; //array booleano para dizer se um retângulo está guardado ou não
  static boolean x[]; //array booleano para dizer se um vértice tem um guarda ou não
  static int vertices[]; //array onde é associado cada vértice a um indicador
  static int nrRets;
  static int nrVerts;
  static int nrInst;
  
  public static void main(String[] args){
    
    Scanner read = new Scanner(System.in);

    nrInst = read.nextInt();
    
    for(int l = 0; l < nrInst; l++){
      System.out.println("Instância número " + (l+1) + ":");

      nrRets = read.nextInt();
      nrVerts = 2 * nrRets + 2;
      rets = new boolean[nrRets + 1];
      x = new boolean[nrVerts + 1];
      vertices = new int[nrVerts + 1];
      Point points[] = new Point[nrVerts + 1];
      
      for(int i = 0; i <= nrRets; i++){
        rets[i]=false;
      }
      for(int i = 0; i <= nrVerts; i++){
        x[i]=false;
      }
      
      int counter = 0;
      for(int i = 1; i <= nrRets; i++){
        
        int id = read.nextInt();
        int nVert = read.nextInt();
        
        for(int j = 1; j <= nVert; j++){
          
          int x = read.nextInt();
          int y = read.nextInt();
          Point p = new Point(x,y);
          int vertix = x * nrRets + y;
          if(!pontos.containsKey(vertix)){
            vertices[counter] = vertix;
            points[counter] = p;
            counter++;
            pontos.put(vertix,new ArrayList<Integer>());
            pontos.get(vertix).add(id);
          }
          else
          pontos.get(vertix).add(id);
        }
      }
      
      //Solução inicial, x[v] = 0, para todo o v (nenhum vértice tem guarda);
      int custoInicial = costTotal(x, rets, nrRets, nrVerts);
      
      System.out.println("Custo inicial: " + custoInicial);
      System.out.println("Número de vértices: " + nrVerts);
      
      Random rand = new Random();
      int iterationCounter = 50000;
      int loopCounter = 100;
      int loopFlag; // 0 quando um guarda foi adicionado, 1 quando retirado
      int newCusto;
      int melhorCusto = 99999;
      boolean melhorx[] = new boolean[nrVerts+1];
      
      //iterative local search
      for(int j = 0; j < iterationCounter; j++){
        
        randomInitial(nrVerts, x, rets, pontos, rand, vertices);
        
        //hill climbing
        for(int i = 0; i < loopCounter; i++){
          int randomInt = rand.nextInt(nrVerts+1);
          
          if(x[randomInt] == true){
            removeGuard(x, randomInt, pontos, vertices, rets);
            loopFlag = 1;
          }
          else{
            addGuard(x, randomInt, pontos, vertices, rets);
            loopFlag = 0;
          }
          
          newCusto = costTotal(x, rets, nrRets, nrVerts);
          
          if(newCusto < custoInicial){
            custoInicial = newCusto;
          }
          else{
            if(loopFlag == 0){
              removeGuard(x, randomInt, pontos, vertices, rets);
            }
            else if(loopFlag == 1){
              addGuard(x, randomInt, pontos, vertices, rets);
            }
          }
        }
        
        if(custoInicial < melhorCusto){
          melhorCusto = custoInicial;
          melhorx = x;
        }
        else if(custoInicial == melhorCusto){
          if(getGuardNumber(x, nrVerts) < getGuardNumber(melhorx, nrVerts)){
            melhorCusto = custoInicial;
            melhorx = x;
          }
        }
      }
      
      System.out.println("Melhor Custo: " + melhorCusto);
      for(int i = 0; i < nrVerts; i++){
        if(melhorx[i] == true){
          System.out.println(points[i]);
        }
      }
    }
  }
  
  public static int getGuardNumber(boolean x[], int nrVerts){

    int number = 0;
    for(int i = 0; i < nrVerts; i++){
      if(x[i] == true) number++;
    }
    return number;
  }

  //função para definir um estado inicial random
  public static void randomInitial(int nrVerts, boolean x[],boolean rets[], HashMap<Integer,List<Integer>> pontos, Random rand, int vertices[]){
    
    for(int i = 0; i < nrVerts + 1; i++){
      int randomBinary = rand.nextInt(2);
      if(randomBinary == 0){
        x[i] = false;
        removeGuard(x, i, pontos, vertices, rets);
      }
      else{
        x[i] = true;
        addGuard(x, i, pontos, vertices, rets);
      }
    }
  }


  //função para adicionar um guarda a um vértice e vigiar todos os seus retângulos associados
  public static void addGuard(boolean x[], int id, HashMap<Integer,List<Integer>> pontos, int vertices[], boolean rets[]){
    if(x[id] == false){
      x[id] = true;
      for(int i = 0; i < pontos.get(vertices[id]).size(); i++){
        int index = pontos.get(vertices[id]).get(i);
        rets[index] = true;
      }
    }    
  } 

  //função para remover um guarda a um vértice e parar de vigiar todos os seus retângulos associados
  //temos de ter cuidado para que retângulos vigiados por outros guardas não deixem de ser vigiados, por isso o segundo loop percorre todos os guardas
  public static void removeGuard(boolean x[], int id, HashMap<Integer,List<Integer>> pontos, int vertices[], boolean rets[]){
    if(x[id] == true){
      x[id] = false;
      for(int i = 0; i < pontos.get(vertices[id]).size(); i++){
        int index = pontos.get(vertices[id]).get(i);
        rets[index] = false;
      }
      for(int i = 0; i < nrVerts; i++){
        if(x[i] == true){
          for(int j = 0; j < pontos.get(vertices[id]).size(); j++){
            int index = pontos.get(vertices[id]).get(j);
            rets[index] = true;
          }
        }
      }
    }
  }
  
  //O custo do candidato X pode ser definido como  G + F (2R+3)
  //G é o número de vértices com guarda em X
  //F é o número de retângulos que X não permite guardar
  //R é o número de retângulos da partição
  public static int costTotal(boolean x[], boolean[] rets, int R, int nrVerts){
    
    int G = 0;
    int F = 0;
    
    for(int i = 0; i <= nrVerts; i++){
      if(x[i] == true) G++; 
    }
    for(int i = 0; i < R; i++){
      if(rets[i] == false) F++;
    }

    return G + F * (2*R + 3);
  }
}
