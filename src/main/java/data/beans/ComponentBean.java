package data.beans;

import data.intf.Incrementer;
import framework.di.annotations.Component;
import framework.di.annotations.Qualifier;

@Qualifier("ComponentBean")
@Component
public class ComponentBean implements Incrementer {

    private int idx = 0;

    public int increment() {
        return idx++;
    }
}
