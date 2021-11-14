package data.controller;

import data.intf.FooIntf;
import framework.di.annotations.Autowired;
import framework.di.annotations.Controller;
import framework.di.annotations.Qualifier;

@Controller
public class FooController {

    @Autowired
    @Qualifier("Fooicina")
    private FooIntf fooIntf;
}
