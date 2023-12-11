package pt.isec.pd.a2020136093.server.rest_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.pd.a2020136093.server.model.jdbc.ManageDB;
import pt.isec.pd.a2020136093.server.rest_api.Application;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class UserController {

    @GetMapping("/user")
    public ResponseEntity<String> user()
    {
        if(Application.getClientData().getAdmin()){
            return ResponseEntity.ok("Current user is admin!");
        }
        else{
            return ResponseEntity.ok("Current user is not admin!");
        }
    }
}
