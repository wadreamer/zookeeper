package zktest;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * ClassName: test
 * Description:
 * date: 2020/7/9 15:50
 *
 * @author CFG
 * @since JDK 1.8
 */
public class TestZookeeper {

    // 线程控制
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    // 不能有空格，集群中所有的 zookeeper 的 地址:端口，以逗号分隔
    private String connectionInfo = "192.168.125.130:2181,192.168.125.131:2181,192.168.125.132:2181";

    // session 超时时间
    private int sessionTimeout = 2000;

    private ZooKeeper client;

    // 连接服务端
    @Before
    public void init() throws IOException {
        client = new ZooKeeper(connectionInfo, sessionTimeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {

                if(Event.KeeperState.SyncConnected == watchedEvent.getState()){
                    connectedSemaphore.countDown();
                }

                // System.out.println("===================START=====================");
                // List<String> children = null;
                // try {
                //     children = client.getChildren("/", true);
                //     for(String child : children){
                //         System.out.println(child);
                //     }
                // } catch (KeeperException e) {
                //     e.printStackTrace();
                // } catch (InterruptedException e) {
                //     e.printStackTrace();
                // }
                // System.out.println("===================END=====================");
            }
        });
    }

    // 创建节点
    @Test
    public void createNode() throws KeeperException, InterruptedException {
        connectedSemaphore.await();

        String path = client.create("/atsz", "wadreamer".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        System.out.println(path);
    }

    // 获取节点数据并监听
    @Test
    public void getDataAndWatch() throws KeeperException, InterruptedException {
        connectedSemaphore.await();

        List<String> children = client.getChildren("/", true);

        for(String child : children){
            System.out.println(child);
        }

        Thread.sleep(Long.MAX_VALUE);
    }

    // 判断节点节点是否存在
    @Test
    public void exist() throws KeeperException, InterruptedException {
        connectedSemaphore.await();

        Stat stat = client.exists("/atsz", false);
        System.out.println(stat == null ? "not exit" : "exist");
    }

}
