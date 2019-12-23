package it.matteopierro;

import it.matteopierro.computer.Computer;
import it.matteopierro.computer.ComputerListener;
import org.jooq.lambda.Seq;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;

class CategorySixTest {

    @Test
    void shouldSendPackets() {
        var sw = new Switch();
        var nic1 = new NetworkInterface(0, sw);
        sw.attach(0, nic1);
        assertThat(nic1.onReadRequested()).isEqualTo("0");
        assertThat(nic1.onReadRequested()).isEqualTo("-1");
        var packet = new Packet("0", "2", "7");
        sw.route(packet);
        assertThat(nic1.onReadRequested()).isEqualTo("2");
        assertThat(nic1.onReadRequested()).isEqualTo("7");
        assertThat(nic1.onReadRequested()).isEqualTo("-1");
    }

    @Test
    void networkWithTwoNics() {
        var sw = new Switch();
        var nic1 = new NetworkInterface(0, sw);
        var nic2 = new NetworkInterface(1, sw);
        sw.attach(0, nic1);
        sw.attach(1, nic2);
        assertThat(nic1.onReadRequested()).isEqualTo("0");
        assertThat(nic1.onReadRequested()).isEqualTo("-1");
        assertThat(nic2.onReadRequested()).isEqualTo("1");
        assertThat(nic2.onReadRequested()).isEqualTo("-1");
        nic1.onStoreRequested("1");
        nic1.onStoreRequested("6");
        nic1.onStoreRequested("9");
        nic1.onStoreRequested("1");
        nic1.onStoreRequested("7");
        nic1.onStoreRequested("5");
        assertThat(nic2.onReadRequested()).isEqualTo("6");
        assertThat(nic2.onReadRequested()).isEqualTo("9");
        assertThat(nic2.onReadRequested()).isEqualTo("7");
        assertThat(nic2.onReadRequested()).isEqualTo("5");
    }

    @Test
    void switchBufferResults() {
        var sw = new Switch();
        var packet = new Packet("0", "8", "5");
        sw.route(packet);
        var nic1 = new NetworkInterface(0, sw);
        sw.attach(0, nic1);
        assertThat(nic1.onReadRequested()).isEqualTo("0");
        assertThat(nic1.onReadRequested()).isEqualTo("8");
        assertThat(nic1.onReadRequested()).isEqualTo("5");
        assertThat(nic1.onReadRequested()).isEqualTo("-1");
    }

    @Test
    void shouldReturnPacketWithAddress255() throws InterruptedException {
        var result = new LinkedBlockingQueue<Packet>();
        var sw = new Switch(result);

        new Thread(() -> {
            var packet = new Packet("255", "8", "4");
            sw.route(packet);
        }).start();

        Packet packet = result.take();
        assertThat(packet.y).isEqualTo("4");
    }

    @Test
    void firstPuzzle() throws Exception {
        String program = Files.readString(Paths.get("./input_day23"));
        var result = new LinkedBlockingQueue<Packet>();
        var sw = new Switch(result);

        List<NetworkInterface> nics = Seq.range(0, 50)
                .map(address -> {
                    var nic = new NetworkInterface(address, sw);
                    sw.attach(address, nic);
                    return nic;
                }).toList();

        nics.forEach( nic -> {
            new Thread(() -> new Computer().execute(program, nic)).start();
        });

        Packet packet = result.take();
        assertThat(packet.y).isEqualTo("4");
    }

    private static class NetworkInterface extends ComputerListener {
        private String identifier;
        private final Switch sw;
        private Queue<Packet> packetQueue = new LinkedList<>();
        private List<String> currentPacketToSent = new ArrayList<>();
        private List<String> currentPacketToReceive = new ArrayList<>();

        public NetworkInterface(int identifier, Switch sw) {
            this.identifier = String.valueOf(identifier);
            this.sw = sw;
        }

        public void addPacket(Packet packet) {
            packetQueue.add(packet);
        }

        @Override
        public String onReadRequested() {
            if (identifier != null) {
                var id = identifier;
                identifier = null;
                return id;
            }
            if (!currentPacketToSent.isEmpty()) {
                return currentPacketToSent.remove(0);
            }
            if (packetQueue.isEmpty()) {
                return "-1";
            }
            Packet packet = packetQueue.poll();
            currentPacketToSent.add(packet.x);
            currentPacketToSent.add(packet.y);
            return currentPacketToSent.remove(0);
        }

        @Override
        public void onStoreRequested(String result) {
            currentPacketToReceive.add(result);
            if (currentPacketToReceive.size() == 3) {
                var address = currentPacketToReceive.remove(0);
                var x = currentPacketToReceive.remove(0);
                var y = currentPacketToReceive.remove(0);
                var packet = new Packet(address, x, y);
                sw.route(packet);
            }
        }
    }

    private static class Packet {
        public final String address;
        public final String x;
        public final String y;

        private Packet(String address, String x, String y) {
            this.address = address;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Packet{" +
                    "address='" + address + '\'' +
                    ", x='" + x + '\'' +
                    ", y='" + y + '\'' +
                    '}';
        }
    }

    private static class Switch {
        private final LinkedBlockingQueue<Packet> result;
        private Map<String, NetworkInterface> nics = new HashMap<>();
        private Map<String, List<Packet>> buffers = new HashMap<>();

        public Switch() {
            result = new LinkedBlockingQueue<>();
        }

        public Switch(LinkedBlockingQueue<Packet> result) {
            this.result = result;
        }

        public synchronized void route(Packet packet) {
            var address = packet.address;
            if (address.equals("255")) {
                result.add(packet);
                return;
            }
            if (nics.containsKey(address)) {
                nics.get(address).addPacket(packet);
            } else {
                List<Packet> queue = buffers.getOrDefault(address, new ArrayList<>());
                queue.add(packet);
                buffers.put(address, queue);
            }
        }

        public void attach(int address, NetworkInterface nic) {
            nics.put(String.valueOf(address), nic);
            List<Packet> buffer = buffers.getOrDefault(String.valueOf(address), new ArrayList<>());
            buffer.forEach(nic::addPacket);
            buffer.clear();
        }
    }
}
