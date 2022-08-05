package org.processmining.estminer.specpp.componenting.delegators;

import org.processmining.estminer.specpp.componenting.data.DataSource;

public class DelegatingDataSource<T> extends AbstractDelegator<DataSource<T>> implements DataSource<T> {


    public DelegatingDataSource() {
    }

    public DelegatingDataSource(DataSource<T> delegate) {
        super(delegate);
    }

    @Override
    public T getData() {
        return delegate.getData();
    }


}
