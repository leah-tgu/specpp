package org.processmining.estminer.discovery;


import org.processmining.estminer.candidatetraverser.AbstractCandidateTraverser;
import org.processmining.estminer.estcoreobjects.MyLog;
import org.processmining.estminer.estcoreobjects.MyPlace;
import org.processmining.estminer.estcoreobjects.MyPlaceStatus;
import org.processmining.estminer.estcoreobjects.MyProcessModel;
import org.processmining.estminer.implicitplaceremoval.AbstractImplicitPlacesRemover;
import org.processmining.estminer.placeevaluators.MyPlaceEvaluator;
import org.processmining.estminer.plugins.Parameters;

public abstract class AbstractDiscovery extends Thread {
    protected MyProcessModel pM;
    protected final AbstractCandidateTraverser candidates;
    protected final MyPlaceEvaluator evaluator;
    protected final AbstractImplicitPlacesRemover IPRemover;
    protected final MyLog log;
    protected final boolean removeImpsConcurrently;
    protected final boolean repairWhileRemovingIPs;


    public AbstractDiscovery(final MyProcessModel pM, final AbstractCandidateTraverser candidates, final MyPlaceEvaluator evaluator, AbstractImplicitPlacesRemover ipRemover, Parameters parameters, MyLog log) {
        this.pM = pM;
        this.candidates = candidates;
        this.evaluator = evaluator;
        this.IPRemover = ipRemover;
        this.log = log;
        if (parameters.isRemoveImps() && parameters.isRemoveImpsConcurrently()) {
            this.removeImpsConcurrently = true;
        } else {
            this.removeImpsConcurrently = false;
        }
        this.repairWhileRemovingIPs = parameters.isRepairWhileRemovingIPs();
    }


    //runs this thread until termination or interuption
    @Override
    public void run() {
        try {
            this.addPlaces();
            System.out.println("Finished adding places without interuption.");

        } catch (InterruptedException e) {
            System.out.println("Place Discovery interupted!");
        }
    }


    //adds places according to the subclass strategy
    protected void addPlaces() throws InterruptedException {
        int currentTreeDepth = 0;
        MyPlace current = candidates.getNext(null, MyPlaceStatus.FIT);
        this.pM.updateAndPrintStatus(log);
        while (current != null) {
            if (!this.isInterrupted()) {

                //update current tree depth and, possibly, perform corresponding actions (otherwise for debugging only)
                int updatedTreeDepth = getCurrentTreeDepth(current);
                if (currentTreeDepth != updatedTreeDepth) {// if tree depth changed
                    //System.out.println("\n New tree level: changed from "+currentTreeDepth+ " to " + updatedTreeDepth); //for debugging
                    performNextTreeLevelActions(currentTreeDepth, updatedTreeDepth, current); //e.g., update current tree depth in place combinator
                    currentTreeDepth = updatedTreeDepth;
                }

                //-------------evaluating local fitness of current----------------------------
                MyPlaceStatus fitness = evaluator.testPlace(current);
                if (fitness == MyPlaceStatus.FIT) {//dealing with locally fit places
                    this.handleLocallyFittingPlace(current);
                } else {// dealing with locally unfit places (currently: just ignore them)
                }

                //prepare next candidate iteration
                current = candidates.getNext(current, fitness);
            }//end of (non-interupted) current place adding iteration
            else {
                //handling interuption (timelimit)
                System.out.println("Timelimit for adding places has been reached (or other interuption).");
                break;
            }
        }//end of candidate traversal loop (interupted or finished)
        System.out.println("________________________End of Standard Place Evaluation________________________________________________________________________ \n");
        this.pM.updateAndPrintStatus(log);


        System.out.println("\n ______________________Perform end of discovery actions: ________________________________________________________________________ \n");
        pM = endOfDiscoveryActions(pM); //for delta discovery, this evaluates additional ('virtual') levels without adding further potentialplaces
        this.pM.updateAndPrintStatus(log);

        if (this.removeImpsConcurrently) {//if enabled, remove implicit places from current model
            if (repairWhileRemovingIPs) {
                pM = IPRemover.removeAllIPsAndRepair(pM);
            } else {
                pM = IPRemover.removeAllIPs(pM);
            }
            this.pM.updateAndPrintStatus(log);
        }

        System.out.println("_______________________________Returning to main... ________________________________________________________________________ \n");
    }


    abstract protected MyProcessModel endOfDiscoveryActions(MyProcessModel pM);


    abstract protected void handleLocallyFittingPlace(MyPlace current);

    abstract protected void performNextTreeLevelActions(int currentTreeDepth, int updatedTreeDepth, MyPlace current);


    protected int getCurrentTreeDepth(MyPlace current) {
        return (Integer.bitCount(current.getInputTrKey()) + Integer.bitCount(current.getOutputTrKey()));
    }

    public MyProcessModel getpM() {
        return pM;
    }


}
