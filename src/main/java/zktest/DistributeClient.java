package zktest;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * ClassName: DistributeClient
 * Description:
 * date: 2020/7/9 16:54
 *
 * @author CFG
 * @since JDK 1.8
 */
public class DistributeClient {

    public static void main(String[] args) throws Exception {
        DistributeClient distributeClient = new DistributeClient();

        distributeClient.getConnect();

        distributeClient.getChildren();

        distributeClient.business();


    }

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1); // 线程控制

    // 不能有空格
    private String connectionInfo = "192.168.125.130:2181,192.168.125.131:2181,192.168.125.132:2181";

    private int sessionTimeout = 2000;

    ZooKeeper zooKeeper;

    private void getConnect() throws IOException {
        zooKeeper = new ZooKeeper(connectionInfo, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                    connectedSemaphore.countDown();
                }

                try {
                    getChildren();
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getChildren() throws KeeperException, InterruptedException {

        connectedSemaphore.await();

        List<String> children = zooKeeper.getChildren("/servers", true);

        List<String> host = new ArrayList<String>();

        for (String child : children) {
            byte[] data = zooKeeper.getData("/servers/" + child, false, null);

            host.add(new String(data));
        }

        System.out.println(host);
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

}
