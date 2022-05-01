package com.soopeach.memofordaeri.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soopeach.memofordaeri.Memo
import com.soopeach.memofordaeri.MemoRecyclerAdapter
import com.soopeach.memofordaeri.R
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



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.setItemClickListener(object : MemoRecyclerAdapter.OnItemClickListener{
            override fun onClick(view: View, position: Int) {

                val bundle = bundleOf(
                    "title" to "${memoList[position].title}",
                    "content" to "${memoList[position].content}",
                    "date" to "${memoList[position].date}",
                    "secret" to "${memoList[position].secret}".toBoolean(),
                    "password" to "${memoList[position].password}"
                )
                if(memoList[position].password == "") findNavController().navigate(R.id.action_navigation_home_to_navigation_detail, bundle)
                else {
                    val builder = AlertDialog.Builder(context)
                    val diaView = layoutInflater.inflate(R.layout.password_dialog, null);
                    val button = diaView.findViewById<Button>(R.id.passwordCheckBtn)

                    builder
                        .setView(R.layout.password_dialog)
                        .create()

                    button.setOnClickListener {
                        var inputpassword = diaView.findViewById<EditText>(R.id.passwordCheck).text.toString()
                        if (memoList[position].password == inputpassword){
                            Toast.makeText(context, "일치", Toast.LENGTH_SHORT).show()
                        } else Toast.makeText(context, "불일치", Toast.LENGTH_SHORT).show()
                    }

                    builder.show()


                }
            }
        })



        binding.btnWrite.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_writeFragment)
        }
    }

    fun loadMemo(){

        CoroutineScope(Dispatchers.IO).launch{
            storeDb.collection("memo")
                .addSnapshotListener{ documents, e ->
                    adapter.memoList.clear()
                    if (documents!!.isEmpty) memoList.add(Memo("제목", "내용", "날짜", false,""))
                    documents?.forEach{document ->
                        val title = document.get("title").toString()
                        val content = document.get("content").toString()
                        val date = document.get("date").toString()
                        val secret = document.get("secret")
                        val password = document.get("password").toString()
                        memoList.add(Memo(title, content, date, secret as Boolean,password))
                    }
                    // 시간순으로 정렬
                    memoList.sortByDescending { it.date }
                    // 리스너는 완료시간이 불분명하기 때문에 어뎁터에게 정보가 변경됨을 알려야함.
                    adapter.notifyDataSetChanged()
                }
        }

    }
}
