package pt.isec.pd.a2020136093.client.ui.text;


import pt.isec.pd.a2020136093.client.communication.ManageConnections;
import pt.isec.pd.a2020136093.client.data.ClientData;
import pt.isec.pd.a2020136093.client.utils.PAInput;
import pt.isec.pd.a2020136093.utils.ENDPOINTS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import static pt.isec.pd.a2020136093.server.rest_api.consumer.PdRestApiComSegurancaConsumer.sendRequestAndShowResponse;

public class ClientUI {
    ClientData clientData;
    ManageConnections manageConnections = new ManageConnections();

    public ClientUI() {
        this.clientData = new ClientData();
    }

    public void start() {
        int option;
        do {
            option = PAInput.chooseOption("Escolha uma opcao:", "Login", "Registar", "Sair");

            switch (option) {
                case 1 -> {
                    login();
                }
                case 2 -> {
                    try {
                        register();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 3 -> {
                    exit();
                }
            }

            while (clientData.getToken() != null && !clientData.getAdmin()) {
                option = PAInput.chooseOption("Escolha uma opcao:", "Submeter codigo de presenca", "Consultar presencas", "Logout");

                switch (option) {
                    case 1 -> {
                        submeterCodigo();
                    }
                    case 2 -> {
                        consultarPresencas();
                    }
                    case 3 -> {
                        logout();
                    }
                }
            }

            while(clientData.getToken() != null && clientData.getAdmin()){
                option = PAInput.chooseOption("Escolha uma opcao:", "Criar novo evento", "Eliminar evento", "Consultar eventos", "Gerar codigo para evento", "Consultar presencas em evento", "Logout");
                switch(option){
                    case 1-> {
                        criarEvento();
                    }
                    case 2-> {
                        eliminarEvento();
                    }
                    case 3-> {
                        consultarEventos();
                    }
                    case 4-> {
                        gerarCodigoEvento();
                    }
                    case 5 -> {
                        consultarPresencasEvento();
                    }
                    case 6 -> {
                        logout();
                    }
                }

            }

        } while (true);
    }


    private void login() {
        String email = PAInput.readString("Email: ", true);
        String password = PAInput.readString("Password: ", true);

        String credentials = Base64.getEncoder().encodeToString((email+":"+password).getBytes());
        try {
            clientData.setToken(sendRequestAndShowResponse(ENDPOINTS.loginURI, "POST","basic "+ credentials, null)); //Base64(admin:admin) YWRtaW46YWRtaW4=
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(clientData.getToken() != null) {
            try {
                if (sendRequestAndShowResponse(ENDPOINTS.isAdminURI, "GET", "bearer " + clientData.getToken(), null).equals("Current user is admin!"))
                    clientData.setAdmin(true);
                else
                    clientData.setAdmin(false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void register() throws IOException {
        String username = PAInput.readString("Nome: ", false);
        String email = PAInput.readString("Email: ", true);
        String password = PAInput.readString("Password: ", true);
        String nIdentificacao = PAInput.readString("Numero de Identificacao: ", true);

        sendRequestAndShowResponse(ENDPOINTS.registerURI+"?name="+username+"&email="+email+"&password="+password+"&nid="+nIdentificacao, "GET", null, null);
    }

    private void logout(){
        clientData = new ClientData();
    }


    // ADMIN
    private void criarEvento() {
        String name = PAInput.readString("Nome do evento: ", false);
        String local = PAInput.readString("Local do evento: ", false);
        String date = PAInput.readString("Data do evento [DD-MM-YYYY]: ", false);
        String timeStart = PAInput.readString("Hora de inicio do evento [HH:MM]: ", false);
        String timeEnd = PAInput.readString("Hora de fim do evento [HH:MM]: ", false);

        try {
            sendRequestAndShowResponse(ENDPOINTS.createEventURI+"?name="+name+"&local="+local+"&dataR="+date+"&horaI="+timeStart+"&horaF="+timeEnd, "GET", "bearer " + clientData.getToken(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void eliminarEvento() {
        String idEvento = PAInput.readString("ID do evento: ", true);

        try {
            sendRequestAndShowResponse(ENDPOINTS.deleteEventURI+"?id="+idEvento, "GET", "bearer " + clientData.getToken(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void consultarEventos() {
        String filter = "";
        filter = PAInput.readString("Filtro: ", false, true);

        try{
            if(filter.isEmpty())
                sendRequestAndShowResponse(ENDPOINTS.checkEventsURI, "GET", "bearer " + clientData.getToken(), null);
            else
                sendRequestAndShowResponse(ENDPOINTS.checkEventsURI+"?filter="+filter, "GET", "bearer " + clientData.getToken(), null);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void gerarCodigoEvento() {
        String idEvento = PAInput.readString("ID do evento: ", true);
        String timeEvento = PAInput.readString("Tempo de duração do código (em minutos): ", true);

        try {
            sendRequestAndShowResponse(ENDPOINTS.generateCodeURI+"?id="+idEvento+"&time="+timeEvento, "GET", "bearer " + clientData.getToken(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void consultarPresencasEvento() {
        String idEvento = PAInput.readString("ID do evento: ", true);

        try {
            sendRequestAndShowResponse(ENDPOINTS.checkEventPresencesURI+"?id="+idEvento, "GET", "bearer " + clientData.getToken(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    

    
    // ALUNO
    private void submeterCodigo() {
        String codigo = PAInput.readString("Codigo: ", true);

        try {
            sendRequestAndShowResponse(ENDPOINTS.submitCodeURI+"?code="+codigo, "GET", "bearer " + clientData.getToken(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void consultarPresencas() {
        try {
            sendRequestAndShowResponse(ENDPOINTS.ownStudentPresences, "GET", "bearer " + clientData.getToken(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    private void exit() {
        System.out.println("A sair...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


}
