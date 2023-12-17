package com.swsm.dttask.common.generator;

import com.swsm.dttask.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 分布式id生成类
 */
@Slf4j
public class DistributedIdGenerator implements IdGenerator {
    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * 构造方法
     */
    public DistributedIdGenerator(final long workerId) {
        if (workerId <= 15L && workerId >= 0L) {
            this.workerId = workerId;
        } else {
            throw new IllegalArgumentException(String.format(
                    "worker Id can't be greater than %d or less than 0", 15L));
        }
    }

    @Override
    public synchronized Long nextLong() {
        long timestamp = this.timeGen();
        if (this.lastTimestamp == timestamp) {
            this.sequence = this.sequence + 1L & 1023L;
            if (this.sequence == 0L) {
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0L;
        }

        if (timestamp < this.lastTimestamp) {
            try {
                throw new BusinessException(String.format(
                        "Clock moved backwards.  Refusing to generate id for %d milliseconds", 
                        this.lastTimestamp - timestamp));
            } catch (Exception var5) {
                log.error("exception", var5);
            }
        }

        this.lastTimestamp = timestamp;
        return timestamp - 1361753741828L << 14 | this.workerId << 10 | this.sequence;
    }

    @Override
    public String next() {
        return "" + this.nextLong();
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
}
