package com.soopeach.memofordaeri

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soopeach.memofordaeri.databinding.MemoRecyclerBinding

class MemoRecyclerAdapter: RecyclerView.Adapter<MemoRecyclerAdapter.MemoHolder>() {

    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemCLickListener = onItemClickListener
    }
    private lateinit var itemCLickListener: OnItemClickListener

    var memoList = mutableListOf<Memo>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoHolder {
        val binding =
            MemoRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemoHolder(binding)
    }

    override fun onBindViewHolder(holder: MemoHolder, position: Int) {
        val memo = memoList.get(position)
        holder.setMemo(memo)

        // 클릭 판정하기 위하여
        holder.itemView.setOnClickListener {
            itemCLickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    class MemoHolder(val binding : MemoRecyclerBinding) : RecyclerView.ViewHolder(binding.root){

        fun setMemo(memo : Memo){
            // 비밀게시글이라면
            if (memo.secret){
                binding.memoTitle.text = "비밀 게시글 입니다."
                binding.memoContent.text = ""
                binding.memoDate.text = memo.date
            } else {
                binding.memoTitle.text = memo.title
                binding.memoContent.text = memo.content
                binding.memoDate.text = memo.date
            }
        }
    }
}

