package com.yixsoft.support.mybatis.support.snowflake;

import com.yixsoft.support.mybatis.support.snowflake.ljf.Sequence;
import com.yixsoft.support.mybatis.support.snowflake.ljf.SystemClock;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class StringSequence {
    private static final Logger log = LoggerFactory.getLogger(StringSequence.class);


    /**
     * 5位的机房id
     */
    private final long datacenterIdBits = 32L;
    /**
     * 5位的机器id
     */
    private final long workerIdBits = 32L;
    /**
     * 每毫秒内产生的id数: 2的12次方个
     */
    private final long sequenceBits = 16L;

    protected final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    protected final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 所属机房id
     */
    private final String datacenterId;
    /**
     * 所属机器id
     */
    private final String workerId;
    /**
     * 并发控制序列
     */
    private long sequence = 0L;

    /**
     * 上次生产 ID 时间戳
     */
    private long lastTimestamp = -1L;

    private static volatile InetAddress LOCAL_ADDRESS = null;
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    public StringSequence() {
        long datacenterId = getDatacenterId();
        long workerId = getMaxWorkerId(datacenterId);
        this.workerId = StringUtils.left(workerId + StringUtils.repeat("0", 5), 5);
        this.datacenterId = StringUtils.left(datacenterId + StringUtils.repeat("0", 5), 5);
    }

    /**
     * 有参构造器
     *
     * @param workerId     工作机器 ID
     * @param datacenterId 序列号
     */
    public StringSequence(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("Datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }

        this.workerId = StringUtils.left(workerId + StringUtils.repeat("0", 5), 5);
        this.datacenterId = StringUtils.left(datacenterId + StringUtils.repeat("0", 5), 5);
    }

    /**
     * 基于网卡MAC地址计算余数作为数据中心
     * <br>
     * 可自定扩展
     *
     * @return datacenter ID detected by IP and MAC address
     */
    protected long getDatacenterId() {
        long id = 0L;
        try {
            NetworkInterface network = NetworkInterface.getByInetAddress(getLocalAddress());
            if (null == network) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 2]) | (0x0000FF00 & (((long) mac[mac.length - 1]) << 8))) >> 6;
                    id = id % (maxDatacenterId + 1);
                }
            }
        } catch (Exception e) {
            log.warn(" getDatacenterId: {}", e.getMessage(), e);
        }
        return id;
    }

    /**
     * 基于 MAC + PID 的 hashcode 获取16个低位
     * <p>
     * 可自定扩展
     * </p>
     *
     * @param datacenterId referenced datacenter id
     * @return generated worker id
     */
    protected long getMaxWorkerId(long datacenterId) {
        StringBuilder mpId = new StringBuilder();
        mpId.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name != null && name.length() > 0) {
            // GET jvmPid
            mpId.append(name.split("@")[0]);
        }

        // MAC + PID 的 hashcode 获取16个低位
        return (mpId.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 获取下一个 ID
     *
     * @return next id
     */
    public synchronized String nextId() {
        long timestamp = timeGen();
        // 闰秒
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    // 休眠双倍差值后重新获取，再次校验
                    wait(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
            }
        }

        if (lastTimestamp == timestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 同一毫秒的序列数已经达到最大
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒内，序列号置为 1 - 3 随机数
            sequence = ThreadLocalRandom.current().nextLong(1, 3);
        }

        lastTimestamp = timestamp;

        // 时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + datacenterId + workerId + sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }

        return timestamp;
    }

    protected long timeGen() {
        return SystemClock.INSTANCE.currentTimeMillis();
    }

    /**
     * Find first valid IP from local network card
     *
     * @return first valid local IP
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }

        LOCAL_ADDRESS = getLocalAddress0();
        return LOCAL_ADDRESS;
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            log.warn("Failed to retrieving ip address, {}", e.getMessage(), e);
        }

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            try {
                                InetAddress address = addresses.nextElement();
                                if (isValidAddress(address)) {
                                    return address;
                                }
                            } catch (Throwable e) {
                                log.warn("Failed to retrieving ip address, {}", e.getMessage(), e);
                            }
                        }
                    } catch (Throwable e) {
                        log.warn("Failed to retrieving ip address, {}", e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            log.warn("Failed to retrieving ip address, {}", e.getMessage(), e);
        }

        log.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }

        String name = address.getHostAddress();
        return (name != null && !"0.0.0.0".equals(name) && !"127.0.0.1".equals(name) && IP_PATTERN.matcher(name).matches());
    }
}
