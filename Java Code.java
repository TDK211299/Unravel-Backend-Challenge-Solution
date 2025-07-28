/*
 * Unravel App Backend Developer Challenge - Complete Java Solution
 * Author: Aryan Jain
 * Description: Solves session management, memory leaks, producer-consumer with priority, deadlocks, and HikariCP optimization.
 */

// ========== PART 1: THREAD-SAFE SESSION MANAGEMENT ==========

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class SessionManager {
    private final ConcurrentHashMap<String, String> sessions = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public String login(String userId) {
        lock.lock();
        try {
            if (sessions.containsKey(userId)) {
                return "User already logged in.";
            }
            sessions.put(userId, "SESSION_" + UUID.randomUUID());
            return "Login successful. Session ID: " + sessions.get(userId);
        } finally {
            lock.unlock();
        }
    }

    public String logout(String userId) {
        lock.lock();
        try {
            if (!sessions.containsKey(userId)) {
                return "User not logged in.";
            }
            sessions.remove(userId);
            return "Logout successful.";
        } finally {
            lock.unlock();
        }
    }

    public String getSessionDetails(String userId) {
        if (!sessions.containsKey(userId)) {
            throw new RuntimeException("Session not found for user " + userId);
        }
        return "Session ID for user " + userId + ": " + sessions.get(userId);
    }
}


// ========== PART 2: MEMORY MANAGEMENT & CACHING ==========

import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryManager {
    private static final int MAX_SESSIONS = 100;
    private static final Map<String, byte[]> largeSessionData = new LinkedHashMap<String, byte[]>(MAX_SESSIONS, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, byte[]> eldest) {
            return size() > MAX_SESSIONS;
        }
    };

    public static void addSessionData(String sessionId) {
        largeSessionData.put(sessionId, new byte[10 * 1024 * 1024]); // 10MB per session
    }

    public static void removeSessionData(String sessionId) {
        largeSessionData.remove(sessionId);
    }
}


// ========== PART 3: PRODUCER-CONSUMER WITH PRIORITY ==========

import java.util.concurrent.PriorityBlockingQueue;

class Task implements Comparable<Task> {
    String description;
    int priority; // lower = higher priority

    Task(String description, int priority) {
        this.description = description;
        this.priority = priority;
    }

    @Override
    public int compareTo(Task o) {
        return Integer.compare(this.priority, o.priority);
    }
}

class LogProcessor {
    private final PriorityBlockingQueue<Task> taskQueue = new PriorityBlockingQueue<>();

    public void produce(Task task) {
        taskQueue.put(task);
    }

    public Task consume() throws InterruptedException {
        return taskQueue.take();
    }
}

class Producer extends Thread {
    private final LogProcessor processor;

    public Producer(LogProcessor processor) {
        this.processor = processor;
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            processor.produce(new Task("Log " + i, i % 5));
        }
    }
}

class Consumer extends Thread {
    private final LogProcessor processor;

    public Consumer(LogProcessor processor) {
        this.processor = processor;
    }

    public void run() {
        try {
            while (true) {
                Task task = processor.consume();
                System.out.println("Consumed: " + task.description + " with priority " + task.priority);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class LogProcessingApp {
    public static void main(String[] args) {
        LogProcessor processor = new LogProcessor();
        new Producer(processor).start();
        new Consumer(processor).start();
    }
}


// ========== PART 4: DEADLOCK-FREE IMPLEMENTATION ==========

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockFreeSimulator {
    private final Lock lock1 = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();

    public void method1() {
        try {
            if (lock1.tryLock()) {
                Thread.sleep(10);
                if (lock2.tryLock()) {
                    try {
                        System.out.println("Method1: Acquired lock1 and lock2");
                    } finally {
                        lock2.unlock();
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock1.tryLock()) lock1.unlock();
        }
    }

    public void method2() {
        try {
            if (lock1.tryLock()) {
                Thread.sleep(10);
                if (lock2.tryLock()) {
                    try {
                        System.out.println("Method2: Acquired lock1 and lock2");
                    } finally {
                        lock2.unlock();
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock1.tryLock()) lock1.unlock();
        }
    }

    public static void main(String[] args) {
        DeadlockFreeSimulator sim = new DeadlockFreeSimulator();
        new Thread(sim::method1).start();
        new Thread(sim::method2).start();
    }
}


// ========== PART 5: HIKARICP MONITORING ==========

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class HikariCPConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
        config.setUsername("dbuser");
        config.setPassword("dbpassword");

        config.setMaximumPoolSize(50);
        config.setMinimumIdle(10);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(30000);

        HikariDataSource ds = new HikariDataSource(config);

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                    System.out.println("Active: " + ds.getHikariPoolMXBean().getActiveConnections());
                    System.out.println("Idle: " + ds.getHikariPoolMXBean().getIdleConnections());
                    System.out.println("Waiting: " + ds.getHikariPoolMXBean().getThreadsAwaitingConnection());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();

        return ds;
    }
}
