package com.soopeach.memofordaeri.ui.Write

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.soopeach.memofordaeri.Memo
import com.soopeach.memofordaeri.R
import com.soopeach.memofordaeri.databinding.FragmentWriteBinding
import java.text.SimpleDateFormat

class WriteFragment : Fragment() {

    private var _binding: FragmentWriteBinding? = null
    val storeDb = Firebase.firestore

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWriteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 글자수 입력을 나타내줌
        with(binding){
            writeContent.addTextChangedListener(object : TextWatcher {
                var maxContent = ""
                override fun beforeTextChanged(pos: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    maxContent = pos.toString()
                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    var userInput = writeContent.text.toString()

                    // 최대 문장 수가 100줄을 초과할 경우
                    if (writeContent.lineCount > 100){
                        Toast.makeText(context, "최대 문장 수를 초과했습니다."
                            ,Toast.LENGTH_SHORT).show()

                        writeContent.setText(maxContent)
                        writeContent.setSelection(writeContent.length())
                        countText.setText(userInput.length.toString() + " / 2000\n" +
                                "최대 100줄")
                        // 최대 글자 수가 2000자를 초과할 경우
                    } else if (userInput.length >= 2000){
                        Toast.makeText(context, "최대 글자 수를 초과했습니다."
                            ,Toast.LENGTH_SHORT).show()

                        writeContent.setText(maxContent)
                        writeContent.setSelection(writeContent.length())
                        countText.setText(userInput.length.toString() + " / 2000\n" +
                                "최대 100줄")
                    }else {
                        countText.text = userInput.length.toString() + " / 2000\n" +
                                "최대 100줄"
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
        }

        // 글 쓰기 버튼을 클릭
        binding.btnWrite.setOnClickListener {upLoadMemo(binding)}

    }
    fun upLoadMemo(binding : FragmentWriteBinding){
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
                    "게시글이 작성되었습니다.",Toast.LENGTH_SHORT).show()
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