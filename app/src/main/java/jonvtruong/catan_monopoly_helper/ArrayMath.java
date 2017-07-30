package jonvtruong.catan_monopoly_helper;

/**
 * Created by jonvt on 7/30/2017.
 */

public class ArrayMath{
    protected int getArrayQuotient(int[] hand, int[] cost){
        int lowest = 0;

        for (int i = 0; i < cost.length; i++) {
            if(cost[i] != 0) {
                int quotient = hand[i] / cost[i];
                if(quotient<lowest || i==0){
                    lowest = quotient;
                }
            }

        }
        return lowest;
    }

    protected int[] getArrayDifference(int[] hand, int[] cost, int count){
        int[] difference = new int[hand.length];

        for(int i=0; i<hand.length; i++){
            difference[i] = hand[i] - cost[i]*count;
        }
        return difference;
    }
}