package data.controller;

import data.DummyObject;
import framework.di.annotations.Autowired;
import framework.di.annotations.Controller;

@Controller
public class FooController {

    @Autowired
    private DummyObject dummyObject;
}
