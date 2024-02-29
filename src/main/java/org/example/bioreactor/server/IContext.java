package org.example.bioreactor.server;

import org.example.bioreactor.server.measures.Measures;
import org.example.bioreactor.server.serverException.EndOfSimulationException;
import org.example.bioreactor.server.serverException.StartOfSimulationException;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.Socket;

public interface IContext {

    void play(Socket clientSocket, int delayMS) throws EndOfSimulationException, IOException;
    void pause();
    void stop();
    Measures goForward() throws EndOfSimulationException;
    Measures goBackwards() throws StartOfSimulationException;
    PropertyChangeSupport getPropertyChangeSupport();

}
