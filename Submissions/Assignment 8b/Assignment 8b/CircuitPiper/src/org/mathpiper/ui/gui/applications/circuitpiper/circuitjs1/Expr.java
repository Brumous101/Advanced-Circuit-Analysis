package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class Expr {
    public static Map<Integer, String> typeSymbolMap = new HashMap();
    
    {
        typeSymbolMap.put(E_ADD, "+");
        typeSymbolMap.put(E_SUB, "-");
        typeSymbolMap.put(E_T, "E_T");
        typeSymbolMap.put(E_VAL, "E_VAL");
        typeSymbolMap.put(E_MUL, "*");
        typeSymbolMap.put(E_DIV, "/");
        typeSymbolMap.put(E_POW, "^");
        typeSymbolMap.put(E_UMINUS, "-");
        typeSymbolMap.put(E_SIN, "SIN");
        typeSymbolMap.put(E_COS, "COS");
        typeSymbolMap.put(E_ABS, "ABS");
        typeSymbolMap.put(E_EXP, "EXP");
        typeSymbolMap.put(E_LOG, "LOG");
        typeSymbolMap.put(E_SQRT, "SQRT");
        typeSymbolMap.put(E_TAN, "TAN");
        typeSymbolMap.put(E_R, "E_R");
        typeSymbolMap.put(E_MAX, "MAX");
        typeSymbolMap.put(E_MIN, "MIN");
        typeSymbolMap.put(E_CLAMP, "E_CLAMP");
        typeSymbolMap.put(E_PWL, "E_PWL");
        typeSymbolMap.put(E_TRIANGLE, "E_TRIANGLE");
        typeSymbolMap.put(E_SAWTOOTH, "E_SAWTOOTH");
        typeSymbolMap.put(E_MOD, "MOD");
        typeSymbolMap.put(E_STEP, "E_STEP");
        typeSymbolMap.put(E_SELECT, "E_SELECT");
        typeSymbolMap.put(E_A, "E_A");
    }

    Expr(Expr e1, Expr e2, int type) {
        children = new Vector<Expr>();
        children.add(e1);
        if (e2 != null) {
            children.add(e2);
        }
        this.type = type;
    }

    Expr(int type, double value) {
        this.type = type;
        this.value = value;
    }

    Expr(int type) {
        this.type = type;
    }

    public double eval(ExprState es) {
        Expr left = null;
        Expr right = null;
        if (children != null && children.size() > 0) {
            left = children.firstElement();
            if (children.size() == 2) {
                right = children.lastElement();
            }
        }
        switch (type) {
            case E_ADD:
                return left.eval(es) + right.eval(es);
            case E_SUB:
                return left.eval(es) - right.eval(es);
            case E_MUL:
                return left.eval(es) * right.eval(es);
            case E_DIV:
                return left.eval(es) / right.eval(es);
            case E_POW:
                return java.lang.Math.pow(left.eval(es), right.eval(es));
            case E_UMINUS:
                return -left.eval(es);
            case E_VAL:
                return value;
            case E_T:
                return es.simulationTime;
            case E_SIN:
                return java.lang.Math.sin(left.eval(es));
            case E_COS:
                return java.lang.Math.cos(left.eval(es));
            case E_ABS:
                return java.lang.Math.abs(left.eval(es));
            case E_EXP:
                return java.lang.Math.exp(left.eval(es));
            case E_LOG:
                return java.lang.Math.log(left.eval(es));
            case E_SQRT:
                return java.lang.Math.sqrt(left.eval(es));
            case E_TAN:
                return java.lang.Math.tan(left.eval(es));
            case E_MIN: {
                int i;
                double x = left.eval(es);
                for (i = 1; i < children.size(); i++) {
                    x = Math.min(x, children.get(i).eval(es));
                }
                return x;
            }
            case E_MAX: {
                int i;
                double x = left.eval(es);
                for (i = 1; i < children.size(); i++) {
                    x = Math.max(x, children.get(i).eval(es));
                }
                return x;
            }
            case E_CLAMP:
                return Math.min(Math.max(left.eval(es), children.get(1).eval(es)), children.get(2).eval(es));
            case E_STEP: {
                double x = left.eval(es);
                if (right == null) {
                    return (x < 0) ? 0 : 1;
                }
                return (x > right.eval(es)) ? 0 : (x < 0) ? 0 : 1;
            }
            case E_SELECT: {
                double x = left.eval(es);
                return children.get(x > 0 ? 2 : 1).eval(es);
            }
            case E_TRIANGLE: {
                double x = posmod(left.eval(es), Math.PI * 2) / Math.PI;
                return (x < 1) ? -1 + x * 2 : 3 - x * 2;
            }
            case E_SAWTOOTH: {
                double x = posmod(left.eval(es), Math.PI * 2) / Math.PI;
                return x - 1;
            }
            case E_MOD:
                return left.eval(es) % right.eval(es);
            case E_PWL:
                return pwl(es, children);
            default:
                if (type >= E_A) {
                    return es.values[type - E_A];
                }
                
                CirSim.console("Unknown operator.\n");
        }
        return 0;
    }

    double pwl(ExprState es, Vector<Expr> args) {
        double x = args.get(0).eval(es);
        double x0 = args.get(1).eval(es);
        double y0 = args.get(2).eval(es);
        if (x < x0) {
            return y0;
        }
        double x1 = args.get(3).eval(es);
        double y1 = args.get(4).eval(es);
        int i = 5;
        while (true) {
            if (x < x1) {
                return y0 + (x - x0) * (y1 - y0) / (x1 - x0);
            }
            if (i + 1 >= args.size()) {
                break;
            }
            x0 = x1;
            y0 = y1;
            x1 = args.get(i).eval(es);
            y1 = args.get(i + 1).eval(es);
            i += 2;
        }
        return y1;
    }

    double posmod(double x, double y) {
        x %= y;
        return (x >= 0) ? x : x + y;
    }

    Vector<Expr> children;
    double value;
    int type;
    public static final int E_ADD = 1;
    public static final int E_SUB = 2;
    public static final int E_T = 3;
    public static final int E_VAL = 6;
    public static final int E_MUL = 7;
    public static final int E_DIV = 8;
    public static final int E_POW = 9;
    public static final int E_UMINUS = 10;
    public static final int E_SIN = 11;
    public static final int E_COS = 12;
    public static final int E_ABS = 13;
    public static final int E_EXP = 14;
    public static final int E_LOG = 15;
    public static final int E_SQRT = 16;
    public static final int E_TAN = 17;
    public static final int E_R = 18;
    public static final int E_MAX = 19;
    public static final int E_MIN = 20;
    public static final int E_CLAMP = 21;
    public static final int E_PWL = 22;
    public static final int E_TRIANGLE = 23;
    public static final int E_SAWTOOTH = 24;
    public static final int E_MOD = 25;
    public static final int E_STEP = 26;
    public static final int E_SELECT = 27;
    public static final int E_A = 28; // Start of node variables a, b, c, etc.
    
    
    public String toString()
    {
        int actualType = type;
        
        if(type > E_A)
        {
            actualType = type - E_A;
        }
        
        String typeSymbol = typeSymbolMap.get(actualType);
        
        return typeSymbol;
    }
}
