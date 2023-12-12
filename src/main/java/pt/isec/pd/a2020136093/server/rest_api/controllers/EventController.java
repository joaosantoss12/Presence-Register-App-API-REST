package pt.isec.pd.a2020136093.server.rest_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.pd.a2020136093.server.model.jdbc.ManageDB;
import pt.isec.pd.a2020136093.server.rest_api.Application;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

@RestController
public class EventController {
    ManageDB manageDB = Application.getManageDB();

    @GetMapping("/create")
    public ResponseEntity<String> create(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "local") String local,
            @RequestParam(name = "dataR") String dataR,
            @RequestParam(name = "horaI") String horaI,
            @RequestParam(name = "horaF") String horaF
    ) {
        if (Application.getClientData().getAdmin()) {
            if (manageDB.addNewEvent(name, local, dataR, horaI, horaF)) {
                return ResponseEntity.ok("Event created successfully");
            } else {
                return ResponseEntity.ok("Error creating event");
            }
        } else {
            return ResponseEntity.ok("Students cant create events");
        }
    }

    @GetMapping("/delete")
    public ResponseEntity<String> delete(
            @RequestParam(name = "id") String id
    ) {
        if (Application.getClientData().getAdmin()) {
            if (manageDB.deleteEvent(Integer.parseInt(id))) {
                return ResponseEntity.ok("Event deleted successfully");
            } else {
                return ResponseEntity.ok("Error deleting event");
            }
        } else {
            return ResponseEntity.ok("Students cant delete events");
        }
    }

    @GetMapping("/generateCode")
    public ResponseEntity<String> generateCode(
            @RequestParam(name = "id") String id,
            @RequestParam(name = "time") String time
    ) {
        if (Application.getClientData().getAdmin()) {
            Random random = new Random();
            int code = random.nextInt(999999 - 100000 + 1) + 100000;

            if (manageDB.generateCode(Integer.parseInt(id), code, Integer.parseInt(time))) {
                return ResponseEntity.ok("Code generated successfully");
            } else {
                return ResponseEntity.ok("Error generating code");
            }
        } else {
            return ResponseEntity.ok("Students cant generate event code");
        }
    }

    // CONSULTAR EVENTOS CRIADOS
    @GetMapping("/events")
    public ResponseEntity<String> events(
            @RequestParam(name = "filter" ,required = false, defaultValue = "") String filter
    ) {
        if (Application.getClientData().getAdmin()) {
            ArrayList<ArrayList<String>> listaEventos = manageDB.checkEvents();
            StringBuilder sb = new StringBuilder();

            if (!filter.isEmpty() && !listaEventos.isEmpty()) {
                for(int i = 0; i < listaEventos.size(); i++){
                    if(!(listaEventos.get(i).get(0).toUpperCase().contains(filter.toUpperCase())) &&
                       !(listaEventos.get(i).get(1).toUpperCase().contains(filter.toUpperCase())) &&
                       !(listaEventos.get(i).get(3).toUpperCase().contains(filter.toUpperCase())) &&
                       !(listaEventos.get(i).get(4).toUpperCase().contains(filter.toUpperCase())) &&
                       !(listaEventos.get(i).get(5).toUpperCase().contains(filter.toUpperCase())) &&
                       !(listaEventos.get(i).get(6).toUpperCase().contains(filter.toUpperCase())) &&
                       !(listaEventos.get(i).get(7).toUpperCase().contains(filter.toUpperCase()))){
                        listaEventos.remove(i);
                        --i;
                    }
                }
            }

            if(!listaEventos.isEmpty()) {
                sb.append(String.format("%-3s | %-15s | %-10s | %-15s | %-15s | %-15s | %-15s | %s\n", "ID", "Nome", "Código", "Local", "Data", "Hora de inicio", "Hora de fim", "Número presenças"));
                sb.append("-----------------------------------------------------------------------------------------------------------------------------");
                for (int i = 0; i < listaEventos.size(); i++) {
                    sb.append(String.format("\n%-3s | %-15s | %-10s | %-15s | %-15s | %-15s | %-15s | %s",
                            listaEventos.get(i).get(0), listaEventos.get(i).get(1), listaEventos.get(i).get(2),
                            listaEventos.get(i).get(3), listaEventos.get(i).get(4), listaEventos.get(i).get(5), listaEventos.get(i).get(6), listaEventos.get(i).get(7)));

                }
                sb.append("\n-----------------------------------------------------------------------------------------------------------------------------");

                return ResponseEntity.ok(sb.toString());
            }
            else{
                return ResponseEntity.ok("There are no events registered yet!");
            }
        } else {
            return ResponseEntity.ok("Students cant check created events");
        }
    }


    // CONSULTAR PRESENÇAS DE EU PRÓPRIO
    @GetMapping("/presences")
    public ResponseEntity<String> presences(
            @RequestParam(name = "filter", required = false, defaultValue = "") String filter
    ) {
        if (Application.getClientData().getAdmin()) {
            return ResponseEntity.ok("This endpoint is for students!");

        }
        else {
            ArrayList<ArrayList<String>> listaPresencas = manageDB.checkPresences(Application.getClientData().getEmail());
            StringBuilder sb = new StringBuilder();

            if (!filter.isEmpty() && !listaPresencas.isEmpty()) {
                for(int i = 0; i < listaPresencas.size(); i++){
                    if(!(listaPresencas.get(i).get(0).toUpperCase().contains(filter.toUpperCase())) &&
                            !(listaPresencas.get(i).get(1).toUpperCase().contains(filter.toUpperCase())) &&
                            !(listaPresencas.get(i).get(3).toUpperCase().contains(filter.toUpperCase())) &&
                            !(listaPresencas.get(i).get(4).toUpperCase().contains(filter.toUpperCase())) &&
                            !(listaPresencas.get(i).get(5).toUpperCase().contains(filter.toUpperCase()))){
                        listaPresencas.remove(i);
                        --i;
                    }
                }
            }

            if(!listaPresencas.isEmpty()) {
                sb.append(String.format("%-3s | %-15s | %-15s | %-15s | %-15s | %s\n", "ID", "Nome", "Local", "Data", "Hora de inicio", "Hora de fim"));
                sb.append("-------------------------------------------------------------------------------------------------------");
                for (int i = 0; i < listaPresencas.size(); i++) {
                    sb.append(String.format("\n%-3s | %-15s | %-15s | %-15s | %-15s | %s",
                            listaPresencas.get(i).get(0), listaPresencas.get(i).get(1), listaPresencas.get(i).get(2),
                            listaPresencas.get(i).get(3), listaPresencas.get(i).get(4), listaPresencas.get(i).get(5)));

                }
                sb.append("\n-------------------------------------------------------------------------------------------------------");

                return ResponseEntity.ok(sb.toString());
            }
            else {
                return ResponseEntity.ok("There are no presences registered!");
            }
        }
    }

    @GetMapping("/eventPresences")
    public ResponseEntity<String> eventPresences(
            @RequestParam(name = "id") String id
    ) {
        if (Application.getClientData().getAdmin()) {
            ArrayList<ArrayList<String>> lista = manageDB.checkPresencesEventID(Integer.parseInt(id));
            StringBuilder sb = new StringBuilder();

            if(!lista.isEmpty()) {
                sb.append(String.format("%-15s | %-20s | %s\n","Nome","Email","nIdentificacao"));
                sb.append("-----------------------------------------------------------------------------------------------------------------------------");

                for(int i=0; i<lista.size(); i++){
                    sb.append(String.format("\n%-15s | %-20s | %s",lista.get(i).get(0),lista.get(i).get(1),lista.get(i).get(2)));
                }
                sb.append("\n-----------------------------------------------------------------------------------------------------------------------------");


                return ResponseEntity.ok(sb.toString());
            }
            else {
                return ResponseEntity.ok("There are no presences registered in this event!");
            }

        }
        else {
           return ResponseEntity.ok("The students cant check the presences of the events");
        }
    }
}
