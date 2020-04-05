package com.example.hackathon.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.example.hackathon.R
import com.example.hackathon.adapters.ShopAdapter
import com.example.hackathon.models.ShopModel
import kotlinx.android.synthetic.main.fragment_customer_shop.*
import java.util.*
import kotlin.collections.ArrayList


class CustomerShopFragment : Fragment(), SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename and change types of parameters


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_shop, container, false)
    }

    companion object {
        fun newInstance() = CustomerShopFragment()
        var ShopList : ArrayList<ShopModel> = ArrayList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var order1 : ShopModel = ShopModel("aaaa","jadsbf","Groceries","ss@gmail.com","1234567890","10:00AM-10:00PM",false,"https://i.imgur.com/SPVVTyd.png")
        var order2 : ShopModel = ShopModel("aaa1","Owner","Medical","halhsdfl@gmail.com","1234567890","11AM-12PM",true,"https://i.imgur.com/SPVVTyd.png")
        if(ShopList.isEmpty())
        {   ShopList.add(order1)
            ShopList.add(order2)
        }
        shopRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ShopAdapter(ShopList,context)
        }
        shopSearch!!.setOnQueryTextListener(this)
        shopSearch!!.setIconifiedByDefault(true)

        shopRefresh.setOnRefreshListener {
            ShopList.add(order1)
            shopRecyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = ShopAdapter(ShopList,context)
            }
            shopRefresh.isRefreshing=false
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        Toast.makeText(this.context,"Not Found ", Toast.LENGTH_LONG).show()
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        filter(newText)
        return true
    }
    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        var searchOrder : ArrayList<ShopModel> = ArrayList()
        if (charText.length == 0) {
            searchOrder.addAll(ShopList)
        } else {
            searchOrder.clear()
            for (wp in ShopList) {
                if (wp.shopName.toLowerCase(Locale.getDefault()).contains(charText)) {
                    searchOrder.add(wp)
                }
            }
        }
        if(searchOrder.isEmpty())
            Toast.makeText(this.context,"Not Found ", Toast.LENGTH_LONG).show()
        shopRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ShopAdapter(searchOrder,context)
        }

    }

    override fun onRefresh() {

    }
}
