package data.controller;

import data.beans.SingletonBean;
import data.intf.FooIntf;
import data.intf.Incrementer;
import framework.di.annotations.*;

@Controller
public class FooController {

    @Autowired
    @Qualifier("Fooicina")
    private FooIntf fooIntf;

    @Autowired
    private SingletonBean singletonBean;

    @Autowired
    @Qualifier("ComponentBean")
    private Incrementer componentBean;

    @Path("/foo")
    @GET
    public String incrementSingleton() {
        return String.format("%d", singletonBean.increment());
    }

    @Path("/foo")
    @POST
    public String increment() {
        return String.format("%d", componentBean.increment());
    }
}
