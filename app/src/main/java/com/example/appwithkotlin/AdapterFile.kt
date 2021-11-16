package com.example.appwithkotlin

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterFile : RecyclerView.Adapter<AdapterFile.myViewholdwer>() {

    private var list: ArrayList<DataModal> = ArrayList()
    private lateinit var context: Context

    fun addItems(context:Context,Items: ArrayList<DataModal>) {
        this.list = Items
        this.context = context
        notifyDataSetChanged()
    }

    class myViewholdwer(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskTitleText: TextView
        var taskCheckBox: CheckBox

        init {
            taskTitleText = itemView.findViewById(R.id.task_title)
            taskCheckBox = itemView.findViewById(R.id.task_check)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterFile.myViewholdwer {
        val v = LayoutInflater.from(parent.context).inflate(
                R.layout.taskrecyclerlayout,
                parent,
                false
        )
        return myViewholdwer(v)
    }

    override fun onBindViewHolder(holder: AdapterFile.myViewholdwer, position: Int) {

        val data = list[position]
        holder.taskCheckBox.setTag(position)

        holder.taskTitleText.text = data.Title


        //get current position of checkbox to resolve overlapping problem when scrolling screen
        // Integer pos = (Integer) holder.task_check_box.getTag();

        //set checkbox behavior in current elements

        //get current position of checkbox to resolve overlapping problem when scrolling screen
        // Integer pos = (Integer) holder.task_check_box.getTag();

        //set checkbox behavior in current elements
        if (data.positionHold == true) {
            // holder.taskTitleText.setText(data.getTitle())
              data.positionHold = true
            holder.taskCheckBox.setChecked(true)
            holder.taskCheckBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FF03DAC5")))
            holder.taskTitleText.setTextColor(Color.parseColor("#808080"))
            holder.taskTitleText.setPaintFlags(holder.taskTitleText.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        } else {
            // holder.taskTitleText.setText(data.getTitle())
            data.positionHold = false
            holder.taskCheckBox.setChecked(false)
            holder.taskTitleText.setTextColor(Color.parseColor("#000000"))
            holder.taskCheckBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#e2e2e2")))
            holder.taskTitleText.setPaintFlags(holder.taskTitleText.getPaintFlags() and Paint.STRIKE_THRU_TEXT_FLAG.inv())
        }



        holder.taskCheckBox.setOnClickListener {
            val myDb = DbHelper(context)
            var pos: Int = holder.taskCheckBox.getTag() as Int
            if (list.get(pos).positionHold==false){
                list.get(pos).positionHold = true
                holder.taskCheckBox.isChecked = true
                myDb.updateData(data.id, "read")
                holder.taskCheckBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FF03DAC5")))
                holder.taskTitleText.setTextColor(Color.parseColor("#808080"))
                holder.taskTitleText.setPaintFlags(holder.taskTitleText.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
            }else{
                list.get(pos).positionHold = false
                holder.taskCheckBox.isChecked = false
                myDb.updateData(data.id, "unread")
                holder.taskTitleText.setTextColor(Color.parseColor("#000000"))
                holder.taskCheckBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#e2e2e2")))
                holder.taskTitleText.setPaintFlags(holder.taskTitleText.getPaintFlags() and Paint.STRIKE_THRU_TEXT_FLAG.inv())
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}