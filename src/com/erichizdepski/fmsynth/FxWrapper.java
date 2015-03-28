/*
 * FxWrapper.java
 *
 * Created on July 30, 2005, 1:15 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.erichizdepski.fmsynth;

/**
 *
 * @author Owner
 */
    public class FxWrapper
    {
        private int name = 0;
        private double amt = 0;
        
        public FxWrapper(int name, double amount)
        {
            this.setName(name);
            this.setAmt(amount);
        }

        public int getName()
        {
            return name;
        }

        public void setName(int name)
        {
            this.name = name;
            System.out.println("fx type=" + name);
        }

        public double getAmt()
        {
            return amt;
        }

        public void setAmt(double amt)
        {
            this.amt = amt;
            System.out.println("fx amount=" + amt);
        }
    }
