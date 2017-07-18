package javaapplication15;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;



public class Principal 
{
    static List<Matrix> matrices = new ArrayList<>();
    static List<Integer> soluciones = new ArrayList<>();
    static PrintWriter writer;

    public static void main (String [] args) throws InterruptedException, FileNotFoundException, UnsupportedEncodingException
    {
       
       PrintWriter writer = new PrintWriter("train_2.m", "UTF-8");
       long time = System.currentTimeMillis();
       llenar();
        for (int i = 0; i < matrices.size(); i++) {
            writer.println("[net, tr] = train(net,"+matrices.get(i)+", "+ toBinaryStringArray(soluciones.get(i))+")");
        }
       System.out.println("Resueltos:"+matrices.size());
       System.out.println("Time:"+(System.currentTimeMillis() - time));
       writer.close();
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
        Matrix m;
        int [] pos;
        for (int i = 0; i <80000; i++) 
        {
            m = new Matrix();
            if(!matrices.contains(m))
            {
                for(int j = 0; j<9; j++)
                {
                   pos = generatePos(j);
                   if(m.juega(pos[0], pos[1]))
                   {    
                       matrices.add(m);
                       soluciones.add(j);
                       break;
                   }
                   
                }
            }
               
        }
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
}