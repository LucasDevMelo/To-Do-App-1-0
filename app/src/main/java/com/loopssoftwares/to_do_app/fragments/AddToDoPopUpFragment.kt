package com.loopssoftwares.to_do_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.loopssoftwares.to_do_app.databinding.FragmentAddToDoPopUpBinding
import com.loopssoftwares.to_do_app.utils.ToDoData


class AddToDoPopUpFragment : DialogFragment(){

    private lateinit var binding : FragmentAddToDoPopUpBinding
    private lateinit var listener : DialogNextBtnClickListener
    private var toDoData : ToDoData? = null

    fun setListener(listener: com.loopssoftwares.to_do_app.ui.home.HomeFragment){
        this.listener = listener
    }

    companion object {
        const val TAG = "AddTodoPopupFragment"

        @JvmStatic
        fun newInstance (taskId: String, task: String, taskDesc: String, taskDate: String, taskTime: String) = AddToDoPopUpFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)
                putString("taskDescription", taskDesc)
                putString("taskDate", taskDate)
                putString("taskTime", taskTime)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddToDoPopUpBinding.inflate(inflater, container, false)
        binding.datePickerButton.setOnClickListener {
            DatePickerFragment { result -> binding.selectedDateText.text = result }
                .show(childFragmentManager,"datePicker")
        }
        binding.timePickerButton.setOnClickListener {
            TimePickerFragment { result -> binding.selectedTimeText.text = result }
                .show(childFragmentManager,"timePicker")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null){
            toDoData = ToDoData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString(),
                arguments?.getString("taskDesc").toString(),
                arguments?.getString("taskDate").toString(),
                arguments?.getString("taskTime").toString()
            )

            binding.todoEt.setText(toDoData?.task)
            binding.todoEt2.setText(toDoData?.taskDesc)

            binding.datePickerButton.setOnClickListener {
                DatePickerFragment { result -> binding.selectedDateText.text = result }
                    .show(childFragmentManager,"datePicker")
            }
            binding.timePickerButton.setOnClickListener {
                TimePickerFragment { result -> binding.selectedDateText.text = result }
                    .show(childFragmentManager,"timePicker")
            }
        }
        registerEvents()
    }

    private fun registerEvents(){
        binding.todoNextBtn.setOnClickListener {
            val todoTask = binding.todoEt.text.toString()
            val todoDescription = binding.todoEt2.text.toString()
            val todoDate = binding.selectedDateText.text.toString()
            val todoTime = binding.selectedTimeText.text.toString()
            if (todoTask.isNotEmpty()){
                if (toDoData == null){
                    listener.onSaveTask(todoTask, binding.todoEt, binding.todoEt2, binding.selectedDateText, binding.selectedTimeText)
                } else {
                    toDoData?.task = todoTask
                    toDoData?.taskDesc = todoDescription
                    toDoData?.taskDate = todoDescription
                    toDoData?.taskTime = todoDescription
                    listener.onUpdateTask(toDoData!!, binding.todoEt, binding.todoEt2, binding.selectedDateText, binding.selectedTimeText)
                }

            } else{
                Toast.makeText(context, "Por favor, digite sua tarefa" , Toast.LENGTH_SHORT).show()
            }
        }

        binding.todoClose.setOnClickListener {
            dismiss()
        }
    }

    interface  DialogNextBtnClickListener{
        fun onSaveTask(todo: String , todoEt : TextInputEditText, todoEt2: TextInputEditText, selected_date_text: TextView, selected_time_text: TextView)
        fun onUpdateTask(toDoData: ToDoData, todoEt : TextInputEditText, todoEt2: TextInputEditText, selected_date_text: TextView, selected_time_text: TextView)
    }
}