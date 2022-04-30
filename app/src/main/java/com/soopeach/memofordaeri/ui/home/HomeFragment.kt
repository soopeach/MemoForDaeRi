package com.soopeach.memofordaeri.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soopeach.memofordaeri.Memo
import com.soopeach.memofordaeri.MemoRecyclerAdapter
import com.soopeach.memofordaeri.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    var memoList = mutableListOf<Memo>()
    val storeDb = Firebase.firestore
    val adapter = MemoRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        loadMemo()
        adapter.memoList = memoList
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
        return binding.root
    }

    fun loadMemo(){

        CoroutineScope(Dispatchers.IO).launch{
            storeDb.collection("Memo")
                .addSnapshotListener{ documents, e ->
                    adapter.memoList.clear()
                    if (documents!!.isEmpty) memoList.add(Memo("제목", "내용", "날짜"))
                    documents?.forEach{document ->
                        val title = document.get("title").toString()
                        val content = document.get("content").toString()
                        val date = document.get("date").toString()
                        memoList.add(Memo(title, content, date))
                    }
                    // 시간순으로 정렬
                    memoList.sortByDescending { it.date }
                    // 리스너는 완료시간이 불분명하기 때문에 어뎁터에게 정보가 변경됨을 알려야함.
                    adapter.notifyDataSetChanged()
                }
        }

    }
}



//class HomeFragment : Fragment() {
//
//    private var _binding: FragmentHomeBinding? = null
//
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
//
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
//        return root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}