package pt.isec.pd.a2020136093.server.rest_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.pd.a2020136093.server.model.jdbc.ManageDB;
import pt.isec.pd.a2020136093.server.rest_api.Application;

@RestController
public class CodeController {
    ManageDB manageDB = Application.getManageDB();

    @GetMapping("/code")
    public ResponseEntity<String> code(
            @RequestParam(name = "code") String code
    )
    {
        if(Application.getClientData().getAdmin()){
            return ResponseEntity.ok("Admins can't submit an event code!");
        }
        else{
            if(manageDB.submitCode(Application.getClientData().getEmail(), code)){
                return ResponseEntity.ok("Code submitted successfully!");
            }
            return ResponseEntity.ok("There was an error submiting the code!"); // CODIGO ERRADO / NAO EXISTE / ERROR NA DB
        }
    }
}
