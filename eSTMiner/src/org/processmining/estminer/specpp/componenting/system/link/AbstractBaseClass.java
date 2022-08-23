package org.processmining.estminer.specpp.componenting.system.link;

import com.google.common.collect.Streams;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.componenting.system.LocalComponentRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractBaseClass implements FullComponentSystemUser {

    private final LocalComponentRepository lcr = new LocalComponentRepository();
    private final GlobalComponentRepository gcr = new GlobalComponentRepository();

    private final List<FullComponentSystemUser> subcomponents = new LinkedList<>();


    @Override
    public void registerSubComponent(FullComponentSystemUser subComponent) {
        subcomponents.add(subComponent);
    }

    @Override
    public Stream<FullComponentSystemUser> collectTransitiveSubcomponents() {
        return Streams.concat(subcomponents.stream()
                                           .flatMap(FullComponentSystemUser::collectTransitiveSubcomponents), Streams.stream(Optional.of(this)));
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

    protected void preSubComponentInit() {}

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
