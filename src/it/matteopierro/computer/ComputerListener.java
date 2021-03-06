package it.matteopierro.computer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Arrays.asList;

public class ComputerListener {

    private final BlockingQueue<String> inputs;
    private final List<String> results = new LinkedList<>();

    public ComputerListener(Integer... inputs) {
        this.inputs = new LinkedBlockingQueue<>();
        asList(inputs).forEach(input -> {
            try {
                this.inputs.put(String.valueOf(input));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

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

    public ComputerListener(List<String> input) {
        this.inputs = new LinkedBlockingQueue<>(input);
    }

    public String onReadRequested() {
        try {
            return inputs.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void onStoreRequested(String result) {
        this.results.add(result);
    }

    public List<String> results() {
        return results;
    }

    public void addInput(String result) {
        inputs.add(result);
    }
}
