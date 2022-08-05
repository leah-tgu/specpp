package org.processmining.estminer.specpp.util.datastructures;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RegexLabel extends Label {

    private final Pattern pattern;
    private Predicate<String> predicate;

    public RegexLabel(String text) {
        super(text);
        pattern = Pattern.compile(text);
        predicate = pattern.asPredicate();
    }

    @Override
    public boolean lt(Label other) {
        if (other instanceof RegexLabel) {
            return predicate.test(other.text);//&& !((RegexLabel) other).predicate.test(text);
        } else return predicate.test(other.text);
    }

    @Override
    public boolean gt(Label other) {
        if (other instanceof RegexLabel) {
            return ((RegexLabel) other).predicate.test(text); // && !predicate.test(other.text);
        } else return false;
    }
}
