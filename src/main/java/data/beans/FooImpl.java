package data.beans;

import data.DummyObject;
import data.intf.FooIntf;
import framework.di.annotations.Bean;
import framework.di.annotations.Qualifier;

@Qualifier("Fooicina")
@Bean(scope="prototype")
public class FooImpl implements FooIntf {

    private DummyObject dummyObject = new DummyObject();
}
