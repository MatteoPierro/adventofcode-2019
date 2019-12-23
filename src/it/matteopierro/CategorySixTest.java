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
        var sw = new Switch();
        var nat = new Nat(result);
        sw.attachNat(nat);

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
        var sw = new Switch();
        var nat = new Nat(result);
        sw.attachNat(nat);

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
        assertThat(packet.y).isEqualTo("26163");
    }

    @Test
    void secondPuzzle() throws Exception {
        String program = Files.readString(Paths.get("./input_day23"));
        var sw = new Switch();
        var nat = new Nat();
        sw.attachNat(nat);

        List<NetworkInterface> nics = Seq.range(0, 50)
                .map(address -> {
                    var nic = new NetworkInterface(address, sw);
                    sw.attach(address, nic);
                    return nic;
                }).toList();

        nics.forEach( nic -> {
            new Thread(() -> new Computer().execute(program, nic)).start();
        });

        String y = nat.twoTimesSentY.take();
        assertThat(y).isEqualTo("26163");
    }

    private static class NetworkInterface extends ComputerListener {
        private final String id;
        private String identifier;
        private final Switch sw;
        private Queue<Packet> packetQueue = new LinkedList<>();
        private List<String> currentPacketToSent = new ArrayList<>();
        private List<String> currentPacketToReceive = new ArrayList<>();

        public NetworkInterface(int identifier, Switch sw) {
            this.id = String.valueOf(identifier);
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
                sw.notEmpty(id);
                return id;
            }
            if (!currentPacketToSent.isEmpty()) {
                sw.notEmpty(id);
                return currentPacketToSent.remove(0);
            }
            if (packetQueue.isEmpty()) {
                sw.empty(id);
                return "-1";
            }
            sw.notEmpty(id);
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
        private Nat nat = new Nat();
        private Map<String, NetworkInterface> nics = new HashMap<>();
        private Map<String, List<Packet>> buffers = new HashMap<>();
        private Map<String, Boolean> idles = new HashMap<>();

        public synchronized void route(Packet packet) {
            var address = packet.address;
            if (address.equals("255")) {
                nat.send(packet);
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
            idles.put(String.valueOf(address), false);
            nics.put(String.valueOf(address), nic);
            List<Packet> buffer = buffers.getOrDefault(String.valueOf(address), new ArrayList<>());
            buffer.forEach(nic::addPacket);
            buffer.clear();
        }

        public void attachNat(Nat nat) {
            this.nat = nat;
        }

        public synchronized void empty(String identifier) {
            idles.put(identifier, true);
            if (idles.entrySet().stream().allMatch(Map.Entry::getValue)) {
                var nic = nics.get("0");
                nat.sendPacketTo(nic);
            }
        }

        public void notEmpty(String identifier) {
            idles.put(identifier, false);
        }
    }

    private static class Nat {
        private final LinkedBlockingQueue<Packet> firstReceivedPacket;
        private final LinkedBlockingQueue<String> twoTimesSentY = new LinkedBlockingQueue<>();
        private Packet lastReceivedPacket;
        private String lastY;

        public Nat() {
            this(new LinkedBlockingQueue<>());
        }

        public Nat(LinkedBlockingQueue<Packet> firstReceivedPacket) {
            this.firstReceivedPacket = firstReceivedPacket;
        }

        public synchronized void send(Packet packet) {
            this.firstReceivedPacket.add(packet);
            this.lastReceivedPacket = packet;
        }

        public synchronized void sendPacketTo(NetworkInterface nic) {
            if (lastReceivedPacket == null) {
                return;
            }
            if (lastReceivedPacket.y.equals(lastY)) {
                twoTimesSentY.add(lastY);
            }
            lastY = lastReceivedPacket.y;
            nic.addPacket(lastReceivedPacket);
            lastReceivedPacket = null;
        }
    }
}
