package org.example.bioreactor.server.tank;

import org.example.bioreactor.server.IContext;
import org.example.bioreactor.server.measures.Measures;
import org.example.bioreactor.server.serverException.EndOfSimulationException;
import org.example.bioreactor.server.serverException.StartOfSimulationException;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Tank implements IContext {
    private List<Measures> measure;

    private Measures lastChange;

    @Override
    public void play(Socket clientSocket, int delayMS) throws EndOfSimulationException, IOException {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Measures goForward() throws EndOfSimulationException {
        return null;
    }

    @Override
    public Measures goBackwards() throws StartOfSimulationException {
        return null;
    }
}
