package findmaxinarray;

/**
 * @author: Edward Cooney 
 * File: Prog1.java 
 * Section: CSC331 
 * Date: 03/14/2014
 * Notes: Use individual threads to perform three step operation to find the
 * maximum value in an array. 
 * 1. Use n threads to initial a flag array to all 1's 
 * 2. Use n * (n-1) / 2 threads to compare two elements of the integer array
 * placing a 0 at the index of the corresponding flag array that holds the
 * lesser integer value. 
 * 3. Use n threads to determine which index of the flag array holds the 
 * remaining 1 value. Print the index value and the value that corresponds to 
 * that index in the integer array.
 * 
 * Sample Input: FindMaxInArray.java 9 3 1 7 4 11 5 9 20 13
 * Sample Output: Maximum = 20
 *                Location = 7
 */
import java.util.Arrays;

public class FindMaxInArray {

    /**
     * Parse the entire args array from the command line to ensure all values
     * are integers. We only return the values needed for the array
     *
     * @param args Command Line args from index 0 to n
     * @return Integer array from index 1 to n
     */
    public static int[] parseArgs(String[] args) {
        int[] inputValues = new int[args.length - 1];
        try {
            Integer.parseInt(args[0]);
            for (int i = 0; i < (inputValues.length); i++) {
                inputValues[i] = Integer.parseInt(args[i + 1]);
            }
        } catch (NumberFormatException nfe) {
            System.out.println(nfe.getMessage());
            return null;
        }
        return inputValues;
    }

    /**
     * Format arrays for output to console
     *
     * @param array The array to be formatted
     * @return String of array elements comma seperated
     */
    static String arrayToString(int[] array) {
        return Arrays.toString(array).replaceAll("[\\[\\],]", "");
    }

    /**
     * Print out message to user detailing normal operation
     */
    static void operationMessage() {
        System.out.println("This program uses command line input in the form:\n"
                + "   > Prog1.java n x0 x1 ... xn\n"
                + "Where n is the number of elements and x0 to xn are unique integers "
                + "that make up\nthe arrayfrom which the user wants to find maximum.  "
                + "Comparison array size\nshould be between 3 and 10.\n");
    }

    /**
     * Initialize each element of the flag array to a 1
     *
     * @param flagArray The array to be initialized to with 1's
     * @return An integer array with each element initialized to 1
     * @throws InterruptedException
     */
    static int[] initializeFlagArray(final int[] flagArray) throws InterruptedException {
        Thread[] t = new Thread[flagArray.length];

        for (int i = 0; i < t.length; i++) {
            final int index = i;
            t[i] = new Thread(new Runnable() {

                @Override
                public void run() {
                    System.out.println(Thread.currentThread());
                    flagArray[index] = 1;
                }
            }, "InitializeFlagArray Thread: " + i);
            t[i].start();
        }

        /* Join threads to ensure orderly execution*/
        for (Thread thd : t) {
            thd.join();
        }
        return flagArray;
    }

    /**
     * Compare each element of the array to all others. The corresponding index
     * of the lesser value will have its value changed from 1 to 0.
     *
     * @param flagArray
     * @param inputValues
     * @return The updated flag array
     * @throws InterruptedException
     */
    static int[] findMax(final int[] flagArray, final int[] inputValues) throws InterruptedException {
        int thdArrayLength = (flagArray.length * (flagArray.length - 1)) / 2;
        Thread[] t = new Thread[thdArrayLength];
        int inc = 1;
        int ij = 0;
        for (int i = 0; i < inputValues.length - 1; i++) {  // 0,1,2
            for (int j = inc++; j < inputValues.length; j++) {  // 1,2,3
                final int indexI = i;
                final int indexJ = j;
                t[ij] = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println(Thread.currentThread());
                        int lesser;
                        if (Integer.compare(inputValues[indexI], inputValues[indexJ]) < 0) {
                            flagArray[indexI] = 0;
                            lesser = indexI;
                        } else {
                            flagArray[indexJ] = 0;
                            lesser = indexJ;
                        }
                        System.out.printf("Thread T(%d,%d) compares x[%1$d] = %d "
                                + "and x[%2$d] = %d, and writes 0 into w[%d]\n",
                                indexI, indexJ, flagArray[indexI],
                                flagArray[indexJ], lesser);
                    }

                }, "FindMax Thread: " + ij);
                t[ij++].start();
            }
        }

        /* Join threads to ensure orderly execution */
        for (Thread thd : t) {
            thd.join();
        }
        return flagArray;
    }

    /**
     * Look for the element in the flagArray that contains the 1. If element x
     * has the 1, print the values. Otherwise, do nothing.
     *
     * @param flagArray
     * @param inputValues
     * @throws InterruptedException
     */
    static void printResults(final int[] flagArray, final int[] inputValues) throws InterruptedException {
        Thread[] t = new Thread[flagArray.length];
        for (int i = 0; i < t.length; i++) {
            final int index = i;
            t[i] = new Thread(new Runnable() {

                @Override
                public void run() {
                    System.out.println(Thread.currentThread());
                    if (flagArray[index] == 1) {
                        System.out.println("Maximum                = " + inputValues[index]);
                        System.out.println("Location               = " + index);
                    }
                }
            }, "PrintResults Thread: " + i);
            t[i].start();
        }

        for (Thread thd : t) {
            thd.join();
        }
    }

    /**
     * Control thread creation and output
     *
     * @param args Command line input
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        /* Check that number of args is within parameters */
        if (args == null || args.length < 4 || args.length > 11) {
            operationMessage();
            return;
        }

        /* Get the integer values from the array */
        int[] inputValues = parseArgs(args);
        if (inputValues == null) {
            operationMessage();
            return;
        }

        /* Check that args[0] matches number of inputs */
        int intArraySize = Integer.parseInt(args[0]);
        if (Integer.compare(inputValues.length, intArraySize) != 0) {
            operationMessage();
            return;
        }

        /* Initialize all w elements to 1 */
        int[] flagArray = initializeFlagArray(new int[intArraySize]);
        System.out.println("Number of input values = " + inputValues.length);
        System.out.println("Input values         x = " + arrayToString(inputValues));
        System.out.println("After initialization w = " + arrayToString(flagArray));

        /* Find the maximum value and set token array to 1 at that index */
        flagArray = findMax(flagArray, inputValues);
        System.out.println("After step 2         w = " + arrayToString(flagArray));

        /* Print the maximum value info */
        printResults(flagArray, inputValues);
        System.out.println("All done!");
    }
}
