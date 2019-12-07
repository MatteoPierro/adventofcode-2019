package it.matteopierro.computer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Arrays.asList;

public class ComputerListener {

    private final BlockingQueue<String> inputs;

    public ComputerListener(String[] inputs) {
        this.inputs = new LinkedBlockingQueue<>();
        asList(inputs).forEach(input -> {
            try {
                this.inputs.put(input);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public String onInputRequested() {
        try {
            return inputs.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void addResult(String result) {
        throw new UnsupportedOperationException();
    }
}
