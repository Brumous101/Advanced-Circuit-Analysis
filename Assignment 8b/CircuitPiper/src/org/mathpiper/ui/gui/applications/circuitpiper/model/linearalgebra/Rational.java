package org.mathpiper.ui.gui.applications.circuitpiper.model.linearalgebra;

import java.math.BigInteger;
/*AUTHORS:

 - Kevin Stueve (2009-12-20): initial published version
 #*****************************************************************************
 #       Copyright (C) 2009 Kevin Stueve kstueve@uw.edu
 #
 #  Distributed under the terms of the GNU General Public License (GPL)
 #                  http://www.gnu.org/licenses/
 #*****************************************************************************
 */

public class Rational {

    public BigInteger num;
    public BigInteger denom;
    //public boolean reduced = false;
    public static final Rational ZERO = new Rational(0, 1);
    public static final Rational ONE = new Rational(1, 1);
    public static final Rational NEGATIVE_ONE = new Rational(-1, 1);

    public Rational(double x) {
        //System.out.println("double="+x);
        //next four lines from http://www.daniweb.com/forums/thread21094.html
        long bits = Double.doubleToLongBits(x);
        //System.out.println("bits "+bits);
        boolean negative = (bits & 0x8000000000000000L) != 0;
        int exponent = (int) ((bits & 0x7ff0000000000000L) >> 52);

        long mantissa = bits & 0x000fffffffffffffL;
        if (exponent != 0) {
            exponent -= 1023;
            long mask = 0x0010000000000000L;
            mantissa += mask;
        }
        //System.out.println("exponent "+exponent);
        //System.out.println("negative: " + negative);
        //System.out.println("mantissa:"+mantissa);
        //System.out.println("exponent: " + Double.longBitsToDouble(exponent));
        //System.out.println("mantissa: " + Double.longBitsToDouble(mantissa));
        if (mantissa == 0) {
            //System.out.println("mantissa is 0");
            this.num = BigInteger.ZERO;
            this.denom = BigInteger.ONE;
        } else if (exponent >= 0) {
            this.num = BigInteger.valueOf(mantissa).multiply(BigInteger.valueOf(2).pow(exponent));
            //System.out.println(num);
            this.denom = BigInteger.valueOf(2).pow(52);
        } else {
            this.num = BigInteger.valueOf(mantissa);
            this.denom = BigInteger.valueOf(2).pow(-exponent + 52);
        }
        if (negative) {
            this.num = this.num.negate();
        }
        //ensureDenomPositive(); not needed
        reduce();
        //System.out.println("in rational form "+num+"/"+denom);
    }

    public Rational(Integer num, Integer denom) {
        this.num = BigInteger.valueOf(num);
        this.denom = BigInteger.valueOf(denom);
        //assume it is already reduced.  if it isn't, it'll be soon enough
    }

    private void reduce() {
        //System.out.println(num);
        //System.out.println(denom);
        BigInteger g = num.gcd(denom);
        num = num.divide(g);
        denom = denom.divide(g);
    }

    private void ensureDenomPositive() {
        //to ensure invariant that denominator is positive
        if (denom.compareTo(BigInteger.ZERO) < 0) {
            denom = denom.negate();
            num = num.negate();
        }
    }

    public Rational(BigInteger numerator, BigInteger denominator) {
        // deal with x / 0
        if (denominator.equals(BigInteger.ZERO)) {
            throw new RuntimeException("Denominator is zero");
        }
        if (numerator.equals(BigInteger.ZERO)) {
            num = BigInteger.ZERO;
            denom = BigInteger.ONE;
        } else {
            num = numerator;
            denom = denominator;
            reduce();

            ensureDenomPositive();
        }
    }

    private Rational() {

    }

    public Rational reciprocal() {
        Rational result = new Rational();
        result.denom = this.num;
        result.num = this.denom;
        result.ensureDenomPositive();
        return result;
    }

    public Rational divide(Rational b) {
        Rational a = this;
        return a.multiply(b.reciprocal());
    }

    public Rational multiply(Rational b) {
        Rational a = this;
        return new Rational(a.num.multiply(b.num), a.denom.multiply(b.denom));
    }

    public Rational add(Rational b) {
        Rational a = this;
        BigInteger numerator = a.num.multiply(b.denom).add(b.num.multiply(a.denom));
        BigInteger denominator = a.denom.multiply(b.denom);
        return new Rational(numerator, denominator);
    }

    public Rational subtract(Rational b) {
        Rational a = this;
        return a.add(b.negate());
    }
//return -a

    public Rational negate() {
        return new Rational(num.negate(), denom);
    }

    public double doubleValue() {
        return this.num.doubleValue() / this.denom.doubleValue();
    }
//is this Rational object equal to y?

    public boolean equals(Object y) {
        if (y == this) {
            return true;
        }
        if (y == null) {
            return false;
        }
        if (y.getClass() != this.getClass()) {
            return false;
        }
        Rational b = (Rational) y;
        return b.num.equals(this.num) && b.denom.equals(this.denom);//compareTo(b) == 0;
    }
//return string representation of (this)

    public String toString() {
        if (denom.equals(BigInteger.ONE)) {
            return num + "";
        } else {
            return num + "/" + denom;
        }
    }
}
