package data.beans;

import framework.di.annotations.Autowired;
import framework.di.annotations.Component;
import framework.di.annotations.Qualifier;

@Qualifier("RecursiveBean")
@Component
public class RecursiveBean {

    @Autowired
    private ComponentBean componentBean;

    public int increment() {
        return componentBean.increment();
    }
}
