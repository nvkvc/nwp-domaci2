package data.controller;

import framework.di.annotations.Controller;
import framework.di.annotations.GET;
import framework.di.annotations.POST;
import framework.di.annotations.Path;

@Controller
public class DogController {
    @GET
    @Path("/dog")
    public String getDummy() {
        return  "get dog";
    }

    @POST
    @Path("/dog")
    public String postDummy() {
        return "post dog";
    }
}
