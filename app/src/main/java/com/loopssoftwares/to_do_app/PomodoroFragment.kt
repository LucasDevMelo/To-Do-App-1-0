package com.loopssoftwares.to_do_app

import android.app.Dialog
import android.os.Binder
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.loopssoftwares.to_do_app.databinding.FragmentHomeBinding
import com.loopssoftwares.to_do_app.databinding.FragmentPomodoroBinding

class PomodoroFragment : Fragment() {

    private lateinit var binding: FragmentPomodoroBinding
    private var timeSelected : Int = 0
    private var timeCountDown: CountDownTimer? = null
    private var timeProgress = 0
    private var pauseOffSet: Long = 0
    private var isStart = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPomodoroBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnAdd.setOnClickListener {
            setTimeFunction()
        }

        binding.btnPlayPause.setOnClickListener {
            startTimerSetup()
        }

        binding.ibReset.setOnClickListener {
            resetTime()
        }

        binding.tvAddTime.setOnClickListener {
            addExtraTime()
        }

        return view
    }

    private fun addExtraTime()
    {
        if (timeSelected!=0)
        {
            timeSelected+=15
            binding.pbTimer.max = timeSelected
            timePause()
            startTimer(pauseOffSet)
            Toast.makeText(context,"15 sec added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetTime()
    {
        if (timeCountDown!=null)
        {
            timeCountDown!!.cancel()
            timeProgress=0
            timeSelected=0
            pauseOffSet=0
            timeCountDown=null
            binding.btnPlayPause.text ="Start"
            isStart = true
            binding.pbTimer.progress = 0
            binding.tvTimeLeft.text = "0"
        }
    }

    private fun timePause()
    {
        if (timeCountDown!=null)
        {
            timeCountDown!!.cancel()
        }
    }

    private fun startTimerSetup()
    {
        val startBtn: Button = binding.btnPlayPause
        if (timeSelected>timeProgress)
        {
            if (isStart)
            {
                startBtn.text = "Pause"
                startTimer(pauseOffSet)
                isStart = false
            }
            else
            {
                isStart =true
                startBtn.text = "Resume"
                timePause()
            }
        }
        else
        {
            Toast.makeText(context,"Enter Time",Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer(pauseOffSetL: Long)
    {
        val progressBar = binding.pbTimer
        progressBar.progress = timeProgress
        timeCountDown = object :CountDownTimer(
            (timeSelected*1000).toLong() - pauseOffSetL*1000, 1000)
        {
            override fun onTick(p0: Long) {
                timeProgress++
                pauseOffSet = timeSelected.toLong()- p0/1000
                progressBar.progress = timeSelected-timeProgress
                val timeLeftTv:TextView = binding.tvTimeLeft
                timeLeftTv.text = (timeSelected - timeProgress).toString()
            }

            override fun onFinish() {
                resetTime()
                Toast.makeText(context,"Times Up!", Toast.LENGTH_SHORT).show()
            }

        }.start()
    }


    private fun setTimeFunction()
    {
        val timeDialog = Dialog(this.requireContext())
        timeDialog.setContentView(R.layout.add_dialog)
        val timeSet = timeDialog.findViewById<EditText>(R.id.etGetTime)
        val timeLeftTv: TextView = binding.tvTimeLeft
        val btnStart: Button = binding.btnPlayPause
        val progressBar = binding.pbTimer
        timeDialog.findViewById<Button>(R.id.btnOk).setOnClickListener {
            if (timeSet.text.isEmpty())
            {
                Toast.makeText(context,"Enter Time Duration",Toast.LENGTH_SHORT).show()
            }
            else
            {
                resetTime()
                timeLeftTv.text = timeSet.text
                btnStart.text = "Start"
                timeSelected = timeSet.text.toString().toInt()
                progressBar.max = timeSelected
            }
            timeDialog.dismiss()
        }
        timeDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(timeCountDown!=null)
        {
            timeCountDown?.cancel()
            timeProgress=0
        }
    }

}