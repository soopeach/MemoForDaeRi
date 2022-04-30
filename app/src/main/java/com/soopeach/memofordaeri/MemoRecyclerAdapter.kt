package com.soopeach.memofordaeri

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soopeach.memofordaeri.databinding.MemoRecyclerBinding

class MemoRecyclerAdapter: RecyclerView.Adapter<MemoRecyclerAdapter.MemoHolder>() {

    var memoList = mutableListOf<Memo>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoHolder {
        val binding =
            MemoRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemoHolder(binding)
    }

    override fun onBindViewHolder(holder: MemoHolder, position: Int) {
        val memo = memoList.get(position)
        holder.setMemo(memo)
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    class MemoHolder(val binding : MemoRecyclerBinding) : RecyclerView.ViewHolder(binding.root){

        fun setMemo(memo : Memo){
            binding.memoTitle.text = memo.title
            binding.memoContent.text = memo.content
            binding.memoDate.text = memo.date
        }
    }
}

