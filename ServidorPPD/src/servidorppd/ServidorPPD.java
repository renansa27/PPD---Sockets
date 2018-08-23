package servidorppd;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class ServidorPPD {

    private int port;
    private List<PrintStream> clients;
    private ServerSocket server;

    public static void main(String[] args) throws IOException {
        String porta = JOptionPane.showInputDialog("Porta: ");
        //int portaServidor = Integer.parseInt(porta);
        
        new ServidorPPD(Integer.parseInt(porta)).run();
    }

    public ServidorPPD(int port) {
        this.port = port;
        this.clients = new ArrayList<PrintStream>();
    }

    public void run() throws IOException {
        server = new ServerSocket(port) {
            protected void finalize() throws IOException {
                this.close();
            }
        };
        System.out.println("Port "+port+" está aberta.");

        while (true) {
            // aceita um novo cliente
            Socket client = server.accept();
            System.out.println("Conexão estabelecida com cliente: " + client.getInetAddress().getHostAddress());

            // adiciona um cliente na lista de crientes
            this.clients.add(new PrintStream(client.getOutputStream()));

            //Seta as cores para os clientes conectados
            setCor();

            new Thread(new ClientHandler(this, client.getInputStream())).start();
        }
    }

    private void setCor() {
        if (clients.size() == 2) {
            String msg = "cor;azul";
            this.clients.get(0).println(msg);
        }
    }

    void enviaParaTodos(String msg) {
        this.clients.forEach((client) -> {
            client.println(msg);
        });
    }
}

class ClientHandler implements Runnable {

    private ServidorPPD server;
    private InputStream client;

    public ClientHandler(ServidorPPD server, InputStream client) {
        this.server = server;
        this.client = client;
    }
    
    @Override
    public void run() {
        String message;
        try ( // quando recebe uma nova msg, manda para todos os outros
                Scanner sc = new Scanner(this.client)) {
            while (sc.hasNextLine()) {
                message = sc.nextLine();
                server.enviaParaTodos(message);
            }
        }
    }
}