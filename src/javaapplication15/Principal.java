package javaapplication15;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;



public class Principal 
{
    static int entrenamientos = 0;

    public static void main (String [] args) throws InterruptedException, FileNotFoundException, UnsupportedEncodingException, MatlabConnectionException, MatlabInvocationException
    {
       
       /*PrintWriter writer = new PrintWriter("train_all.m", "UTF-8");
       long time = System.currentTimeMillis();
       llenar();
        for (int i = 0; i < matrices.size(); i++) {
            writer.println("[net, tr] = train(net,"+matrices.get(i)+", "+ toBinaryStringArray(soluciones.get(i))+")");
        }
       System.out.println("Resueltos:"+matrices.size());
       System.out.println("Time:"+(System.currentTimeMillis() - time));
       writer.close();*/
        MatLabManager matlab = MatLabManager.getInstance();
        matlab.exec("load net");
        matlab.exec("clear train");
        for (int i = 0; i < 8000; i++) {
            jugarContraMaquina(matlab);
        }
        System.out.println("Entrenamientos:"+entrenamientos);
        matlab.exec("save net");
        Thread.sleep(1000);
        matlab.kill();
      
    }   
    
   
    private static int[] generatePos(int j) 
    {   
        int [] pos = new int[2];
        for (int i = 0; i < 3; i++) 
        {   
            for (int k = 0; k < 3; k++) 
            {   
                if(j == 0)
                {
                    pos[0] = i;
                    pos[1] = k;
                    
                    return pos;
                }
                j--;
            }
            
        }
        return pos;
    }
    
    
    private static void llenar()
    {
      llenar((matrix, x, y) -> {
          return matrix.resuelve(x, y, 2);
      });  
        
      llenar((Matrix m, int x, int y) -> m.buenaJugada(x, y,1,1,2));
      
      llenar((Matrix m, int x, int y) -> m.buenaJugada(x, y,2,1,1));
      
      llenar(Matrix::juega);
    }
    
    private static void llenar(ByCondition condition)
    {/*
        Matrix m;
        int [] pos;
        for (int i = 0; i <500000; i++) 
        {
            m = new Matrix();
            if(!matrices.contains(m))
            {
                for(int j = 0; j<9; j++)
                {
                   pos = generatePos(j);
                   if(condition.condition(m,pos[0],pos[1]))
                   {    
                       matrices.add(m);
                       soluciones.add(j);
                       break;
                   }
                  
                }
            }      
        }*/
    }
    
    
    private static Runnable fillRunnable = new Runnable() {

        @Override
        public void run() 
        {
            llenar();
        }
    };

    private static String toBinaryStringArray(int j) 
    {   
       String str =  Integer.toBinaryString(j);
       int nums = str.length();
        for (int i = 0; i < 4 - nums; i++) {
            str = "0" + str;
        }
                    
       return "[ "+str.replaceAll(".", "$0; ")+"]";
        
    }

    private static void jugarContraMaquina(MatLabManager matlab) throws MatlabInvocationException, InterruptedException 
    {
      Random r = new Random();
      boolean maquinaFirst = r.nextBoolean();
      boolean ganador = false;
      Matrix m = new Matrix("");
      while(!ganador)
      {
        if(m.resuelta() || m.llena())
        {
            ganador = true;
            System.out.println("Alguien Gano?:"+maquinaFirst);
            break;
        }
        Thread.sleep(100);

        if(maquinaFirst)
        {
            jugarMaquina(m,r);
            if(m.resuelta() || m.llena())
            {
                ganador = true;
                System.out.println("Alguien Gano?:"+maquinaFirst);
                break;
            }
            jugarNeurona(m,matlab);
        }
        else
        {
            jugarNeurona(m,matlab);
            if(m.resuelta() || m.llena())
            {
                ganador = true;
                System.out.println("Alguien Gano?:"+maquinaFirst);
                break;
            }
            jugarMaquina(m,r);
        }

      }
    }

    private static void jugarNeurona(Matrix m, MatLabManager matlab) throws MatlabInvocationException 
    {
       String s = "round(sim(net,"+m.toString()+"));";
       int position = matlab.execForPosition(s);
       System.out.println("Posicion a jugar Neurona:"+position);
       int []pos = generatePos(position);
       int []mejor = buscarSiguienteTapeGanada(m);
       if(!m.juega(pos[0], pos[1]) || position >= 9 || 
               (mejor != null && pos[0] != mejor[0] && pos[1] != mejor[1]))
          corregir(m,matlab); 
       else
          m.jugar(pos[0], pos[1], 1);
    }

    private static void jugarMaquina(Matrix m, Random r) 
    {
        if(m.vacia())
            jugarEsquinaOCentro(m,r);
        else
        {
            int [] pos = buscarMejorJuego(m);
            m.jugar(pos[0], pos[1], 2);
        }
        
    }

    private static void corregir(Matrix m, MatLabManager matlab) throws MatlabInvocationException 
    {
        int [] pos = buscarMejorJuego(m);
        int p = pos[0] * 3 + pos[1];
        String s = "[net, tr] = train(net,"+m.toString()+","+toBinaryStringArray(p)+");";
        System.out.println("Enviando:"+s);
        matlab.exec(s);
        m.jugar(pos[0],pos[1],1);
        entrenamientos++;
    }

    private static void jugarEsquinaOCentro(Matrix m, Random r) 
    {
        for (int i = 0; i <= 8; i+=2) {
            if(r.nextBoolean())
            {
                int []pos = generatePos(i);
                m.jugar(pos[0], pos[1], 2);
                return;
            }    
        }
        m.jugar(2, 2, 2);
    }
    
    
    private static int[] buscarSiguienteTapeGanada(Matrix m)
    {
        int[] tg1 = ganaSiguienteMov(m,1);
        if(tg1 != null)
            return tg1;
        int[] tg2 = ganaSiguienteMov(m,2);
        if(tg2 != null)
            return tg2;
        return null;
    }
    private static int[] buscarMejorJuego(Matrix m) 
    {
        int[] tg1 = buscarSiguienteTapeGanada(m);
        if(tg1 != null)
            return tg1;
        
         int[][] preferredMoves = {
         {1, 1}, {0, 0}, {0, 2}, {2, 0}, {2, 2},
         {0, 1}, {1, 0}, {1, 2}, {2, 1}};
        for (int[] move : preferredMoves) 
        {
            if (m.juega(move[0],move[1])) 
            {
              return move;
            }
        }
        return null;
    }

    private static int[] ganaSiguienteMov(Matrix m, int j) {
        for (int i = 0; i < 9; i++) 
        {
            int [] pos = generatePos(i);
            if(m.resuelve(pos[0], pos[1],j))
                return pos;
        }
        return null;
    }
    
    interface ByCondition 
    {
        boolean condition(Matrix m,int x, int y);
    }
}