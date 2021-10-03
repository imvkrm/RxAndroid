package com.vikram.rxandroidsampleapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.vikram.rxandroidsampleapp.api.Repository
import io.reactivex.Observable
import okhttp3.ResponseBody
import java.util.concurrent.Future


class MainViewModel : ViewModel() {
    private var repository: Repository? = null

    fun MainViewModel() {
        repository = Repository().getInstance()
    }

    fun makeFutureQuery(): Future<Observable<ResponseBody>> {
        if(repository==null)
            repository = Repository().getInstance()
        return repository!!.makeFutureQuery()
    }

    fun makePublisherQuery():LiveData<ResponseBody>{
        if(repository==null)
            repository = Repository().getInstance()
        return  repository!!.makePublisherQuery()
    }
}