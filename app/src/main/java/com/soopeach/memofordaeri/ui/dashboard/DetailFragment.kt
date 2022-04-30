package com.soopeach.memofordaeri.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soopeach.memofordaeri.Memo
import com.soopeach.memofordaeri.R
import com.soopeach.memofordaeri.databinding.FragmentDetailBinding
import java.text.SimpleDateFormat

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    val storeDb = Firebase.firestore

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        with(binding){
            writeTitle.setText("${arguments?.getString("title")}")
            writeContent.setText("${arguments?.getString("content")}")
            checkSecret.isChecked = arguments?.getBoolean("secret") == true
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun modifiedMemo(binding: FragmentDetailBinding){
        with(binding){
            val title = writeTitle.text.toString().trim()
            val content = writeContent.text.toString().trim()
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()).toString()
            // 비밀로 작성하기라면
            val secret = if (checkSecret.isChecked) true else false

            // 제목과 내용이 모두 입력되어있어야함.
            if(title.isNotEmpty() && content.isNotEmpty()){
                val memo = Memo(title, content, date, secret)
                storeDb.collection("memo").add(memo)

                // 게시글 제목, 내용 초기화
                writeTitle.setText("")
                writeContent.setText("")
                Toast.makeText(context,
                    "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_navigation_write_to_navigation_home)
            } else {
                Toast.makeText(context, "제목과 내용을 모두 입력하여주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}