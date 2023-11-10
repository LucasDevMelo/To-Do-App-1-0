package com.loopssoftwares.to_do_app.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.loopssoftwares.to_do_app.databinding.EachTodoDayItemBinding

class ToDoDayAdapter(private val list:MutableList<ToDoData>) :
    RecyclerView.Adapter<ToDoDayAdapter.ToDoViewHolder>(){

    private var listener:ToDoDayAdapterClicksInterface? = null
    fun setListener(listener:ToDoDayAdapterClicksInterface){
        this.listener = listener
    }
    inner class ToDoViewHolder(val binding : EachTodoDayItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding = EachTodoDayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.todoTask.text = this.task
                binding.todoDescription.text = this.taskDesc
                binding.todoDate.text = this.taskDate

                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteBtnClicked(this)
                }

                binding.editTask.setOnClickListener {
                    listener?.onEditTaskBtnClicked(this)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ToDoDayAdapterClicksInterface{
        fun onDeleteBtnClicked(toDoData: ToDoData)
        fun onEditTaskBtnClicked(toDoData: ToDoData)
    }
}