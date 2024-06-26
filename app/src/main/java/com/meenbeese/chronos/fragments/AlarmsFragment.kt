package com.meenbeese.chronos.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.meenbeese.chronos.R
import com.meenbeese.chronos.adapters.AlarmsAdapter
import com.meenbeese.chronos.interfaces.ContextFragmentInstantiator
import com.afollestad.aesthetic.Aesthetic.Companion.get

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy


class AlarmsFragment : BasePagerFragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var empty: View
    private lateinit var alarmsAdapter: AlarmsAdapter
    private var colorAccentSubscription: Disposable? = null
    private var colorForegroundSubscription: Disposable? = null
    private var textColorPrimarySubscription: Disposable? = null
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_recycler, container, false)
        recyclerView = v.findViewById(R.id.recycler)
        empty = v.findViewById(R.id.empty)
        (v.findViewById<View>(R.id.emptyText) as TextView).setText(R.string.msg_alarms_empty)
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        alarmsAdapter = AlarmsAdapter(chronos!!, recyclerView, parentFragmentManager)
        recyclerView.adapter = alarmsAdapter

        colorAccentSubscription = get()
            .colorAccent()
            .subscribeBy(
                onNext = { integer: Int? -> alarmsAdapter.colorAccent = integer!! },
                onError = { it.printStackTrace() }
            ).also { disposables.add(it) }
        colorForegroundSubscription = get()
            .colorCardViewBackground()
            .subscribeBy(
                onNext = { integer: Int? -> alarmsAdapter.colorForeground = integer!! },
                onError = { it.printStackTrace() }
            ).also { disposables.add(it) }
        textColorPrimarySubscription = get()
            .textColorPrimary()
            .subscribeBy(
                onNext = { integer: Int? -> alarmsAdapter.textColorPrimary = integer!! },
                onError = { it.printStackTrace() }
            ).also { disposables.add(it) }
        onChanged()

        return v
    }

    override fun onDestroyView() {
        disposables.dispose()
        super.onDestroyView()
    }

    override fun getTitle(context: Context?): String? {
        return context?.getString(R.string.title_alarms)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onAlarmsChanged() {
        recyclerView.post { alarmsAdapter.notifyDataSetChanged() }
        onChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTimersChanged() {
        recyclerView.post { alarmsAdapter.notifyDataSetChanged() }
        onChanged()
    }

    private fun onChanged() {
        empty.visibility = if (alarmsAdapter.itemCount > 0) View.GONE else View.VISIBLE
    }

    class Instantiator(context: Context?) : ContextFragmentInstantiator(context!!) {
        override fun getTitle(context: Context?, position: Int): String? {
            return context?.getString(R.string.title_alarms)
        }

        override fun newInstance(position: Int): BasePagerFragment {
            return AlarmsFragment()
        }
    }
}
