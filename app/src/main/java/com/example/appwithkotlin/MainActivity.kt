package com.example.appwithkotlin

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var Rv: RecyclerView
    private lateinit var Addbtn: FloatingActionButton
    private lateinit var done: Button
    private lateinit var addTaskTextField: TextInputLayout
    private lateinit var addTaskLayout: LinearLayout
    var taskLayout = false
    private lateinit var courseModalArrayList: ArrayList<DataModal>
    private lateinit var sqliteDb: DbHelper
    private var adapter: AdapterFile? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContentView(R.layout.activity_main)
        sqliteDb = DbHelper(this)

        Rv = findViewById(R.id.mainRv)
        Addbtn = findViewById(R.id.addFloatingBtn)
        done = findViewById(R.id.addTaskBtn)
        addTaskTextField = findViewById(R.id.addTaskField)
        addTaskLayout = findViewById(R.id.topLayout)


        Rv.layoutManager = LinearLayoutManager(this)
        adapter = AdapterFile()
        Rv.adapter = adapter


        Addbtn.setOnClickListener {
            if (taskLayout==false){
                addTaskLayout.visibility= View.VISIBLE
                taskLayout=true
            }else{
                addTaskLayout.visibility= View.GONE
                taskLayout=false
            }


        }

        done.setOnClickListener {
            insertData()
        }



        //touchh

        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                Toast.makeText(this@MainActivity, "on Move", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val dbhelper = DbHelper(this@MainActivity)

                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                val dataModal = courseModalArrayList[position]
                //get the id of view from DataModal class
                dbhelper.deleteData(dataModal.id)
                courseModalArrayList.removeAt(position)
                adapter!!.notifyItemRemoved(position)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                val itemView = viewHolder.itemView
                val background = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_delete)
                val p = Paint()
                if (dX > 0) {
                    /* Set your color for positive displacement */
                    // Draw Rect with varying right side, equal to displacement dX
                    c.drawRect(itemView.left.toFloat(), itemView.top.toFloat(), dX,
                            itemView.bottom.toFloat(), p)
                } else {
                    /* Set your color for negative displacement */
                    // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                    c.drawRect(itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), p)
                }
                val itemHeight = itemView.bottom - itemView.top
                val intrinsicHeight = background!!.intrinsicHeight
                val iconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val iconMargin = (itemHeight - intrinsicHeight) / 2
                val iconLeft = itemView.right - iconMargin - background!!.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                val iconBottom = iconTop + intrinsicHeight
                background!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background!!.draw(c)
                // Fade out the view as it is swiped out of the parent's bounds
                val alpha = 1.0f - Math.abs(dX) / itemView.width.toFloat()
                itemView.alpha = alpha
                itemView.translationX = dX
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(Rv)


        // get all data from SQLite db


        //touchh

        displayData()

    }

    private fun displayData() {
        val stdList = sqliteDb.getAllData()
        courseModalArrayList=stdList
        Log.e("pppp", "${stdList.size}")
        adapter?.addItems(this,courseModalArrayList)

    }



    private fun insertData() {
        val title = addTaskTextField.editText?.text.toString()
        if (title.isEmpty()) {
            Toast.makeText(this, "please add", Toast.LENGTH_SHORT).show()

        } else {

            val res = sqliteDb.insertData(title, "unread")

            if (res > -1) {
                Toast.makeText(this, " added", Toast.LENGTH_SHORT).show()
                addTaskTextField.editText?.text?.clear()
              //  displayData()
                val id = sqliteDb.getLastInertedId()

                val newData:DataModal
                newData = DataModal(id = id.toString(),Title = title,"unread",false)

                courseModalArrayList.add(0,newData)
                adapter?.notifyItemInserted(0)
                adapter?.notifyItemRangeChanged(0,courseModalArrayList.size)


            } else {
                Toast.makeText(this, "not added", Toast.LENGTH_SHORT).show()

            }

        }
    }

}


