
/**
 * Simple test of ability to print out Chinese characters in Terminal
 * 
 * @author Ben Armstrong
 * @version October 18, 2011
 */

import java.io.*;
import java.util.Scanner;
public class Lab51
{
    public static void main(String[]args)
        throws java.io.UnsupportedEncodingException
        {
            PrintWriter out = new PrintWriter(new java.io.OutputStreamWriter(System.out,"utf8"));
            
            String output = "Type a number and it should be reprinted as a Chinese character: ";
            
            int number = Input(output);
            
            String chineseNumber = chineseCharacter(number);
            
            out.println(chineseNumber);
            out.flush();
            
        }
        
    public static int Input(String output)
        {
            Scanner scan = new Scanner(System.in);
            
            System.out.print(output);
            int userInput = scan.nextInt();
            
            
            return userInput;
        }
        
    public static String chineseCharacter(int number)
        {
            String chineseNumber = "There is no Chinese character for " + number;
            
            switch (number)
                {
                    case 0: chineseNumber = "\uC1E3"; break;
                    case 1: chineseNumber = "\u4E00"; break;
                    case 2: chineseNumber = "\u4E8C"; break;
                    case 3: chineseNumber = "\u4E09"; break;
                    case 4: chineseNumber = "\u56DB"; break;
                    case 5: chineseNumber = "\u4E94"; break;
                    case 6: chineseNumber = "\u516D"; break;
                    case 7: chineseNumber = "\u4E03"; break;
                    case 8: chineseNumber = "\u516B"; break;
                    case 9: chineseNumber = "\u4E5D"; break;
                    case 10: chineseNumber = "\u5341"; break;
                    case 100: chineseNumber = "\u767E"; break;
                    case 1000: chineseNumber = "\u5343"; break;
                    case 10000: chineseNumber = "\u4E07"; break;
                    case 100000000: chineseNumber = "\u4EBF"; break;
                }
            
            return chineseNumber;
        }



}