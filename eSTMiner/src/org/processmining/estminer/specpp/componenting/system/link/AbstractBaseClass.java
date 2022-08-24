package org.processmining.estminer.specpp.componenting.system.link;

import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.componenting.system.LocalComponentRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractBaseClass implements FullComponentSystemUser {

    private final LocalComponentRepository lcr = new LocalComponentRepository();
    private final GlobalComponentRepository gcr = new GlobalComponentRepository();

    private final List<FullComponentSystemUser> subcomponents = new LinkedList<>();


    @Override
    public void registerSubComponent(FullComponentSystemUser subComponent) {
        subcomponents.add(subComponent);
    }

    @Override
    public List<FullComponentSystemUser> collectTransitiveSubcomponents() {
        List<FullComponentSystemUser> collect = subcomponents.stream()
                                                             .flatMap(fcsu -> fcsu.collectTransitiveSubcomponents()
                                                                                  .stream())
                                                             .collect(Collectors.toList());
        collect.add(this);
        return collect;
    }


    @Override
    public final void init() {
        preSubComponentInit();
        for (FullComponentSystemUser subcomponent : subcomponents) {
            subcomponent.init();
        }
        postSubComponentInit();
    }

    protected void postSubComponentInit() {
        initSelf();
    }

    protected void preSubComponentInit() {
    }

    protected abstract void initSelf();


    @Override
    public ComponentCollection localComponentSystem() {
        return lcr;
    }

    @Override
    public ComponentCollection componentSystemAdapter() {
        return gcr;
    }

    @Override
    public ComponentCollection getComponentCollection() {
        return gcr;
    }

}
