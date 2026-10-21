import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.HotSpotDiagnosticMXBean.ThreadDumpFormat;

import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * Step 0: produce a REAL JSON thread dump, like `jcmd <pid> Thread.dump_to_file -format=json`.
 * Starts a few platform and virtual threads, then dumps itself to dump.json.
 * Works on Java 21+ (no JSON API needed here: the JDK already WRITES JSON).
 */
public class GenerateDump {
    public static void main(String[] args) throws Exception {
        Path out = Path.of(args.length > 0 ? args[0] : "dump.json").toAbsolutePath();
        Files.deleteIfExists(out); // dumpThreads refuses to overwrite

        var release = new CountDownLatch(1);
        Runnable waitForRelease = () -> {
            try { release.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        };

        // Two named platform threads
        Thread.ofPlatform().name("order-worker-", 1).daemon().start(waitForRelease);
        Thread.ofPlatform().name("order-worker-", 2).daemon().start(waitForRelease);

        // Five virtual threads in an executor (shows up as its own thread container)
        var factory = Thread.ofVirtual().name("payment-", 1).factory();
        try (var executor = Executors.newThreadPerTaskExecutor(factory)) {
            for (int i = 0; i < 5; i++) executor.submit(waitForRelease);
            Thread.sleep(300); // let them all start and block

            ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class)
                    .dumpThreads(out.toString(), ThreadDumpFormat.JSON);
            System.out.println("Thread dump written to " + out);

            release.countDown();
        }
    }
}
