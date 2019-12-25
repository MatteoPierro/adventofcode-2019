package it.matteopierro.cryostasis;

import it.matteopierro.computer.Computer;
import it.matteopierro.computer.ComputerListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Cryostasis extends ComputerListener {

    private final BlockingQueue<String> inputs = new LinkedBlockingQueue<>();

    @Override
    public String onReadRequested() {
        try {
            if (inputs.isEmpty()) {
                System.out.println("Input:");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String command = reader.readLine();
                for (char c : command.toCharArray()) {
                    inputs.add(String.valueOf((int) c));
                }
                inputs.add("10");
            }

            return inputs.take();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStoreRequested(String result) {
        super.onStoreRequested(result);
        System.out.print((char) Integer.parseInt(result));
    }

    public static void main(String[] args) throws IOException {
        System.out.println("hello!");
        String program = Files.readString(Paths.get("./input_day25"));
        new Computer().execute(program, new Cryostasis());
    }
}
