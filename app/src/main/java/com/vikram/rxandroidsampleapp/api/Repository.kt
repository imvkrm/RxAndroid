package com.vikram.rxandroidsampleapp.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.util.concurrent.*

/**
 * using an Executor to make a network call, and then returning a Future Observable to the ViewModel.*/
class Repository {

    private var instance: Repository? = null

    fun getInstance(): Repository? {
        if (instance == null) {
            instance = Repository()
        }
        return instance
    }

     fun makeFutureQuery(): Future<Observable<ResponseBody>> {
        val executor: ExecutorService = Executors.newSingleThreadExecutor();

         //Callable , represents an asynchronous task which can be executed by a separate thread.
        val myNetworkCallable: Callable<Observable<ResponseBody>> =
            Callable<Observable<ResponseBody>> {
                ServiceGenerator().getRequestApi().makeObservableQueryF()
            }

        return object : Future<Observable<ResponseBody>> {
            override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
                if (mayInterruptIfRunning) {
                    executor.shutdown()
                }
                return false
            }

            override fun isCancelled(): Boolean {
                return executor.isShutdown
            }

            override fun isDone(): Boolean {
                return executor.isTerminated
            }

            @Throws(ExecutionException::class, InterruptedException::class)
            override fun get(): Observable<ResponseBody> {
                return executor.submit(myNetworkCallable).get()
            }

            @Throws(ExecutionException::class, InterruptedException::class, TimeoutException::class)
            override fun get(timeout: Long, unit: TimeUnit?): Observable<ResponseBody> {
                return executor.submit(myNetworkCallable)[timeout, unit]
            }
        }
    }
    fun makePublisherQuery():LiveData<ResponseBody>{
        return LiveDataReactiveStreams
            .fromPublisher(
                ServiceGenerator().getRequestApi()//fromPublisher method to convert a Flowable into LiveData.
                .makeObservableQueryP()
                .subscribeOn(Schedulers.io()))//on a background thread

    }

}