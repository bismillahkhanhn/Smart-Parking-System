package com.smartparking.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")            public String home()          { return "login"; }
    @GetMapping("/login")       public String login()         { return "login"; }
    @GetMapping("/register")    public String register()      { return "register"; }
    @GetMapping("/otp-verify")  public String otp()           { return "otp-verify"; }
    @GetMapping("/user-dashboard")  public String userDash()  { return "user-dashboard"; }
    @GetMapping("/owner-dashboard") public String ownerDash() { return "owner-dashboard"; }
    @GetMapping("/parking-list") public String parkingList()  { return "parking-list"; }
    @GetMapping("/booking")     public String booking()       { return "booking"; }
    @GetMapping("/payment")     public String payment()       { return "payment"; }
    @GetMapping("/rating")      public String rating()        { return "rating"; }
}
