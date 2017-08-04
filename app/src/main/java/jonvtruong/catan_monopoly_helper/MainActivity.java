package jonvtruong.catan_monopoly_helper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    GridLayout handGrid, buildGrid;
    String[] resourceName = {"wood", "brick", "sheep", "wheat", "ore"};
    String[] buildName = {"road", "settlement", "development card", "city"};

    Map<String, int[]> costs;
    int[] maxBuild = {15, 5, 34, 4}; /** road, settlement, dev card, city **/
    int[] previousBuild = {0,0,0,0};
    /*
    Build Costs:
    road [1 1 0 0 0]
    settlement [1 1 1 1 0]
    dev card [0 0 1 1 1]
    city [0 0 0 2 3]
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handGrid = (GridLayout) findViewById(R.id.handGrid);
        buildGrid = (GridLayout) findViewById(R.id.buildGrid);
        costs = new HashMap<>();
        setDefaults();
    }

    private void setDefaults(){
        /* Hand*/
        for(int i=5; i<10; i++){
            NumberPicker pick = (NumberPicker) handGrid.getChildAt(i);
            pick.setMinValue(0);
            pick.setMaxValue(24);
            pick.setValue(5);
            pick.setWrapSelectorWheel(false);
        }

        /* Costs */
        costs.put(buildName[0], new int[]{1, 1, 0, 0, 0}); //road
        costs.put(buildName[1], new int[]{1, 1, 1, 1, 0}); // settlement
        costs.put(buildName[2], new int[]{0, 0, 1, 1, 1}); // dev card
        costs.put(buildName[3], new int[]{0, 0, 0, 2, 3}); // city

        /* Builds */
        for(int i=4; i<8; i++){
            NumberPicker pick = (NumberPicker) buildGrid.getChildAt(i);
            pick.setMinValue(0);
            pick.setMaxValue(maxBuild[i-4]);
            pick.setWrapSelectorWheel(false);

            pick.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() { // called whenever build is changed
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    int current = buildGrid.indexOfChild(picker);
                    updateHand(picker); // every time build changed, update remaining hand
                    // update max build for others except the one that just changed
                }
            });

        }
    }

    /** Called when the user taps the Submit button */
    public void submitHand(View view) {
        int[] numResource = new int[5];

        for(int i=0; i<resourceName.length; i++){ // saves hand into array
            numResource[i]=getNumResource(i);
        }

        setMaxHand(); // sets the maximum resource to be whatever the current hand is
        setMaxBuild(calculateBuild(numResource)); // sets the maximum possible build for each purchase
    }

    public void remainingHand(){
        int[] fullBuild = setMaxBuild(calculateBuild((numResource)));
    }

    private int[] calculateBuild(int[] hand){
        int[] build = new int[previousBuild.length];
        int[] tempHand = hand.clone();
        ArrayMath a = new ArrayMath();

        for (int i=0; i<build.length; i++) {
            int[] currentBuildCost = costs.get(buildName[i]); // get build to start calculating from array of buildName: road, settlement, dev card, city
            Log.d("console", "before build: " + Arrays.toString(tempHand));
            build[i] = a.getArrayQuotient(tempHand, currentBuildCost); //get number of current item that can be built
            Log.d("console", buildName[i] + " built: " + build[i]);
            // tempHand = a.getArrayDifference(tempHand, currentBuildCost, build[i]); //subtract total build cost from hand balance
            //Log.d("console", "after build: " + Arrays.toString(tempHand));
        }
        return build;
    }

    private void setMaxBuild(int[] build){
        for(int i=0; i<build.length; i++){
            NumberPicker buildPicker = (NumberPicker) buildGrid.getChildAt(i+4);
            if(build[i] < maxBuild[i]) {
                buildPicker.setMaxValue(build[i]);
            }
        }
    }

    private void setMaxHand(){
        for(int i=0; i<resourceName.length; i++){
            NumberPicker picker = (NumberPicker) handGrid.getChildAt(i+5);
            if(picker.getValue() < 24) {
                picker.setMaxValue(picker.getValue());
            }
        }
    }

    private int getNumResource(int index){
        NumberPicker resource = (NumberPicker) handGrid.getChildAt(index+5);
        return resource.getValue();
    }

    /** Called when the user taps the Update button */
    public void updateHand(View view) {
        int[] currentHand = new int[resourceName.length];

        for(int i=0; i<currentHand.length; i++){
            currentHand[i] = getNumResource(i);
        }

        setHand(calculateHand(currentHand));
    }

    /** Calculates and returns the new max hand given the change in build **/
    private int[] calculateHand(int[] hand){
        int[] currentHand = hand.clone();
        int[] currentBuild = new int[previousBuild.length];

        ArrayMath a = new ArrayMath();

        for(int i=0; i<previousBuild.length; i++){
            currentBuild[i] = getBuild(i);
        }

        int[] changeBuild = a.getArrayDifference(currentBuild, previousBuild, 1); //don't need this anymore
        previousBuild = currentBuild.clone();

        for(int i=0; i<previousBuild.length; i++){
            currentHand = a.getArrayDifference(currentHand, costs.get(buildName[i]), changeBuild[i]);
        }
        return currentHand;
    }

    private int getBuild(int index){
        NumberPicker resource = (NumberPicker) buildGrid.getChildAt(index+4);
        return resource.getValue();
    }

    private void setHand(int[] hand){
        for(int i=0; i<hand.length; i++){
            NumberPicker picker = (NumberPicker) handGrid.getChildAt(i+5);
            picker.setValue(hand[i]);
        }
    }
}

