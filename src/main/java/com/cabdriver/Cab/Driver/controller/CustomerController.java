package com.cabdriver.Cab.Driver.controller;

import com.cabdriver.Cab.Driver.exceptions.UserNotFound;
import com.cabdriver.Cab.Driver.models.Customer;
import com.cabdriver.Cab.Driver.requestbody.UserCredentialRequestBody;
import com.cabdriver.Cab.Driver.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @PostMapping("/register")
    public String createAccount(@RequestBody Customer customer){
        customerService.registerAccount(customer);
        return "Account created successfully";
    }

    @GetMapping("/authenticate")
    public String loginCustomer(@RequestBody UserCredentialRequestBody userCredentialRequestBody){

        String email = userCredentialRequestBody.getEmail();
        String password = userCredentialRequestBody.getPassword();
        try{
            String  authenticationDetails = customerService.authenticateUser(email, password);
            return authenticationDetails;
        }catch (UserNotFound userNotFound){
            return userNotFound.getMessage();
        }

    }
}
