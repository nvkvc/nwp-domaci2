package data.controller;

import data.beans.ComponentBean;
import data.beans.SingletonBean;
import framework.di.annotations.*;

@Controller
public class BazController {

    @Autowired
    private SingletonBean singletonBean;

    @Autowired
    private ComponentBean componentBean;

    @Path("/baz")
    @GET
    public String incrementSingleton() {
        return String.format("%d", singletonBean.increment());
    }

    @Path("/baz")
    @POST
    public String increment() {
        return String.format("%d", componentBean.increment());
    }
}
