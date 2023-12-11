package pt.isec.pd.a2020136093.server.rest_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.isec.pd.a2020136093.server.model.jdbc.ManageDB;
import pt.isec.pd.a2020136093.server.rest_api.Application;
import pt.isec.pd.a2020136093.server.rest_api.security.TokenService;

import java.sql.SQLException;

@RestController
public class RegisterController {
    ManageDB manageDB = Application.getManageDB();

    @GetMapping("/register")
    public ResponseEntity<String> register(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "nid") String nid
    )
    {
        ResponseEntity.ok("CARALHOOOOO");
        try {
            if(manageDB.addNewUser(name, email, password, nid)){
                return ResponseEntity.ok("Email: " + email + " registered!");
            }
            else{
                return ResponseEntity.badRequest().body("Email: " + email + " already exists!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
