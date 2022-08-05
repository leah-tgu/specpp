import com.google.common.collect.Sets;
import org.apache.commons.collections4.IteratorUtils;
import org.junit.Assert;
import org.junit.Test;
import org.processmining.estminer.specpp.base.impls.SpecPP;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorCollection;
import org.processmining.estminer.specpp.componenting.supervision.FulfilledObservableRequirement;
import org.processmining.estminer.specpp.componenting.supervision.ObservableRequirement;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.*;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceGenerator;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.evaluation.fitness.AggregatedBasicFitnessEvaluation;
import org.processmining.estminer.specpp.evaluation.fitness.MarkingHistoryBasedFitnessEvaluator;
import org.processmining.estminer.specpp.orchestra.BaseSpecOpsConfigBundle;
import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.FixedOrdering;
import org.processmining.estminer.specpp.datastructures.InputDataBundle;
import org.processmining.estminer.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.datastructures.encoding.HashmapEncoding;
import org.processmining.estminer.specpp.datastructures.encoding.IndexSubset;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncodings;
import org.processmining.estminer.specpp.datastructures.log.Activity;
import org.processmining.estminer.specpp.datastructures.log.Log;
import org.processmining.estminer.specpp.datastructures.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.datastructures.log.impls.LogBuilderImpl;
import org.processmining.estminer.specpp.datastructures.log.impls.VariantImpl;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.datastructures.petri.Transition;
import org.processmining.estminer.specpp.datastructures.tree.base.*;
import org.processmining.estminer.specpp.datastructures.vectorization.IVSComputations;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;
import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.util.*;
import org.processmining.estminer.specpp.datastructures.util.Label;
import org.processmining.estminer.specpp.datastructures.util.RegexLabel;
import org.processmining.estminer.specpp.datastructures.util.Tuple2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.processmining.estminer.specpp.orchestra.SpecOpsSetup.executeSpecOps;
import static org.processmining.estminer.specpp.orchestra.SpecOpsSetup.setupSpecOps;
import static org.processmining.estminer.specpp.util.HardcodedTestInput.getDummyInputBundle;

public class Main {


    @Test
    public void transitionsets() {

        Transition start = new Transition("\u25B7");
        Transition end = new Transition("\u2610");
        Transition a = new Transition("a");
        Transition b = new Transition("b");
        Transition c = new Transition("c");
        Transition d = new Transition("d");

        Set<Transition> presetTransitions = Sets.newHashSet(start, a, b, c, d);
        Set<Transition> postsetTransitions = Sets.newHashSet(a, b, c, d, end);

        Comparator<Transition> presetOrdering = new FixedOrdering<>(start, a, b, c, d);
        Comparator<Transition> postsetOrdering = new FixedOrdering<>(a, b, c, d, end);

        HashmapEncoding<Transition> presetEncoding = new HashmapEncoding<>(presetTransitions, presetOrdering);
        HashmapEncoding<Transition> postEncoding = new HashmapEncoding<>(postsetTransitions, postsetOrdering);

        BitEncodedSet<Transition> s1 = BitEncodedSet.empty(presetEncoding);
        s1.addAll(start, b, c);
        BitEncodedSet<Transition> s2 = BitEncodedSet.empty(postEncoding);
        BitEncodedSet<Transition> s3 = BitEncodedSet.empty(postEncoding);
        s3.addAll(c, b, end);

        BitEncodedSet<Transition>[] sets = new BitEncodedSet[]{s1, s2, s3};
        for (int i = 0; i < sets.length; i++) {
            BitEncodedSet<Transition> s = sets[i];
            System.out.println(s + " / " + s.getBitMask() + " c=" + s.cardinality() + " ,l=" + s.maxSize());
            for (int j = 0; j <= s.maxSize(); j++) {
                System.out.println("s" + i + " kMaxRange(" + j + ") = " + s.kMaxIndex(j - 1) + " - " + s.kMaxIndex(j) + " - 1 = " + s.kMaxRange(j) + " / " + s.kMaxRangeMask(j));
            }
        }

    }

