package net.pubnative.library.network;

import android.os.Handler;
import android.os.Looper;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PubnativeAPIRequestManager {

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    // A queue of Runnables for requests pool
    private final BlockingQueue<Runnable> mRequestWorkQueue;

    // A managed pool of background request threads
    private final ThreadPoolExecutor mRequestThreadPool;

    // A queue of request tasks.
    private final Queue<PubnativeAPIRequestTask> mPubnativeAPIRequestTaskWorkQueue;

    // A single instance of PubnativeAPIRequestManager, used to implement the singleton pattern
    private static PubnativeAPIRequestManager sInstance = null;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {

        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "PubnativeAPIRequest #" + mCount.getAndIncrement());
        }
    };

    // A static block that sets class fields
    static {

        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        // Creates a single static instance of PubnativeAPIRequestManager
        sInstance = new PubnativeAPIRequestManager();
    }

    /**
     * Constructs the work queues and thread pools used to execute request.
     */
    private PubnativeAPIRequestManager() {
		/*
         * Creates a work queue for the pool of Thread objects used for executing requests, using a linked
         * list queue that blocks when the queue is empty.
         */
        mRequestWorkQueue = new LinkedBlockingQueue<Runnable>();

		/*
         * Creates a work queue for the set of of task objects that control executing requests,
         * using a linked list queue that blocks when the queue is empty.
         */
        mPubnativeAPIRequestTaskWorkQueue = new LinkedBlockingQueue<PubnativeAPIRequestTask>();

		/*
         * Creates a new pool of Thread objects for executing requests queue
         */
        mRequestThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mRequestWorkQueue, sThreadFactory);
    }

    /**
     * Returns the PubnativeAPIRequestManager object
     * @return The global PubnativeAPIRequestManager object
     */
    public static PubnativeAPIRequestManager getInstance() {

        return sInstance;
    }

    /**
     * Starts a pubnativeAPIRequest
     */
    public static void sendRequest(PubnativeAPIRequest pubnativeAPIRequest) {
    	/*
         * Gets a task from the pool of tasks, returning null if the pool is empty
         */
        PubnativeAPIRequestTask pubnativeAPIRequestTask = sInstance.mPubnativeAPIRequestTaskWorkQueue.poll();

        // If the queue was empty, create a new task instead.
        if (null == pubnativeAPIRequestTask) {
            pubnativeAPIRequestTask = new PubnativeAPIRequestTask();
        }
        pubnativeAPIRequestTask.setRequest(pubnativeAPIRequest);
        final Handler handler = new Handler(Looper.getMainLooper());
        pubnativeAPIRequestTask.setResponsePoster(new Executor() {

            @Override
            public void execute(Runnable runnable) {

                handler.post(runnable);
            }
        });

        sInstance.mRequestThreadPool.execute(pubnativeAPIRequestTask.getExecuteRequestRunnable());
    }

    /**
     * Recycles tasks by calling their internal recycle() method and then putting them back into
     * the task queue.
     * @param pubnativeAPIRequestTask The task to recycle
     */
    public void recycleTask(PubnativeAPIRequestTask pubnativeAPIRequestTask) {

        // Puts the task object back into the queue for re-use.
        mPubnativeAPIRequestTaskWorkQueue.offer(pubnativeAPIRequestTask);
    }
}
