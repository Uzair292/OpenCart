package com.example.opencart.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import com.example.opencart.R
import com.example.opencart.databinding.FragmentProductBinding
import com.example.opencart.firebase.FirestoreClass
import com.example.opencart.models.Product
import com.example.opencart.ui.activities.AddProductActivity
import com.example.opencart.ui.activities.SettingsActivity

class ProductFragment : BaseFragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductBinding.inflate(inflater, container, false)

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_product_menu,menu)
            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.action_add_product -> {
                        startActivity(Intent(activity, AddProductActivity::class.java))
                        return true
                    }
                }
                return false
            }

        }, viewLifecycleOwner)


        return binding.root
    }

    override fun onResume() {
        super.onResume()

        getProductListFromFireStore()
    }

    private fun getProductListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductsList(this@ProductFragment)
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()
        for (i in productsList){
            Log.d("Product Name", i.title)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