    @Test
    public void tree() {

        Transition start = new Transition("\u25B7");
        Transition end = new Transition("\u2610");
        Transition a = new Transition("a");
        Transition b = new Transition("b");
        Transition c = new Transition("c");

        Set<Transition> presetTransitions = Sets.newHashSet(start, a, b);
        Set<Transition> postsetTransitions = Sets.newHashSet(end, a, b);

        FixedOrdering<Transition> presetOrdering = new FixedOrdering<>(start, a, b, c);
        FixedOrdering<Transition> postsetOrdering = new FixedOrdering<>(a, b, c, end);

        PlaceGenerator pg = new PlaceGenerator(new IntEncodings<>(new HashmapEncoding<>(presetTransitions, presetOrdering), new HashmapEncoding<>(postsetTransitions, postsetOrdering)));
        EnumeratingTree<PlaceNode> tree = new EnumeratingTree<>(pg.generateRoot(), new VariableExpansion<>());

        System.out.println(tree);

        for (int i = 0; i < 50; i++) {
            PlaceNode next = tree.expandTree();
            System.out.println("created " + i + " : " + next.getProperties());
        }

    }

    @Test
    public void bigtree() {
        int k = 10, n = 100000;
        Set<Transition> transitions = IntStream.range(0, k)
                                               .mapToObj(i -> new Transition("" + i))
                                               .collect(Collectors.toSet());

        HashmapEncoding<Transition> encoding = new HashmapEncoding<>(transitions, Comparator.comparingInt(o -> Integer.parseInt(o.toString())));

        PlaceGenerator pg = new PlaceGenerator(new IntEncodings<>(encoding, encoding));

        EnumeratingTree<PlaceNode> tree = new EnumeratingTree<>(pg.generateRoot(), new VariableExpansion<>());


        int printerval = 5000;
        List<Place> places = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            PlaceNode expansion = tree.expandTree();
            places.add(expansion.getProperties());
            if (i % printerval == 0) {
                System.out.println(places.subList(Math.max(0, i - Math.min(i / 10, Math.min(k, 10))), i));
                System.out.println("current leaves: " + IteratorUtils.size(tree.getLeaves()));
            }
        }

    }


    @Test
    public void traversal() {
        AnnotatableBiDiNodeImpl<String> root = ReflectiveNodeFactory.annotatedRoot(JavaTypingUtils.castClass(AnnotatableBiDiNodeImpl.class), "a");
        BiDiTree<AnnotatableBiDiNodeImpl<String>> tree = new BiDiTreeImpl<>(root);

        System.out.println(tree);

        AnnotatableBiDiNodeImpl<String> b = ReflectiveNodeFactory.annotatedChildOf(root, "b");
        ReflectiveNodeFactory.annotatedChildOf(root, "c");
        ReflectiveNodeFactory.annotatedChildOf(b, "d");
        AnnotatableBiDiNodeImpl<String> e = ReflectiveNodeFactory.annotatedChildOf(b, "e");
        ReflectiveNodeFactory.annotatedChildOf(e, "f");

        System.out.println(tree);
    }


    @Test
    public void ivs_math() {
        IntVectorStorage ivs1 = IntVectorStorage.of(new int[]{1, 1, 0, -2, 1, 2, -1, 0, 0, 1, 1, 1, 1, -1, 1}, new int[]{3, 4, 8});
        IntVectorStorage ivs2 = IntVectorStorage.of(new int[]{0, 1, 1, -1, 0, 1, 0, -1, 0, -2, 1, 2, 1, -1, 0}, new int[]{3, 4, 8});

        System.out.println(ivs1);
        System.out.println(ivs2);
        System.out.println(IVSComputations.interleave(ivs1, ivs2));
    }


    @Test
    public void impliciticity() {
        String[] labels = {"a", "b", "c", "d", "e"};
        Tuple2<IntEncodings<Transition>, Map<String, Transition>> tuple2 = HardcodedTestInput.setupTransitions(labels);
        Map<String, Activity> as = HardcodedTestInput.setupActivities(labels);
        IntEncodings<Transition> encs = tuple2.getT1();
        Activity a = as.get("a");
        Activity b = as.get("b");
        Activity c = as.get("c");
        Activity d = as.get("d");
        Activity e = as.get("e");
        Log log = new LogBuilderImpl().appendVariant(VariantImpl.of(a, b, c, d, e), 2)
                                      .appendVariant(VariantImpl.of(a, c, b, d, e), 1)
                                      .appendVariant(VariantImpl.of(a, c, d, b, e), 3)
                                      .build();

        Map<String, Transition> ts = tuple2.getT2();
        Transition ta = ts.get("a");
        Transition tb = ts.get("b");
        Transition tc = ts.get("c");
        Transition td = ts.get("d");
        Transition te = ts.get("e");
        Placemaker maker = new Placemaker(encs);
        Place p1 = maker.preset(ta).postset(tb).get();
        Place p2 = maker.preset(ta).postset(tc).get();
        Place p3 = maker.preset(tb).postset(te).get();
        Place p4 = maker.preset(tc).postset(td).get();
        Place p5 = maker.preset(td).postset(te).get();
        Place p6 = maker.preset(ta, tb, tc, td).postset(tb, tc, td, te).get();
        Place p7 = maker.preset(ta).postset(te).get();

        Map<Activity, Transition> mapping = HardcodedTestInput.setupMapping(as, ts);


        MarkingHistoryBasedFitnessEvaluator ev = new MarkingHistoryBasedFitnessEvaluator();
        ev.componentSystemAdapter().fulfilFrom(DataRequirements.CONSIDERED_VARIANTS.fulfilWith(() -> BitMask.of(0)));
        EvaluatorCollection ec = new EvaluatorCollection();
        ec.register(EvaluationRequirements.evaluator(Place.class, AggregatedBasicFitnessEvaluation.class, ev::aggregatedEval));

        System.out.println(ev.aggregatedEval(p1));
        System.out.println(ev.aggregatedEval(p2));
        System.out.println(ev.aggregatedEval(p3));
        System.out.println(ev.aggregatedEval(p4));
        System.out.println(ev.aggregatedEval(p5));

        PlaceCollection comp = new PlaceCollection();
        comp.accept(p1);
        comp.accept(p2);
        comp.accept(p3);
        comp.accept(p4);
        comp.accept(p5);
        System.out.println(comp.rateImplicitness(p6));
        System.out.println(comp.rateImplicitness(p7));

        ev.setConsideredVariants(BitMask.of(0));
        Place pprime = maker.preset(ta, tb).postset(tc, td).get();
        PlaceCollection c2 = new PlaceCollection();
        c2.componentSystemAdapter().fulfilFrom(ec);
        c2.accept(p1);
        System.out.println(c2.rateImplicitness(pprime));

        Place ptiny = maker.preset(ta).postset(td).get();
        Place psmaller = maker.preset(ta, tb).postset(tb, td).get();
        Place pbigger = maker.preset(ta, tb, tc).postset(tb, tc, td).get();
        PlaceCollection c3 = new PlaceCollection();
        c3.componentSystemAdapter().fulfilFrom(ec);
        c3.accept(pbigger);
        System.out.println(c3.rateImplicitness(psmaller));

    }

    @Test
    public void indexSubsetMapping() {
        IntVectorStorage ivs1 = IntVectorStorage.of(new int[]{1, 1, 0, -2, 1, 2, -1, /*from*/ 0, 0, 1, 1, 1, 0, -1, /*to*/ 1}, new int[]{3, 4, 7, 1});
        IntVectorStorage ivs2 = IntVectorStorage.of(new int[]{1, 1, 0, -2, 1, 2, -1, /*from*/ 0, 0, 1, 1, 1, 0, -1, /*to*/ 1}, new int[]{7, 7, 1});

        IndexSubset s1 = IndexSubset.of(BitMask.of(0, 1, 2, 3));
        IndexSubset s2 = IndexSubset.of(BitMask.of(1, 2, 5));

        DenseVariantMarkingHistories h1 = new DenseVariantMarkingHistories(s1, ivs1);
        DenseVariantMarkingHistories h2 = new DenseVariantMarkingHistories(s2, ivs2);

        BitMask variantMask = BitMask.of(2);

        System.out.println("h1 gt h2: " + h1.gtOn(variantMask, h2));
        System.out.println("h1 lt h2: " + h1.ltOn(variantMask, h2));
        System.out.println("h1 ordering h2: " + h1.orderingRelationsOn(variantMask, h2));
        System.out.println(Arrays.toString(h1.getAt(2).toArray()));
        System.out.println(Arrays.toString(h2.getAt(2).toArray()));
    }

    @Test
    public void orchestra() {
        InputDataBundle bundle = getDummyInputBundle("a", "b", "c");
        System.out.println(bundle.getLog());
        System.out.println(bundle.getTransitionEncodings());
        System.out.println(bundle.getMapping());

        SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specPP = setupSpecOps(new BaseSpecOpsConfigBundle(), bundle);

        executeSpecOps(specPP);

    }

    @Test
    public void actualLog() {
        InputDataBundle bundle = TestFactory.defaultInputBundle();
        System.out.println(bundle.getLog());
        System.out.println(bundle.getTransitionEncodings());
        System.out.println(bundle.getMapping());

        SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specPP = setupSpecOps(new BaseSpecOpsConfigBundle(), bundle);

        executeSpecOps(specPP);

    }


    @Test
    public void labels() {

        Label l1 = new Label("hello.world");
        Label l2 = new Label("goodbye.world");
        RegexLabel r1 = new RegexLabel("\\w*.world");
        RegexLabel r2 = new RegexLabel("\\w*.\\w*");

        Assert.assertTrue(l1.gt(l1));
        Assert.assertTrue(l1.lt(l1));
        Assert.assertSame(l1.gt(l2), l2.lt(l1));
        Assert.assertFalse(l1.gt(l2));
        Assert.assertSame(l1.lt(l2), l2.gt(l1));
        Assert.assertFalse(l2.gt(l1));

        Assert.assertTrue(l2.gt(l2));
        Assert.assertTrue(l2.lt(l2));
        Assert.assertSame(r1.gt(r2), r2.lt(r1));
        Assert.assertTrue(r1.gt(r2));
        Assert.assertSame(r1.lt(r2), r2.gt(r1));
        Assert.assertFalse(r2.gt(r1));

        Assert.assertTrue(r1.gt(r1));
        Assert.assertTrue(r1.lt(r1));
        Assert.assertSame(r1.gt(l1), l1.lt(r1));
        Assert.assertFalse(r1.gt(l1));
        Assert.assertSame(r1.lt(l1), l1.gt(r1));
        Assert.assertTrue(l1.gt(r1));

        Assert.assertTrue(r2.gt(r2));
        Assert.assertTrue(r2.lt(r2));
        Assert.assertSame(r2.gt(l1), l1.lt(r2));
        Assert.assertFalse(r2.gt(l1));
        Assert.assertSame(r2.lt(l1), l1.gt(r2));
        Assert.assertTrue(l1.gt(r2));

        FulfilledObservableRequirement<Observation> f1 = SupervisionRequirements.observable("hello.world", Observation.class, PipeWorks.identityPipe());
        FulfilledObservableRequirement<Observation> f2 = SupervisionRequirements.observable("goodbye.world", Observation.class, PipeWorks.identityPipe());
        FulfilledObservableRequirement<Observation> f3 = SupervisionRequirements.observable("\\w*", Observation.class, PipeWorks.identityPipe());
        ObservableRequirement<Observation> req = SupervisionRequirements.observable(SupervisionRequirements.regex("\\w*.world"), Observation.class);

        Assert.assertSame(f1.gt(req), req.lt(f1.getComparable()));
        Assert.assertSame(f2.gt(req), req.lt(f2.getComparable()));
        Assert.assertSame(f3.gt(req), req.lt(f3.getComparable()));
        Assert.assertSame(f1.lt(req), req.gt(f1.getComparable()));
        Assert.assertSame(f2.lt(req), req.gt(f2.getComparable()));
        Assert.assertSame(f3.lt(req), req.gt(f3.getComparable()));

        Assert.assertTrue(f1.gt(req));
        Assert.assertTrue(f2.gt(req));
        Assert.assertFalse(f3.gt(req));


    }

}
