package org.processmining.estminer.specpp.supervision.observations.performance;

import org.processmining.estminer.specpp.supervision.observations.StringStatisticKey;

public class TaskDescription extends StringStatisticKey {

    public static final TaskDescription HEURISTICS_COMPUTATION = new TaskDescription("Tree Heuristics Computation");
    public static final TaskDescription TREE_EXPANSION = new TaskDescription("Tree Expansion");
    public static final TaskDescription AGGREGATED_EVAL = new TaskDescription("Aggregated Fitness Evaluation");
    public static final TaskDescription FULL_EVAL = new TaskDescription("Full Fitness Evaluation");
    public static final TaskDescription SHORT_CIRCUITING_AGGREGATED_EVAL = new TaskDescription("Short Circuiting Aggregated Fitness Evaluation");
    public static final TaskDescription SHORT_CIRCUITING_FULL_EVAL = new TaskDescription("Short Circuiting Full Fitness Evaluation");
    public static final TaskDescription CANDIDATE_PROPOSAL = new TaskDescription("Candidate Proposal");
    public static final TaskDescription CANDIDATE_COMPOSITION = new TaskDescription("Candidate Composition");
    public static final TaskDescription RESULT_GENERATION = new TaskDescription("Result Generation");
    public static final TaskDescription POST_PROCESSING = new TaskDescription("Post Processing");
    public static final TaskDescription PEC_CYCLE = new TaskDescription("PEC Cycle");
    public static final TaskDescription TOTAL_CYCLING = new TaskDescription("Total PEC Cycling");
    public static final TaskDescription REPLAY_BASED_CONCURRENT_IMPLICITNESS = new TaskDescription("Concurrent Replay Based Implicitness");

    public TaskDescription(String description) {
        super(description);
    }

}
