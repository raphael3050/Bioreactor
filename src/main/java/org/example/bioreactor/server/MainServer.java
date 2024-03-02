package org.example.bioreactor.server;

import org.example.bioreactor.server.fileParser.FileParser;
import org.example.bioreactor.server.tcpServer.TCPServer;

import java.io.IOException;

public class MainServer {

    public static void main(String[] args) throws IOException {

        // TODO : Change the filename location
        String filename = "2022-10-03-Act2-1.xlsx";

        TCPServer aServer = new TCPServer(6666, filename);
        System.out.println("----------------- Démarrage du serveur -----------------");
        aServer.start();

    }
}
