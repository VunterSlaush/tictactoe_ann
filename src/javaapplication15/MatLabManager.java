/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication15;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

/**
 *
 * @author Slaush
 */
public class MatLabManager 
{
    private static MatLabManager instance;
    MatlabProxy proxy;
    private MatLabManager() throws MatlabConnectionException
    {
       MatlabProxyFactory factory = new MatlabProxyFactory();
       proxy = factory.getProxy();
    }
    
    public static MatLabManager getInstance() throws MatlabConnectionException
    {
        if(instance == null)
            instance = new MatLabManager();
        return instance;
    }
    
    public void exec(String command) throws MatlabInvocationException
    {
        proxy.eval(command);
    }
    
    public String execForResult(String command) throws MatlabInvocationException
    {
        String result = "";
        Object[] objs = proxy.returningEval(command,1);
        double[] objs2 = (double[])objs[0];
        for (int i = 0; i < objs2.length; i++) {
            result+= objs2[i] + " ";
        }
        return result;
    }
    
    public int execForPosition(Matrix m) throws MatlabInvocationException
    {   
        String command = "round(sim(net,"+m.toString()+"));";
        System.out.println("Enviando:"+command);
        Object[] objs = proxy.returningEval(command,1);
        
        double[] objs2 = (double[])objs[0];
        int value = 0;
        int aux;
        for (int i = objs2.length; i > 0 ; i--) 
        {   
           aux = (int) objs2[objs2.length - i];
           value += aux * Math.pow(2, i-1);
        }
        return value;
    }
    
    public void kill() throws MatlabInvocationException
    {
        proxy.exit();
        proxy.disconnect();
        
    }
}
