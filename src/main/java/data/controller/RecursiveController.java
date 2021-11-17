package data.controller;

import data.beans.RecursiveBean;
import framework.di.annotations.Autowired;
import framework.di.annotations.Controller;
import framework.di.annotations.GET;
import framework.di.annotations.Path;

@Controller
public class RecursiveController {

//    @Autowired
//    private Object nesto;

    @Autowired
    private RecursiveBean recursiveBean;

    @GET
    @Path("/rec")
    public String get() {
        return String.format("%d", recursiveBean.increment());
    }
}
