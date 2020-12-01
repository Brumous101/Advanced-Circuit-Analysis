package org.mathpiper.ui.gui.applications.circuitpiper.model.numericalanalysis;

/*
    @ Author: Nasir Islam Sujan
    @ GitHub: https://github.com/78526Nasir
*/


/*
    * This solution was written for
    * the following equation
    * f(x) = x^3 - 0.165x^2 + 3.993 * 10^-4
    You need to iterate till the relative approximate error of 0.05 or less.
    sample input:
    x0=0.05
    samout output:
    Enter value for x0 : 0.05
    Iteration : 1
    Root:0.06242222222222221
    Error:19.90032039871839
    Iteration : 2
    Root:0.062377576543465846
    Error:0.07157328198740867
    Iteration : 3
    Root:0.06237758151374945
    Error:7.96806076917568E-6
        
*/


import java.util.Scanner;



public class NewtonRaphson {
    public static void main(String[] args) {
        double x0,root,givenError,error;
        boolean flag=true;
        int i=1;
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter value for x0 : ");
        x0 = sc.nextDouble();

        givenError=x0;

        while(flag){
            root = x0 - (F(x0)/Fprime_X(x0));
            error = Math.abs((root-x0)/root)*100;

            System.out.println("Iteration : "+ i++);
            System.out.println("Root:"+root);
            System.out.println("Error:"+error);
            System.out.println();
            x0 = root;

            if(givenError>error)
            {
                    flag=false;
            }
        }
    }
            
    public static double F(double x){
        return Math.pow(x,3)-(0.165*Math.pow(x,2))+(3.993*Math.pow(10,-4));
    }
    
                
    public static double Fprime_X(double x){
        return 3*Math.pow(x,2)-0.33*x;
    }
    
    
}

