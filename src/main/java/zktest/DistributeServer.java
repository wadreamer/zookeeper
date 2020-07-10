package zktest;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * ClassName: DistributeServer
 * Description:
 * date: 2020/7/9 16:45
 *
 * @author CFG
 * @since JDK 1.8
 */
public class DistributeServer {

    public static void main(String[] args) throws Exception {

        DistributeServer server = new DistributeServer();

        // 连接 zookeeper 集群
        server.getConnect();

        // 将自己的信息注册到集群
        server.register("wadreamer");

        // 业务逻辑处理
        server.bussiness();

    }

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1); // 线程控制

    // 不能有空格
    private String connectionInfo = "192.168.125.130:2181,192.168.125.131:2181,192.168.125.132:2181";

    private int sessionTimeout = 2000;

    ZooKeeper zooKeeper;

    private void getConnect() throws IOException {
        zooKeeper = new ZooKeeper(connectionInfo, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                if(Event.KeeperState.SyncConnected == watchedEvent.getState()){
                    connectedSemaphore.countDown();
                }
            }
        });
    }

    private void register(String hostName) throws KeeperException, InterruptedException {
        connectedSemaphore.await();

        String path =zooKeeper.create("/servers/server", hostName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        System.out.println(path + "is onLine");
    }

    private void bussiness() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

}
