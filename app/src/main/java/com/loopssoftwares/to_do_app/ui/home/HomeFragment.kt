package com.loopssoftwares.to_do_app.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
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
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Tarefas").child(auth.currentUser?.uid.toString())

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = ToDoAdapter(mList)
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase(){
        databaseRef.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for(taskSnapshot in snapshot.children){
                    val todoTask = taskSnapshot.key?.let{
                        ToDoData(it , taskSnapshot.value.toString())
                    }

                    if (todoTask != null){
                        mList.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context , error.message , Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onSaveTask(todo: String, todoEt: TextInputEditText) {

        databaseRef.push().setValue(todo).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context , "Tarefa salva com sucesso !!" , Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context , it.exception?.message , Toast.LENGTH_SHORT).show()
            }
            todoEt.text = null
            popUpFragment!!.dismiss()
        }
    }

    override fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText) {
        val map = HashMap<String,Any>()
            map[toDoData.taskId] = toDoData.task
            databaseRef.updateChildren(map).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(context , "Tarefa atualizada com sucesso!" , Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context , it.exception?.message , Toast.LENGTH_SHORT).show()
                }
                todoEt.text = null
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

        popUpFragment = AddToDoPopUpFragment.newInstance(toDoData.taskId , toDoData.task)
        popUpFragment!!.setListener(this)
        popUpFragment!!.show(childFragmentManager, AddToDoPopUpFragment.TAG)

    }

}