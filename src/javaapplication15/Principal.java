package javaapplication15;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;



public class Principal 
{   
    static boolean matlab = true; // al poner esto en true entrena la red
    static int entrenamientos = 0; // en matlab y no la de nodejs
    static ArrayList<Matrix> matrices;
    static ArrayList<Integer> soluciones;
    static int empates = 0;
    static int ganadas = 0;
    static int perdidas = 0;
    public static void main (String [] args) throws InterruptedException, FileNotFoundException, UnsupportedEncodingException, MatlabConnectionException, MatlabInvocationException, JSONException, IOException
    {
        if(matlab)
        {
            MatLabManager.getInstance().exec("load net");
            MatLabManager.getInstance().exec("clear train");
        }
            
        
        for (int i = 0; i < 100; i++) {
            jugarContraMaquina();
            
        }
        System.out.println("Entrenamientos:"+entrenamientos);
        System.out.println("ganadas:"+ganadas); 
        System.out.println("perdidas:"+perdidas); 
        System.out.println("empates:"+empates);
        if(matlab)
        {
            MatLabManager.getInstance().exec("save net");
            MatLabManager.getInstance().kill();
        }
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
    {
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
       if(matlab)
           return "[ "+str.replaceAll(".", "$0; ")+"]";
       else
            return "[ "+str.replaceAll(".", "$0, ")+"]";
        
    }

    private static void jugarContraMaquina() throws MatlabInvocationException, InterruptedException, JSONException, IOException, MatlabConnectionException 
    {
      Random r = new Random();
      boolean maquinaFirst = r.nextBoolean();
      boolean ganador = false;
      Matrix m = new Matrix("");
      while(!ganador)
      {
        if(juegoFinalizado(m))
                break;

        if(maquinaFirst)
        {
            jugarMaquina(m,r);
            if(juegoFinalizado(m))
                break;
            jugarNeurona(m);
        }
        else
        {
            jugarNeurona(m);
            if(juegoFinalizado(m))
                break;
           
            jugarMaquina(m,r);
        }
        Thread.sleep(100);
      }
    }
    
    public static boolean juegoFinalizado(Matrix m)
    {
        if(m.resuelta() || m.llena())
        {
          if(m.resuelta(1))
              ganadas++;
          else if(m.resuelta(2))
              perdidas++;
          else
              empates++;
          return true;
        }
        return false;
    }

    private static void jugarNeurona(Matrix m) throws MatlabInvocationException, JSONException, IOException, MatlabConnectionException 
    {
       int position;
       if(matlab)
        position = MatLabManager.getInstance().execForPosition(m);
       else
           position = execForPosition(m);
       
       System.out.println("Posicion a jugar Neurona:"+position);
       int []pos = generatePos(position);
       int []mejor = buscarSiguienteTapeGanada(m);
       if(!m.juega(pos[0], pos[1]) || position >= 9 || 
               (mejor != null && pos[0] != mejor[0] && pos[1] != mejor[1]))
          corregir(m); 
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

    private static void corregir(Matrix m) throws MatlabInvocationException, JSONException, IOException, MatlabConnectionException 
    {
        int [] pos = buscarMejorJuego(m);
        int p = pos[0] * 3 + pos[1];
        if(matlab)
            MatLabManager.getInstance().exec("[net tr] = train(net,"+m.toString()+","+toBinaryStringArray(p)+");");
        else
            entrenar(m,p);
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

    private static int execForPosition(Matrix m) throws JSONException, IOException 
    {
        String req = "{'game':"+m.toString()+"}";
        JSONObject o = new JSONObject(req);
        System.out.println("Enviando:"+o);
        o = executePost("http://localhost:5000/play",o);
        System.out.println("Recibiendo:"+o);
        return o.getInt("result");
    }

    private static void entrenar(Matrix m, int p) throws IOException, JSONException 
    {
        String s = "{ 'game':"+m.toString()+", 'target':"+toBinaryStringArray(p)+", 'key':'MOTA RULES'}";
        System.out.println("Enviando:"+s);
        JSONObject obj = new JSONObject(s);
        obj = executePost("http://localhost:5000/train",obj);
        System.out.println("Recibiendo:"+obj.toString());
    }
    
    interface ByCondition 
    {
        boolean condition(Matrix m,int x, int y);
    }
    
    
    public static JSONObject executePost(String targetURL, JSONObject o) throws IOException, JSONException 
    {
       
        HttpClient   httpClient    = HttpClientBuilder.create().build();
        HttpPost     post          = new HttpPost(targetURL);
        StringEntity postingString = new StringEntity(o.toString());
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse  response = httpClient.execute(post);
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, "UTF-8");
        return new JSONObject(responseString);
    }
}