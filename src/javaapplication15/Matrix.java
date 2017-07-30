/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication15;

import java.util.Random;

/**
 *
 * @author Slaush
 */
public class Matrix 
{
    int [][] matrix = new int[3][3];
    
    public Matrix()
    {
        boolean generado = false;
        while(!generado)
        {
            generar();
            if(valida())
                generado = true;
        }
    }
    
    public Matrix(String r)
    {
        for (int i = 0; i < 3; i++) 
           {
            for (int j = 0; j < 3; j++) 
            {
                matrix[i][j] = 0;
            }
           }
    }
    
    public boolean resuelve(int x, int y, int j)
    {    
        if(matrix[x][y] != 0 || resuelta())
            return false;
        matrix[x][y] = j;
        boolean resuelta = resuelta(j);
        matrix[x][y] = 0;
        return resuelta;
    }
    
    public boolean juega(int x, int y)
    {
        int p = x * 3 + y;
        return matrix[x][y] == 0 && !resuelta() && p < 9;
    }
    
    public boolean buenaJugada(int x, int y, int p1, int p2, int p3)
    {
        if(matrix[x][y] != 0 || resuelta())
            return false;
        matrix[x][y] = 1;
        boolean resuelta = tapa(p1,p2,p3);
        matrix[x][y] = 0;
        return resuelta;
    }

    @Override
    public String toString()
    {   
        String str  = "[";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) 
            {   
                if(Principal.matlab)
                    str+= matrix[i][j] + ";";
                else
                    str+= matrix[i][j] + ",";
            }
        }
        str+="]";
        return str;
    }

    @Override
    public boolean equals(Object obj) 
    {
       if(obj instanceof Matrix)
       {
           Matrix m = (Matrix)obj;
           for (int i = 0; i < 3; i++) 
           {
            for (int j = 0; j < 3; j++) 
            {
                if(m.matrix[j][i] != this.matrix[j][i])
                    return false;
            }
           }
           return true;
       }
       return false;
    }

    public boolean resuelta(int p) 
    {
        return (matrix[0][0] == p && matrix[0][1] == p &&  matrix[0][2] == p ) ||
                (matrix[1][0] == p && matrix[1][1] == p &&  matrix[1][2] == p ) ||
                (matrix[2][0] == p && matrix[2][1] == p &&  matrix[2][2] == p ) ||

                (matrix[0][0] == p && matrix[1][0] == p &&  matrix[2][0] == p ) ||
                (matrix[0][1] == p && matrix[1][1] == p &&  matrix[2][1] == p ) ||
                (matrix[0][2] == p && matrix[1][2] == p &&  matrix[2][2] == p ) ||

                (matrix[0][0] == p && matrix[1][1] == p &&  matrix[2][2] == p ) ||
                (matrix[0][2] == p && matrix[1][1] == p &&  matrix[2][0] == p );
        
    }
    
    private boolean tapa(int p1, int p2, int p3)
    {
                return (matrix[0][0] == p1 && matrix[0][1] == p2 &&  matrix[0][2] == p3 ) ||
                (matrix[1][0] == p1 && matrix[1][1] == p2 &&  matrix[1][2] == p3 ) ||
                (matrix[2][0] == p1 && matrix[2][1] == p2 &&  matrix[2][2] == p3 ) ||

                (matrix[0][0] == p1 && matrix[1][0] == p2 &&  matrix[2][0] == p3 ) ||
                (matrix[0][1] == p1 && matrix[1][1] == p2 &&  matrix[2][1] == p3 ) ||
                (matrix[0][2] == p1 && matrix[1][2] == p2 &&  matrix[2][2] == p3 ) ||

                (matrix[0][0] == p1 && matrix[1][1] == p2 &&  matrix[2][2] == p3 ) ||
                (matrix[0][2] == p1 && matrix[1][1] == p2 &&  matrix[2][0] == p3 );
    }
    
    public boolean resuelta()
    {
        return resuelta(1) || resuelta(2);
    }

    private void generar() 
    {
        Random r = new Random();
        for (int i = 0; i < 3; i++) 
        {
            for (int j = 0; j < 3; j++) 
            {
                matrix[j][i] =  r.nextInt(3);
            }
        }
    }

    private boolean valida() 
    {   
        int x = 0, y = 0;
        for (int i = 0; i < 3; i++) 
        {
            for (int j = 0; j < 3; j++) 
            {
                if(matrix[j][i] == 1)
                    x++;
                if(matrix[j][i] == 2)
                    y++;
                
            }
        }
        return y - x == 1 || y == x;
            
    }
    
    
    public void jugar(int x, int y, int j)
    {   
        System.out.println("Jugando:"+j+"en "+x+":"+y);
        matrix[x][y] = j;
    }

    boolean vacia() 
    {
        int ceros = 0;
        for (int i = 0; i < 3; i++) 
           {
            for (int j = 0; j < 3; j++) 
            {
                if(matrix[i][j] == 0)
                    ceros++;
            }
           }
        return ceros == 9;
    }

    boolean llena() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                int noceros = 0;
        for (int i = 0; i < 3; i++) 
           {
            for (int j = 0; j < 3; j++) 
            {
                if(matrix[i][j] != 0)
                    noceros++;
            }
           }
        return noceros == 9;
    }
    
    
    
    
}
