package data.controller;

import framework.di.annotations.Controller;
import framework.di.annotations.GET;
import framework.di.annotations.POST;
import framework.di.annotations.Path;

@Controller
public class CatController {
    @GET
    @Path("/cat")
    public String getDummy() {
        return  "get cat";
    }

    @POST
    @Path("/cat")
    public String postDummy() {
        return "post cat";
    }
}
