package massim.javaagents;

import redis.clients.jedis.Jedis;

/**
 * Created by alireza on 1/22/18.
 */
public class Test {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        System.out.println((char)27 + "\u001B[36m "+"SCHEDULER STEP " + value + (char)27 + "[0m");
        System.out.println("".isEmpty());
        System.out.println(jedis.get("agent:sub:number"));


    }
}
