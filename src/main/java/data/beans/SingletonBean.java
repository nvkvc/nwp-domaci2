package data.beans;

import data.intf.Incrementer;
import framework.di.annotations.Qualifier;
import framework.di.annotations.Service;

@Qualifier("SingletonBean")
@Service
public class SingletonBean implements Incrementer {

    private int idx = 0;

    public int increment() {
        return idx++;
    }
}
