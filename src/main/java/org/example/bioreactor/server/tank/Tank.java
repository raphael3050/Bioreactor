package org.example.bioreactor.server.tank;

import org.example.bioreactor.server.IContext;
import org.example.bioreactor.server.measures.Measures;
import org.example.bioreactor.server.updated.Updated;

import java.util.List;

public class Tank implements IContext {
    private List<Measures> measure;
    private Updated lastChange;


}
