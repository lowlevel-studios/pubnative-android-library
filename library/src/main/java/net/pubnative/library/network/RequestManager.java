package net.pubnative.library.network;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jaiswal.anshuman on 2/2/2016.
 */
public class RequestManager {

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 10;

    // A queue of Runnables for requests pool
    private final BlockingQueue<Runnable> mRequestWorkQueue;

    // A managed pool of background send stat threads
    private final ThreadPoolExecutor mRequestThreadPool;

    // A queue of stat tasks. Tasks are handed to a ThreadPool.
    private final Queue<RequestTask> mRequestTaskWorkQueue;

    // A single instance of RequestManager, used to implement the singleton pattern
    private static RequestManager sInstance = null;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {

        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "Request #" + mCount.getAndIncrement());
        }
    };

    // A static block that sets class fields
    static {

        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        // Creates a single static instance of RequestManager
        sInstance = new RequestManager();
    }

    /**
     * Constructs the work queues and thread pools used to send statistic.
     */
    private RequestManager() {
		/*
         * Creates a work queue for the pool of Thread objects used for executing requests, using a linked
         * list queue that blocks when the queue is empty.
         */
        mRequestWorkQueue = new LinkedBlockingQueue<Runnable>();

		/*
         * Creates a work queue for the set of of task objects that control executing requests,
         * using a linked list queue that blocks when the queue is empty.
         */
        mRequestTaskWorkQueue = new LinkedBlockingQueue<RequestTask>();

		/*
         * Creates a new pool of Thread objects for executing requests queue
         */
        mRequestThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mRequestWorkQueue, sThreadFactory);
    }

    /**
     * Returns the RequestManager object
     * @return The global RequestManager object
     */
    public static RequestManager getInstance() {

        return sInstance;
    }

    /**
     * Starts a request
     */
    public static void sendRequest(Request request) {
    	/*
         * Gets a task from the pool of tasks, returning null if the pool is empty
         */
        RequestTask requestTask = sInstance.mRequestTaskWorkQueue.poll();

        // If the queue was empty, create a new task instead.
        if (null == requestTask) {
            requestTask = new RequestTask();
        }
        requestTask.setRequest(request);
        sInstance.mRequestThreadPool.execute(requestTask.getExecuteRequestRunnable());
    }

    /**
     * Recycles tasks by calling their internal recycle() method and then putting them back into
     * the task queue.
     * @param requestTask The task to recycle
     */
    public void recycleTask(RequestTask requestTask) {

        // Puts the task object back into the queue for re-use.
        mRequestTaskWorkQueue.offer(requestTask);
    }
}
