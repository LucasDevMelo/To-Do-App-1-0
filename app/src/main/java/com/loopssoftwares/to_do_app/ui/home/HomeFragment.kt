package com.loopssoftwares.to_do_app.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.loopssoftwares.to_do_app.databinding.FragmentHomeBinding
import com.loopssoftwares.to_do_app.fragments.AddToDoPopUpFragment
import com.loopssoftwares.to_do_app.utils.ToDoAdapter
import com.loopssoftwares.to_do_app.utils.ToDoData

class HomeFragment : Fragment(), AddToDoPopUpFragment.DialogNextBtnClickListener,
    ToDoAdapter.ToDoAdapterClicksInterface{


    private lateinit var auth : FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private var popUpFragment : AddToDoPopUpFragment? = null
    private lateinit var adapter: ToDoAdapter
    private lateinit var mList: MutableList<ToDoData>
    lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvents()
    }

//    private fun initNavigation(){
//        val navHostFragment =
//            supportFragmentManeger.findFragmentById(R.id.nav_host_fragment2) as NavHostFragment
//        navControler = navHostFragment.navController
//        NavigationUI.setupWithNavController(binding.btnv, navController)
//
//    }

    private fun registerEvents(){
        binding.addBtnHome.setOnClickListener {
            if(popUpFragment != null)
                childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            popUpFragment = AddToDoPopUpFragment()
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(
                childFragmentManager,
                AddToDoPopUpFragment.TAG
            )
        }
    }

    private fun init(view : View){
        database = FirebaseDatabase.getInstance()
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        databaseRef = FirebaseDatabase.getInstance().getReference("Tarefas").child(uid.toString())

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = ToDoAdapter(mList)
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (taskSnapshot in snapshot.children) {
                    val taskId = taskSnapshot.key
                    val taskTitle = taskSnapshot.child("task").value.toString()
                    val taskDescription = taskSnapshot.child("taskDesc").value.toString()
                    val taskDate = taskSnapshot.child("taskDate").value.toString()
                    val taskTime = taskSnapshot.child("taskTime").value.toString()

                    if (!taskId.isNullOrEmpty() && !taskTitle.isNullOrEmpty()) {
                        val todoTask = ToDoData(taskId, taskTitle, taskDescription, taskDate, taskTime)
                        mList.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSaveTask(todo: String, todoEt: TextInputEditText, todoEt2: TextInputEditText, selected_date_text: TextView, selected_time_text: TextView) {
        val taskName = todoEt.text.toString()
        val taskDescription = todoEt2.text.toString()
        val taskTheId = databaseRef.push().key!!
        val taskTheDate = selected_date_text.text.toString()
        val taskTheTime = selected_time_text.text.toString()


        val theTask = ToDoData(taskTheId, taskName, taskDescription, taskTheDate, taskTheTime) // Crie um objeto ToDoData com título e descrição

        databaseRef.child(taskTheId).setValue(theTask).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Tarefa salva com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            todoEt.text = null
            todoEt2.text = null
            selected_date_text.text = null
            selected_time_text.text = null
            popUpFragment!!.dismiss()
        }
    }


    override fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText, todoEt2: TextInputEditText, selected_date_text: TextView, selected_time_text: TextView) {
        val map = HashMap<String,Any>()
            map[toDoData.taskId] = toDoData.task
            databaseRef.updateChildren(map).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(context , "Tarefa atualizada com sucesso!" , Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context , it.exception?.message , Toast.LENGTH_SHORT).show()
                }
                todoEt.text = null
                todoEt2.text = null
                selected_date_text.text = null
                selected_time_text.text = null
                popUpFragment!!.dismiss()
            }
    }

    override fun onDeleteBtnClicked(toDoData: ToDoData) {
        databaseRef.child((toDoData.taskId)).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context , "Tarefa deletada com sucesso!" , Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context , it.exception?.message , Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClicked(toDoData: ToDoData) {
        if (popUpFragment != null)
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()

        popUpFragment = AddToDoPopUpFragment.newInstance(toDoData.taskId , toDoData.task, toDoData.taskDesc, toDoData.taskDate, toDoData.taskTime)
        popUpFragment!!.setListener(this)
        popUpFragment!!.show(childFragmentManager, AddToDoPopUpFragment.TAG)

    }

}