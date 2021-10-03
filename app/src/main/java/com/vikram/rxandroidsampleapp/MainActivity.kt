package com.vikram.rxandroidsampleapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.vikram.rxandroidsampleapp.flatMap.FlatMapActivity
import com.vikram.rxandroidsampleapp.models.Task
import com.vikram.rxandroidsampleapp.viewmodels.MainViewModel
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.reactivestreams.Subscription
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private var timeSinceLastRequest: Long = 0
    lateinit var compositeDisposable: CompositeDisposable
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(
            MainViewModel::class.java
        )
        compositeDisposable = CompositeDisposable()

        findViewById<Button>(R.id.buttonFlatMap).setOnClickListener {
            startActivity(Intent(this,FlatMapActivity::class.java))
        }


        // createObservableAndFlowable()
        // createRange()
        // createRepeat()

        //  createIntervalAndTimer()

        //createFromCallable()

        // createFromFuture()
        // createFromPublisher()

        // createUsingFilter()

        // createUsingDistinct()

        //take -> The take() operator will emit only the first 'n' items emitted by an Observable and then complete while ignoring the remaining items.
        // takeWhile()->Same as While Loop
        // It mirrors the source Observable until such time as some condition you specify becomes false. If the condition becomes false, TakeWhile() stops mirroring the source Observable and terminates its own Observable.

        // createUsingMap()

        // createUsingBuffer()

      //  createDebounce()
        createThrottleFirst()

    }

    private fun createObservableAndFlowable() {
        val taskObservable: Observable<Task> =// create a new Observable object
            Observable.fromIterable(createTasksList()) // apply 'fromIterable' operator
                .subscribeOn(Schedulers.io())// designate worker thread (background)
                .filter {
                    Thread.sleep(1000)// this will affect the background thread So will not freeze the UI
                    Log.d("RXJAVA: ", "filter: " + Thread.currentThread().name)

                    it.isComplete
                }
                .observeOn(AndroidSchedulers.mainThread()) // designate observer thread (main thread)

        taskObservable.subscribe(object : Observer<Task> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXJAVA: ", "onSubscribe")
                compositeDisposable.add(d)
            }

            override fun onNext(task: Task) {

                Log.d("RXJAVA: ", "onNext: " + Thread.currentThread().name)
                Log.d("RXJAVA: ", "onNext: ${task.description}")

                /*  Thread.sleep(1000)//sleep the main thread for 1 sec for every task So UI will be freezed for that much time
       */

            }

            override fun onError(e: Throwable) {
                Log.d("RXJAVA: ", "onError: " + e.localizedMessage)
            }

            override fun onComplete() {
                Log.d("RXJAVA: ", "onComplete")
            }

        })

        //In case of Consumer it return disposable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            compositeDisposable.add(taskObservable.subscribe(object : Consumer<Task> {
                override fun accept(p0: Task) {
                    Log.d("RXJAVA", "accept: ")
                }

            }))
        }


        //Flowable-> Backpressing Aware
        val flowable: Flowable<Task> = taskObservable.toFlowable(BackpressureStrategy.BUFFER)

        flowable.subscribe(object : FlowableSubscriber<Task> {

            override fun onSubscribe(s: Subscription) {


            }

            override fun onNext(t: Task) {

            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {

            }


        })

    }

    //Range -> when you need to perform some high end task in background then instead of loop use range
    private fun createRange() {
        val rangeObservable: Observable<Task> =
            Observable.range(0, 9)
                .map {
                    Log.d("RXJAVA: range ", "map: " + Thread.currentThread().name)

                    Task("Take out the trash", true, it)
                }
                .takeWhile { it.priority < 9 }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        rangeObservable.subscribe(object : Observer<Task> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXJAVA: range ", "onSubscribe")
                compositeDisposable.add(d)
            }

            override fun onNext(task: Task) {

                Log.d("RXJAVA: range ", "onNext: " + Thread.currentThread().name)
                Log.d("RXJAVA: range ", "onNext: ${task.description} ${task.priority}")

                /*  Thread.sleep(1000)//sleep the main thread for 1 sec for every task So UI will be freezed for that much time
       */

            }

            override fun onError(e: Throwable) {
                Log.d("RXJAVA: range ", "onError: " + e.localizedMessage)
            }

            override fun onComplete() {
                Log.d("RXJAVA: range ", "onComplete")
            }

        })

    }

    //Repeat - repeat the task for n times
    private fun createRepeat() {
        val repeatObservable: Observable<Int> =
            Observable.range(0, 9)
                .subscribeOn(Schedulers.io())
                .repeat(2)
                .observeOn(AndroidSchedulers.mainThread())
        repeatObservable.subscribe(object : Observer<Int> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXJAVA: repeat ", "onSubscribe")
                compositeDisposable.add(d)
            }

            override fun onNext(t: Int) {

                Log.d("RXJAVA: repeat ", "onNext: " + Thread.currentThread().name)
            }

            override fun onError(e: Throwable) {
                Log.d("RXJAVA: repeat ", "onError: " + e.localizedMessage)
            }

            override fun onComplete() {
                Log.d("RXJAVA: repeat ", "onComplete")
            }
        })


    }

    // Interval-> emit an observable every time interval
    //Timer -> emits one particular item after a span of time that you specify.
    private fun createIntervalAndTimer() {
        val intervalObservable: Observable<Long> =
            Observable.interval(1, TimeUnit.SECONDS) // if period=2 it will emit after every 2 secs
                .subscribeOn(Schedulers.io())
                .takeWhile {//// stop the process if more than 5 seconds passes
                    Log.d(
                        "RXJAVA: interval ",
                        "intervalObservable: $it :" + Thread.currentThread().name
                    )
                    it <= 5
                }
                .observeOn(AndroidSchedulers.mainThread())

        intervalObservable.subscribe(object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXJAVA: interval ", "onSubscribe")
                compositeDisposable.add(d)
            }

            override fun onNext(t: Long) {
                Log.d("RXJAVA: interval ", "onNext: " + Thread.currentThread().name)
                Log.d("RXJAVA: interval ", "onNext: $t")

            }

            override fun onError(e: Throwable) {
                Log.d("RXJAVA: interval ", "onError: " + e.localizedMessage)

            }

            override fun onComplete() {
                Log.d("RXJAVA: interval ", "onComplete")
            }
        })


        val timerObservable: Observable<Long> = Observable.timer(5, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        timerObservable.subscribe(object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXJAVA: timer ", "onSubscribe")
                compositeDisposable.add(d)
            }

            override fun onNext(t: Long) {
                Log.d("RXJAVA: timer ", "onNext: " + Thread.currentThread().name)
                Log.d("RXJAVA: timer ", "onNext: $t")

            }

            override fun onError(e: Throwable) {
                Log.d("RXJAVA: timer ", "onError: " + e.localizedMessage)

            }

            override fun onComplete() {
                Log.d("RXJAVA: timer ", "onComplete")
            }
        })


    }

    //fromCallable()->  will execute a block of code (usually a method) and return a result.
    // useful when you need to return a Task object from a local SQLite database cache All database operations must be done on a background thread. Then the result is returned to the main thread.
    private fun createFromCallable() {
        val callableObservable: Observable<List<Task>> =
            Observable.fromCallable {
                createTasksList()
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        // method will be executed since now something has subscribed
        callableObservable.subscribe(object : Observer<List<Task>> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXJAVA :", "onSubscribe")
                compositeDisposable.add(d)

            }

            override fun onNext(t: List<Task>) {
                Log.d("RXJAVA :", "onNext: ${t[0]}")
            }

            override fun onError(e: Throwable) {
                Log.d("RXJAVA :", "onError: ${e.localizedMessage}")
            }

            override fun onComplete() {
                Log.d("RXJAVA :", "onComplete")
            }
        })
    }

    /**
     * This is generally use for an Api
     * A future is essentially a pending task.
     * It's a promise for a result from a task when it runs sometime in the future.
     * The task can be a executed via a Runnable or a Callable (not to be confused with an Rx Callable).
     * An example of something that can execute these runnables or callables is an ExecutorService.*/
    private fun createFromFuture() {

        try {
            viewModel.makeFutureQuery().get()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResponseBody?> {

                    override fun onSubscribe(d: Disposable) {
                        Log.d("RXJAVA: ", "onSubscribe: called.")
                    }

                    override fun onNext(responseBody: ResponseBody) {
                        Log.d("RXJAVA: ", "onNext: got the response from server!")
                        try {
                            Log.d("RXJAVA: ", "onNext: " + responseBody.string())
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onComplete() {
                        Log.d("RXJAVA: ", "onComplete: called.")
                    }

                    override fun onError(e: Throwable) {
                        Log.e("RXJAVA: ", "onError: ", e)
                    }
                })
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


    /**
     *  This is also generally use for an Api
     * A network request using Retrofit and retrieves a response in the form of a Flowable object.
     * Flowable implements the Publisher interface.
     * Publisher objects can be subscribed to just like Observables.*/
    private fun createFromPublisher() {
        viewModel.makePublisherQuery().observe(this, androidx.lifecycle.Observer {
            Log.d("RX JAVA", "onChanged: this is a live data response!")
            try {
                Log.d("RX JAVA", "onChanged: " + it.string())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
    }

    //If list is very large  so the filtering should probably be done on a background thread then we can use this
    private fun createUsingFilter() {
        val filterObservable: Observable<Task> =// create a new Observable object
            Observable.fromIterable(createTasksList()) // apply 'fromIterable' operator
                .subscribeOn(Schedulers.io())// designate worker thread (background)
                .filter {
                    Log.d("RXJAVA: ", "filter: " + Thread.currentThread().name)
                    it.description == "Walk the dog"
                }
                .observeOn(AndroidSchedulers.mainThread()) // designate observer thread (main thread)

        filterObservable.subscribe(object : Observer<Task> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXJAVA: ", "onSubscribe")
                compositeDisposable.add(d)
            }

            override fun onNext(task: Task) {

                Log.d("RXJAVA: ", "onNext: " + Thread.currentThread().name)
                Log.d("RXJAVA: ", "onNext: ${task.description}")

                /*  Thread.sleep(1000)//sleep the main thread for 1 sec for every task So UI will be freezed for that much time
       */

            }

            override fun onError(e: Throwable) {
                Log.d("RXJAVA: ", "onError: " + e.localizedMessage)
            }

            override fun onComplete() {
                Log.d("RXJAVA: ", "onComplete")
            }

        })
    }

    //The Distinct operator filters an Observable by only allowing items through that have not already been emitted.
    //But what defines the object as "distinct" is up to the developer to determine.
    private fun createUsingDistinct() {
        val distinctObservable: Observable<Task> =// create a new Observable object
            Observable.fromIterable(createTasksList2()) // apply 'fromIterable' operator
                .subscribeOn(Schedulers.io())// designate worker thread (background)
                .distinct {
                    it.description// on the basis of description
                }
                .observeOn(AndroidSchedulers.mainThread()) // designate observer thread (main thread)

        distinctObservable.subscribe(object : Observer<Task> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXJAVA: ", "onSubscribe")
                compositeDisposable.add(d)
            }

            override fun onNext(task: Task) {

                Log.d("RXJAVA: ", "onNext: " + Thread.currentThread().name)
                Log.d("RXJAVA: ", "onNext: ${task.description}")

                /*  Thread.sleep(1000)//sleep the main thread for 1 sec for every task So UI will be freezed for that much time
       */

            }

            override fun onError(e: Throwable) {
                Log.d("RXJAVA: ", "onError: " + e.localizedMessage)
            }

            override fun onComplete() {
                Log.d("RXJAVA: ", "onComplete")
            }

        })
    }

    //Applies a function to each emitted item. It transforms each emitted item by applying a function to it.
    private fun createUsingMap() {
        val mapObservable: Observable<String> =// create a new Observable object
            Observable.fromIterable(createTasksList()) // apply 'fromIterable' operator
                .subscribeOn(Schedulers.io())// designate worker thread (background)
                .map {
                    it.description
                }
                .observeOn(AndroidSchedulers.mainThread()) // designate observer thread (main thread)

        mapObservable.subscribe(object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXJAVA: ", "onSubscribe")
                compositeDisposable.add(d)
            }

            override fun onNext(t: String) {
                Log.d("RXJAVA: ", "onNext: " + Thread.currentThread().name)
                Log.d("RXJAVA: ", "onNext: ${t}")
            }

            override fun onError(e: Throwable) {
                Log.d("RXJAVA: ", "onError: " + e.localizedMessage)
            }

            override fun onComplete() {
                Log.d("RXJAVA: ", "onComplete")
            }
        })

    }

    //Periodically gather items from an Observable into bundles and emit the bundles rather than emitting items one at a time.
    private fun createUsingBuffer() {
        val bufferObservable: Observable<List<Task>> =// create a new Observable object
            Observable.fromIterable(createTasksList()) // apply 'fromIterable' operator
                .subscribeOn(Schedulers.io())// designate worker thread (background)
                .buffer(2)
                .observeOn(AndroidSchedulers.mainThread()) // designate observer thread (main thread)

        bufferObservable.subscribe(object : Observer<List<Task>> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXJAVA: ", "onSubscribe")
                compositeDisposable.add(d)
            }

            override fun onNext(t: List<Task>) {
                Log.d("RXJAVA: ", "onNext: bundle results: -------------------");
                for (task in t) {
                    Log.d("RXJAVA: ", "onNext: ${task.description}")
                }
            }

            override fun onError(e: Throwable) {
                Log.d("RXJAVA: ", "onError: " + e.localizedMessage)
            }

            override fun onComplete() {
                Log.d("RXJAVA: ", "onComplete")
            }
        })
    }

    //Kind of same methods are used by instagram/yt etc in their search
