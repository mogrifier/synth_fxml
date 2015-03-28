/*
 * Math.java
 *
 * Created on January 28, 2006, 6:45 PM
 */

package com.erichizdepski.fmsynth.utils;

import java.lang.reflect.*;
import java.util.*;

/**
 *
 * @author  Owner
 */
public class TrigTester
{
    static Double zero = new Double(0);
    
    //pi radians corresponds to 180 degrees
    static Double deg90 = new Double(Math.PI/2);
    static Double deg45 = new Double(Math.PI/4);
    static Double mdeg90 = new Double(Math.PI/2 * -1);
    static Double mdeg45 = new Double(Math.PI/4 * -1);
    
    static Double[] domain = {Math.PI, mdeg90, mdeg45, zero, deg45, deg90 };
    
    static String[] methods = {"asin","sin","atan","tan","acos","cos"};
    static java.util.List trigSet = Arrays.asList(methods);
    
    /** Creates a new instance of Math */
    public TrigTester()
    {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // TODO code application logic here
        
        
        //check the range and domain of all java trig functions
        
        Method[] functions = Math.class.getMethods();
        Double answer = null;
        
        for (int i = 0; i< functions.length; i++)
        {
            if (trigSet.contains(functions[i].getName()))
            {
                System.out.println("testing function " + functions[i].getName());
                
                //pass in a few values for the function to run
                for (int j = 0; j < domain.length; j++)
                {
                    try
                    {
                        System.out.print(functions[i].getName() + " of " + Math.toDegrees(domain[j]) + " = ");
                        answer = (Double)functions[i].invoke(null, (Object[])new Double[]{domain[j]});
                        System.out.println(answer);
                    }
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                    catch (InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }
                }
                
                System.out.println();
            }
            
            
            
        }
        
        
        
        
    }
    
}
