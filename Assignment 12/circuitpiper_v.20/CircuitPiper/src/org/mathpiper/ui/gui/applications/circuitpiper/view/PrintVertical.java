
package org.mathpiper.ui.gui.applications.circuitpiper.view;

// Original code obtained from https://codereview.stackexchange.com/questions/101540/print-array-of-sentences-vertically;

import java.util.Formatter;


public class PrintVertical
{

    public static void main(String[] args) {
        String[] target = {
            "How To Format",
            "put returns between paragraphs",
            "for linebreak add 2 spaces at end"
        };

        System.out.println(formatWordsVertically(target, 2, 5));
    }

    public static int wordCount(String input) {
        return input.length();
    }

    public static int longestWordCount(String[] input) {
        int result = 0;

        for (String s : input) {
            if (result < wordCount(s)) {
                result = wordCount(s);
            }
        }

        return result;
    }

    public static String formatWordsVertically(String[] input, int spaceColumn1, int spaceColumn2) {
        StringBuilder sb = new StringBuilder();
        
        Formatter fm = new Formatter(sb);
        
        int longestWordCount = longestWordCount(input);
        int spacesBetweenColumns = 2; // use needed int, or pass as argument
        String formatSpecifier = "%-" + (1 + spacesBetweenColumns) + "s";
        String[][] words = convertToWordArray(input);

        for (int i = 0; i < longestWordCount; i++) {
            sb.append("   ");
            for (int j = 0; j < words.length; j++) {
                
                if (j == spaceColumn1 || j == spaceColumn2)
                {
                    sb.append(" ");
                }
                
                if (i < words[j].length) {
                    fm.format(formatSpecifier, words[j][i]);
                } else {
                    fm.format(formatSpecifier, "");
                }
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }

    public static String[][] convertToWordArray(String[] input) {
        String[][] result = new String[input.length][];

        for (int i = 0; i < result.length; i++) {
            result[i] = input[i].trim().split("");
        }

        return result;
    }
}