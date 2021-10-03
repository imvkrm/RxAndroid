package com.vikram.rxandroidsampleapp.flatMap


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vikram.rxandroidsampleapp.R
import com.vikram.rxandroidsampleapp.api.ServiceGenerator
import com.vikram.rxandroidsampleapp.models.Post
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Order is not maintained
 * Transform the item(s) emitted by an Observable into Observables, then flatten the emissions from those into a single Observable.
 * */
class FlatMapActivity : AppCompatActivity() {
    private val TAG = "FlatMapActivity"

    //ui
    private var recyclerView: RecyclerView? = null

    // vars
    private val disposables: CompositeDisposable = CompositeDisposable()
    private var adapter: RecyclerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flat_map)
        recyclerView = findViewById(R.id.recycler_view)

        initRecyclerView()

      //  createFlatMap()
        createConcatMap()
    }

    private  fun createFlatMap(){
        getPostsObservable().subscribeOn(Schedulers.io())
            .flatMap {
                getCommentsObservable(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Post?> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(post: Post) {
                    updatePost(post)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {

                }

            })
    }
//Order is maintained but speed reduced
    //Transform the item(s) emitted by an Observable into Observables, then flatten the emissions from those into a single Observable.
    // This operator is essentially the same as the Flatmap operator, but it emits the object(s) while maintaining order.

    private  fun createConcatMap(){
        getPostsObservable().subscribeOn(Schedulers.io())
            .concatMap {
                getCommentsObservable(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Post?> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(post: Post) {
                    updatePost(post)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {

                }

            })
    }


    private fun getPostsObservable(): Observable<Post?> {
        return ServiceGenerator().getRequestApi()
            .getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap {
                adapter!!.setPosts(it)
                 Observable.fromIterable(it)
                    .subscribeOn(Schedulers.io())
            }
    }


    private fun updatePost(p: Post) {
        Observable
            .fromIterable(adapter!!.getPosts())
            .filter {
                it.id == p.id
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Post?> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(post: Post) {
                    Log.d(TAG,
                        "onNext: updating post: " + post.id + ", thread: " + Thread.currentThread().name
                    )
                    adapter!!.updatePost(post)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {}

            })
    }

    private fun getCommentsObservable(post: Post): Observable<Post?>? {
        return ServiceGenerator().getRequestApi()
            .getComments(post.id)
            .map {
                val delay: Int = (Random().nextInt(5) + 1) * 1000 // sleep thread for x ms
                Thread.sleep(delay.toLong())
                Log.d(
                    TAG,
                    "apply: sleeping thread " + Thread.currentThread().name + " for " + delay.toString() + "ms"
                )
                post.comments = it
                post
            }
            .subscribeOn(Schedulers.io())
    }

    private fun initRecyclerView() {
        adapter = RecyclerAdapter()
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = adapter
    }


    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

}