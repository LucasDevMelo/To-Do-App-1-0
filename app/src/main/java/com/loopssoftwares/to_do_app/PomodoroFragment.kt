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
            timeSelected+= 5
            binding.pbTimer.max = timeSelected * 60
            timePause()
            startTimer(pauseOffSet)
            Toast.makeText(context,"5 minuto adicionado", Toast.LENGTH_SHORT).show()
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
            binding.btnPlayPause.text ="Começar"
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
                startBtn.text = "Pausar"
                startTimer(pauseOffSet)
                isStart = false
            }
            else
            {
                isStart =true
                startBtn.text = "Retomar"
                timePause()
            }
        }
        else
        {
            Toast.makeText(context,"Defina o tempo",Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer(pauseOffSetL: Long)
    {
        val progressBar = binding.pbTimer
        progressBar.progress = timeProgress
        timeCountDown = object :CountDownTimer(
            (timeSelected * 60 - timeProgress - pauseOffSetL) * 1000, 1000
        ) {
            override fun onTick(p0: Long) {
                timeProgress++
                pauseOffSet = (timeSelected * 60 - timeProgress) - p0 / 1000

                val minutes = (timeSelected * 60 - timeProgress) / 60
                val seconds = (timeSelected * 60 - timeProgress) % 60
                val formattedTime = String.format("%02d:%02d", minutes, seconds)

                val timeLeftTv: TextView = binding.tvTimeLeft
                timeLeftTv.text = formattedTime
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
                Toast.makeText(context,"Informe a duração do tempo",Toast.LENGTH_SHORT).show()
            }
            else
            {
                resetTime()
                timeLeftTv.text = timeSet.text
                btnStart.text = "Começar"
                timeSelected = timeSet.text.toString().toInt()
                progressBar.max = timeSelected * 60
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