package com.example.cw1_dicegame

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    //https://drive.google.com/file/d/18c6r9QlkUfMZycaSbcZWvjDUsTlhTHhZ/view?usp=sharing
    //Video link given above

    //Pop up Window
    var popUpWindow: PopupWindow? = null

    var setUserWin:Int = 0 //No of user wins
    var setCompWin:Int =0 //No of computer wins

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Buttons
        val aboutBtn = findViewById<Button>(R.id.about)
        val newGameBtn = findViewById<Button>(R.id.newGame)

        val head = findViewById<TextView>(R.id.heading)

        //Getting the intent from game activity
        setUserWin = intent.getIntExtra("userNoOfWins",0)
        setCompWin = intent.getIntExtra("compNoOfWins",0)


        //on click listener for new game button
        newGameBtn.setOnClickListener{
            //default score
            val score:String = "101"

            //Building a dialog box to get user's target score
            val dialogBox = AlertDialog.Builder(this)
            dialogBox.setTitle("Change target score")

            //Creating a edit text to enter target score
            val scoreTarget = EditText(this)
            scoreTarget.inputType = InputType.TYPE_CLASS_NUMBER
            scoreTarget.setText(score)

            //adding the set text to dialog box
            dialogBox.setView(scoreTarget)

            //Setting a button in dialog box
            dialogBox.setPositiveButton("OK") { _: DialogInterface, _: Int ->
                //on click the no of wins and target score is sent to game activity
                val finalScore = scoreTarget.text.toString()
                Log.d("myTag",finalScore)

                val gameIntent = Intent(this,GameActivity::class.java)
                gameIntent.putExtra("finalScore",finalScore)
                gameIntent.putExtra("setUserWins",setUserWin)
                gameIntent.putExtra("setCompWins",setCompWin)
                startActivity(gameIntent)

            }

            //Show the dialog box
            dialogBox.show()
        }

        //ABout button
        aboutBtn.setOnClickListener {

            //Pop-up Window
            val layout = layoutInflater.inflate(R.layout.popup_window,null)

            val width = LinearLayout.LayoutParams.MATCH_PARENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT

            val closeBtn = layout.findViewById<ImageButton>(R.id.closeBtn)

            //Displaying the pop up window
            popUpWindow = PopupWindow(layout,width,height)
            popUpWindow!!.showAtLocation(layout,Gravity.CENTER,0,100)


            //Making other view in activity disappear
            aboutBtn.visibility = View.GONE
            head.visibility = View.GONE
            newGameBtn.visibility = View.GONE

            //Adding a close button to close the pop up window and display the view in activity
            closeBtn.setOnClickListener{
                aboutBtn.visibility = View.VISIBLE
                head.visibility = View.VISIBLE
                newGameBtn.visibility = View.VISIBLE
                popUpWindow!!.dismiss()
            }
        }

    }

    //onPause dismissing the pop-up window to avoid leak
    override fun onPause() {
        super.onPause()
        popUpWindow?.dismiss()
    }

}