package com.csx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: TODO
 * @author: csx
 * @Date: 2018-07-25
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    @RequestMapping("index.page")
    public ModelAndView index() {
        return new ModelAndView("admin");
    }
}
