package com.loopssoftwares.to_do_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.loopssoftwares.to_do_app.R
import com.loopssoftwares.to_do_app.calendar.CalendarAdapter
import com.loopssoftwares.to_do_app.calendar.CalendarDateModel
import com.loopssoftwares.to_do_app.utils.ToDoData
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment(), CalendarAdapter.onItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvDateMonth: TextView
    private lateinit var ivCalendarNext: ImageView
    private lateinit var ivCalendarPrevious: ImageView
    private lateinit var mList: MutableList<ToDoData>

    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)
    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val dates = ArrayList<Date>()
    private lateinit var adapter: CalendarAdapter
    private val calendarList2 = ArrayList<CalendarDateModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)

        tvDateMonth = view.findViewById(R.id.text_date_month)
        recyclerView = view.findViewById(R.id.recyclerView)
        ivCalendarNext = view.findViewById(R.id.iv_calendar_next)
        ivCalendarPrevious = view.findViewById(R.id.iv_calendar_previous)
        setUpAdapter()
        setUpClickListener()
        setUpCalendar()

        return view
    }

    override fun onItemClick(text: String, date: String, day: String) {
        getTasksForSelectedDay(date)
    }

    /**
     * Set up click listener
     */
    private fun setUpClickListener() {
        ivCalendarNext.setOnClickListener {
            cal.add(Calendar.MONTH, 1)
            setUpCalendar()
        }
        ivCalendarPrevious.setOnClickListener {
            cal.add(Calendar.MONTH, -1)
            if (cal == currentDate)
                setUpCalendar()
            else
                setUpCalendar()
        }
    }

    /**
     * Setting up adapter for recyclerview
     */
    private fun setUpAdapter() {
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        adapter = CalendarAdapter { calendarDateModel: CalendarDateModel, position: Int ->
            calendarList2.forEachIndexed { index, calendarModel ->
                calendarModel.isSelected = index == position
            }
            adapter.setData(calendarList2)
            adapter.setOnItemClickListener(this@CalendarFragment)
        }
        recyclerView.adapter = adapter
    }

    /**
     * Function to setup calendar for every month
     */
    private fun setUpCalendar() {
        val calendarList = ArrayList<CalendarDateModel>()
        tvDateMonth.text = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (dates.size < maxDaysInMonth) {
            dates.add(monthCalendar.time)
            calendarList.add(CalendarDateModel(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        calendarList2.clear()
        calendarList2.addAll(calendarList)
        adapter.setOnItemClickListener(this@CalendarFragment)
        adapter.setData(calendarList)
    }
    private fun getTasksForSelectedDay(selectedDate: String) {
        // Use a referência do seu banco de dados do Firebase
        val databaseReference = FirebaseDatabase.getInstance().getReference("Tarefas")

        // Iterar sobre os subnós de usuário dentro do nó "Tarefas"
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    // Iterar sobre as tarefas dentro de cada usuário
                    for (taskSnapshot in userSnapshot.children) {
                        val taskDate = taskSnapshot.child("taskDate").value.toString()

                        // Verificar se a tarefa possui a data desejada
                        if (taskDate == selectedDate) {
                            // Aqui você pode criar um objeto ToDoData e adicionar à sua lista
                            val taskId = taskSnapshot.key
                            val taskTitle = taskSnapshot.child("task").value.toString()
                            val taskDescription = taskSnapshot.child("taskDesc").value.toString()
                            val taskTime = taskSnapshot.child("taskTime").value.toString()

                            if (!taskId.isNullOrEmpty() && !taskTitle.isNullOrEmpty()) {
                                val todoTask = ToDoData(taskId, taskTitle, taskDescription, taskDate, taskTime)
                                mList.add(todoTask)
                            }
                        }
                    }
                }

                // Atualize o adapter após obter as tarefas
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Trate erros de consulta do Firebase
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}