package group20;


import genius.core.Bid;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.ValueDiscrete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DistributionOpponent {
    private final double LAPLACE_EXPONENT = 0.5; //variable between 0 and 1
    private ArrayList<HashMap<ValueDiscrete,Integer>> issuesOptionFrequency;
    private ArrayList<Double> weightsOfIssues; //sum of weights is 1
    private ArrayList<HashMap<ValueDiscrete, Double>> valuesOfOptions; //Values are normalised to (0,1). They can be stored in the same hashmap cause why not?
    private ArrayList<Bid> pastOpponentsBids;

    private int priorBids = 0;
    //Value function estimation
    //We assume, that the best option will be played the most and changed less frequently


    //WHENEVER YOU USE ISSUE NUMBER, REMEMBER IT'S OFF BY ONE

    public DistributionOpponent(List<Issue> issues){
        issuesOptionFrequency = new ArrayList<HashMap<ValueDiscrete, Integer>>();
        weightsOfIssues = new ArrayList<>();
        pastOpponentsBids = new ArrayList<>();
        valuesOfOptions = new  ArrayList<HashMap<ValueDiscrete, Double>>();


        for(int i = 0; i < issues.size(); i++){
            issuesOptionFrequency.add(new HashMap<ValueDiscrete, Integer>());
            valuesOfOptions.add((new HashMap<ValueDiscrete, Double>()));
            weightsOfIssues.add((new Double(0)));
        }

        for(Issue issue : issues){
            int issueNumber = issue.getNumber();
            IssueDiscrete issueDiscrete = (IssueDiscrete) issue;

            //Start putting Pairs into the issue array
            Integer counter = 1;
            for (ValueDiscrete valueDiscrete : issueDiscrete.getValues()){

                //Initialize each value in both hashmaps to 0: occurences and value
                issuesOptionFrequency.get(issueNumber-1).put(valueDiscrete, 0);
                valuesOfOptions.get(issueNumber-1).put(valueDiscrete, 0.0);
            }

        }
    }
/*
    private void updateValues(Bid bid){
        for(Issue issue : bid.getIssues()){
            ValueDiscrete valueInBid = (ValueDiscrete)bid.getValue(issue);
            //Map of values for a given isseu
            HashMap<ValueDiscrete,Double> issueValueMap = valuesOfOptions.get(issue.getNumber()-1);
            HashMap<ValueDiscrete,Integer> frequencyMap = issuesOptionFrequency.get(issue.getNumber()-1);
            Double count = 1.0 + frequencyMap.get(valueInBid).doubleValue();
            //Find maximal value for the given issue
            Double
        }
    }
*/
    private void updateValues(Bid bid){

        //for each issue
        for(int issueIndex = 0 ; issueIndex < issuesOptionFrequency.size(); issueIndex++){
            HashMap<ValueDiscrete,Double> issueValueMap = valuesOfOptions.get(issueIndex);
            HashMap<ValueDiscrete,Integer> issueFrequencyMap = issuesOptionFrequency.get(issueIndex);

            //find maximal frequency count fro the given issue
            Double maxCount = 1.0 +  Collections.max(issueFrequencyMap.values()).doubleValue();

            //update all values
            for(ValueDiscrete valueDiscrete: issueValueMap.keySet()){
                Double valueCount = 1.0 +  issueFrequencyMap.get(valueDiscrete).doubleValue();
                //Laplace smoothing
                Double updatedValue = Math.pow(valueCount / maxCount, LAPLACE_EXPONENT);
                issueValueMap.put(valueDiscrete, updatedValue);
            }

        }
        return;
    }


/*
    //Returns 1 if bid contains this value, 0 otherwise
    private double optionInOffer(ValueDiscrete option, Bid bid){
        boolean contains =  bid.getValues().containsValue(option);
        if(contains == true)
        return 1.0;
        return 0.0;
    }
    private double getValueOfOption(ValueDiscrete option){
        double numerator = 1.0;
        //TODO optimize this: too much traversing
        //add one to the numerator every time this value is in past bids
        for(Bid bid : pastOpponentsBids){
            numerator += optionInOffer(option, bid);
        }

    }
*/
    public ArrayList updateFrequencyCounter(Bid lastOffer) {
        this.priorBids += 1;
        for (Issue issue : lastOffer.getIssues()) {
            HashMap<ValueDiscrete, Integer> map = this.issuesOptionFrequency.get(issue.getNumber() - 1);
            ValueDiscrete value = (ValueDiscrete) lastOffer.getValue(issue);
            map.put(value, map.get(value) + 1);
        }
    }

}