//The Debounce operator filters out items(by delaying their emission) emitted by the source Observable that are rapidly followed by another emitted item.
    private fun createDebounce() {
        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.search_view)

        var observableQueryText: Observable<String> =
            Observable.create {
                // Listen for text input into the SearchView
                searchView.setOnQueryTextListener(object :
                    androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (!it.isDisposed) {
                            it.onNext(newText!!) // Pass the query to the emitter
                        }
                        return false
                    }
                })
            }
        observableQueryText = observableQueryText
            .debounce(1000, TimeUnit.MILLISECONDS) // Apply Debounce() operator to limit requests
            .subscribeOn(Schedulers.io())


        observableQueryText.subscribe(object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXJAVA: ", "onSubscribe")
                compositeDisposable.add(d)
            }

            override fun onNext(s: String) {
                Log.d(
                    "RXJAVA: ",
                    "onNext: time  since last request: " + (System.currentTimeMillis() - timeSinceLastRequest)
                );
                Log.d("RXJAVA: ", "onNext: search query: $s");
                timeSinceLastRequest = System.currentTimeMillis();


                // method for sending a request to the server
                sendRequestToServer(s);
            }

            override fun onError(e: Throwable) {
                Log.d("RXJAVA: ", "onError ${e.localizedMessage}")
            }

            override fun onComplete() {
                Log.d("RXJAVA: ", "onComplete")
            }
        })


    }



    // Fake method for sending a request to the server
    private fun sendRequestToServer(query: String) {
        // do nothing
    }


    //The ThrottleFirst() operator filters out items emitted by the source Observable that are within a timespan.
    private fun createThrottleFirst(){
        val button = findViewById<Button>(R.id.button)
        timeSinceLastRequest = System.currentTimeMillis();

        var observableQueryText: Observable<Unit> =
            Observable.create {
                // Listen for text input into the SearchView
                button.setOnClickListener { view->
                    if (!it.isDisposed) {
                        it.onNext(Unit) // Pass the query to the emitter
                    }
                }
            }
        observableQueryText = observableQueryText
            .throttleFirst(4000, TimeUnit.MILLISECONDS) // Apply Debounce() operator to limit requests
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


        observableQueryText.subscribe(object : Observer<Unit> {
            override fun onSubscribe(d: Disposable) {
                Log.d("RXJAVA: ", "onSubscribe")
                compositeDisposable.add(d)
            }

            override fun onNext(u: Unit) {
                Log.d("RXJAVA: ", "onNext: time since last clicked: " + (System.currentTimeMillis() - timeSinceLastRequest));
                someMethod(); // Execute some method when a click is registered

            }

            override fun onError(e: Throwable) {
                Log.d("RXJAVA: ", "onError ${e.localizedMessage}")
            }

            override fun onComplete() {
                Log.d("RXJAVA: ", "onComplete")
            }
        })
    }

    //Here Order is not maintained
    //FlatMap -> Transform the item(s) emitted by an Observable into Observables, then flatten the emissions from those into a single Observable.
    private fun createFlatMap(){}

    private fun someMethod() {
        timeSinceLastRequest = System.currentTimeMillis();
        Toast.makeText(this,"You clicked a button $timeSinceLastRequest",Toast.LENGTH_SHORT).show()
    }

    private fun createTasksList(): List<Task> {
        val tasks: MutableList<Task> = ArrayList()
        tasks.add(Task("Take out the trash", true, 3))
        tasks.add(Task("Walk the dog", false, 2))
        tasks.add(Task("Make my bed", true, 1))
        tasks.add(Task("Unload the dishwasher", false, 0))
        tasks.add(Task("Make dinner", true, 5))
        return tasks
    }

    private fun createTasksList2(): List<Task> {
        val tasks: MutableList<Task> = ArrayList()
        tasks.add(Task("Take out the trash", true, 3))
        tasks.add(Task("Walk the dog", false, 2))
        tasks.add(Task("Make my bed", true, 1))
        tasks.add(Task("Unload the dishwasher", false, 0))
        tasks.add(Task("Unload the dishwasher", false, 0))
        tasks.add(Task("Make dinner", true, 5))
        tasks.add(Task("Make dinner", false, 3))

        return tasks
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()

    }
}