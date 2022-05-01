package com.soopeach.memofordaeri.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
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

            if (arguments?.getBoolean("secret") == true){
                checkSecret.isChecked = arguments?.getBoolean("secret") == true
                password.isVisible = true
                password.setText("${arguments?.getString("password")}")
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 수정버튼
        binding.btnWrite.setOnClickListener {
            modifiedMemo(binding)
        }
        // 비밀 여부 버튼
        binding.checkSecret.setOnClickListener {
            if (binding.checkSecret.isChecked) binding.password.isVisible = true
            else if (!(binding.checkSecret.isChecked)) binding.password.isVisible = false
        }
        // 삭제 버튼
        binding.deleteBtn.setOnClickListener {
            deleteMemo(binding)

        }

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
    }

    fun modifiedMemo(binding: FragmentDetailBinding){
        with(binding){
            val title = writeTitle.text.toString().trim()
            val content = writeContent.text.toString().trim()
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()).toString()
            // 비밀로 작성하기라면
            val secret = if (checkSecret.isChecked) true else false
            val password = password.text.toString().trim()

            // 제목과 내용이 모두 입력되어있어야함.
            if(title.isNotEmpty() && content.isNotEmpty()){
                val memo = Memo(title, content, date, secret, password)
                storeDb.collection("memo").add(memo)

                // 게시글 제목, 내용 초기화
                writeTitle.setText("")
                writeContent.setText("")
                Toast.makeText(context,
                    "게시글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "제목과 내용을 모두 입력하여주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteMemo(binding: FragmentDetailBinding){
        storeDb.collection("memo")?.whereEqualTo("title", arguments?.getString("title"))
            ?.whereEqualTo("content",arguments?.getString("content"))?.whereEqualTo("secret", arguments?.getBoolean("secret"))
            ?.whereEqualTo("password", arguments?.getString("password"))
            .addSnapshotListener{documents, e ->
                documents?.forEach { document ->
                    var del = document.id
                    storeDb.collection("memo").document("$del").delete()
                    Toast.makeText(context, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    // 게시글 제목, 내용 초기화
                    binding.writeTitle.setText("")
                    binding.writeContent.setText("")
                    findNavController().popBackStack()
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}