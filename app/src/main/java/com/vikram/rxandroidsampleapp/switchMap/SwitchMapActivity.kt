package com.vikram.rxandroidsampleapp.switchMap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vikram.rxandroidsampleapp.R
import com.vikram.rxandroidsampleapp.api.ServiceGenerator
import com.vikram.rxandroidsampleapp.models.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit


class SwitchMapActivity : AppCompatActivity(), RecyclerAdapter.OnPostClickListener {
    private val TAG = "SwitchMapActivity"

    //ui
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    // vars
    private val disposables: CompositeDisposable = CompositeDisposable()
    private var adapter: RecyclerAdapter? = null
    private val publishSubject: PublishSubject<Post> =
        PublishSubject.create() // for selecting a post

    private val PERIOD: Long = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_switch_map)
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);

        initRecyclerView();
        retrievePosts();
    }

    private fun initSwitchMapDemo() {
        // apply switchmap operator so only one Observable can be used at a time.
        // it clears the previous one


    /*    publishSubject // apply switchmap operator so only one Observable can be used at a time.
            // it clears the previous one
            *//*  .switchMap(Function<Post, ObservableSource<Post>>() {
                  @Throws(Exception::class)
                override fun apply(post: Post): ObservableSource<Post?>? {
                      return Observable // simulate slow network speed with interval + takeWhile + filter operators
                          .interval(PERIOD, TimeUnit.MILLISECONDS)
                          .subscribeOn(AndroidSchedulers.mainThread())
                          .takeWhile { aLong ->

                              // stop the process if more than 5 seconds passes
                              Log.d(TAG, "test: " + Thread.currentThread().name + ", " + aLong)
                              progressBar!!.max = (3000 - PERIOD).toInt()
                              progressBar!!.progress =
                                  (aLong * PERIOD + PERIOD).toString().toInt()
                              aLong <= 3000 / PERIOD
                          }
                          .filter { aLong -> aLong >= 3000 / PERIOD } // flatmap to convert Long from the interval operator into a Observable<Post>
                          .subscribeOn(Schedulers.io())
                          .flatMap(object : Function<Long?, ObservableSource<Post?>> {
                              @Throws(Exception::class)
                            override fun apply(aLong: Long?): ObservableSource<Post?> {
                                  return ServiceGenerator().getRequestApi().getPost(post.id)
                              }
                          })
                  }
              })*//*
            .switchMap {
                Observable.interval(PERIOD, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .takeWhile {
                        Log.d(TAG, "test: " + Thread.currentThread().name + ", " + it)
                        progressBar!!.max = 3000 - PERIOD
                        progressBar!!.progress =
                            (it * PERIOD + PERIOD).toString().toInt()
                        it <= 3000 / PERIOD
                    }
                    .filter {
                        it >= 3000 / PERIOD
                    }
                    // flatmap to convert Long from the interval operator into a Observable<Post>
                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        ServiceGenerator().getRequestApi().getComments(it.id)
                    }
            }
            .subscribe(object : Observer<Post?> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }


                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {}
                override fun onNext(post: Post) {
                    Log.d(TAG, "onNext: done.")
                    navViewPostActivity(post)
                }
            })*/


        /* publishSubject.switchMap{
                // simulate slow network speed with interval + takeWhile + filter operators
                Observable.interval(PERIOD, TimeUnit.MILLISECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .takeWhile{
                        Log.d(TAG, "test: " + Thread.currentThread().name + ", " + it)
                        progressBar!!.max = 3000 - PERIOD
                        progressBar!!.progress =
                            (it * PERIOD + PERIOD).toString().toInt()
                         it <= 3000 / PERIOD
                    }
                    .filter {
                        it >= 3000 / PERIOD
                        }
                    // flatmap to convert Long from the interval operator into a Observable<Post>
                    .subscribeOn(Schedulers.io())
                    .flatMap{
                        ServiceGenerator().getRequestApi().getComments(it.id)
                    }

            }
            .subscribe(object : Observer<Post?> {

                override fun onComplete() {}


                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: Post) {
                    Log.d(TAG, "onNext: done.")
                    navViewPostActivity(t)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }
            })*/
    }

    private fun retrievePosts() {
        ServiceGenerator().getRequestApi()
            .getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MutableList<Post>> {

                override fun onComplete() {}
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: MutableList<Post>) {
                    adapter!!.setPosts(t)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }
            })
    }

    override fun onResume() {
        super.onResume()
        progressBar!!.progress = 0
        initSwitchMapDemo()
    }

    private fun initRecyclerView() {
       // adapter = RecyclerAdapter(this)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = adapter
    }

    private fun navViewPostActivity(post: Post) {
        val intent = Intent(this, ViewPostActivity::class.java)
        intent.putExtra("post_title", post.title)
        startActivity(intent)
    }

    override fun onPause() {
        Log.d(TAG, "onPause: called.")
        disposables.clear()
        super.onPause()
    }

    override fun onPostClick(position: Int) {
        Log.d(TAG, "onPostClick: clicked.")

        // submit the selected post object to be queried
        publishSubject.onNext(adapter!!.getPosts()[position])
    }

}