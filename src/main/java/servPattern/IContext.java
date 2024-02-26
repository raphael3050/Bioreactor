package servPattern;

import servPattern.measures.Measures;
import servPattern.serverException.EndOfSimulationException;
import servPattern.serverException.StartOfSimulationException;

import java.io.IOException;
import java.net.Socket;

public interface IContext {

    void play(Socket clientSocket, int delayMS) throws EndOfSimulationException, IOException;
    void pause();
    void stop();
    Measures goForward() throws EndOfSimulationException;
    Measures goBackwards() throws StartOfSimulationException;

}
