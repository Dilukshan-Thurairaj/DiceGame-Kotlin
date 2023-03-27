package com.example.cw1_dicegame




import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.*
import kotlin.collections.ArrayList


class GameActivity : AppCompatActivity() {

    var popUpWindow: PopupWindow ?= null //Pop Window for lose and win

    var tie = false // tie Boolean
    var didUserThrow = false // Did the user Throw?
    var computerWantReRoll = true // Does Computer Want ReRoll?
    var computerStrategyOn = false // Is the computer Strategy on?

    var noOfRound = 1 // Starts the game with round 1

    //Temporary variables to hold user's each dice number when dice rolled
    var userTempHolder1 =0 ; var userTempHolder2 = 0 ; var userTempHolder3 = 0 ; var userTempHolder4 = 0 ; var userTempHolder5 = 0

    //Temporary variables to hold computer's each dice number when dice rolled
    var compTempHolder1 =0 ; var compTempHolder2 = 0 ; var compTempHolder3 = 0 ; var compTempHolder4 = 0 ; var compTempHolder5 = 0

    //No of wins by human and computer
    var userWinCount = 0
    var compWinCount = 0

    //Total score of the game for both human and computer
    var userTotalScore =0
    var computerTotalScore = 0

    //Score for each roll
    var userRollScore = 0
    var machineRollScore = 0

    //No of times rolled the dice
    var userNoOfRoll = 0
    var compNoOfRoll = 0


    //Used for re roll
    var imagesSelectedUser = mutableListOf<Int>() //Stores the selected image by user
    var machineReRoll = mutableListOf<Int>() //Stores the computer's selected dices

    lateinit var updateTv: TextView // TextView to update on what's happening in the game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        //List of User side ImageViews
        val userImageView = listOf(
            R.id.userImage1,
            R.id.userImage2,
            R.id.userImage3,
            R.id.userImage4,
            R.id.userImage5
        )

        //List of Computer side ImageViews
        val computerImageView = listOf(
            R.id.computerImage1,
            R.id.computerImage2,
            R.id.computerImage3,
            R.id.computerImage4,
            R.id.computerImage5
        )

        //List of the dice images
        val diceImages = listOf(
            R.drawable.die_face_1,R.drawable.die_face_2,R.drawable.die_face_3,R.drawable.die_face_4,R.drawable.die_face_5,R.drawable.die_face_6
        )

        //When there is an saved instance the below code is executed --> restores after configuration change
        if (savedInstanceState != null){
            //Gets the user's saved dice numbers at roll
            userTempHolder1 = savedInstanceState.getInt("userTemp1")
            userTempHolder2 = savedInstanceState.getInt("userTemp2")
            userTempHolder3 = savedInstanceState.getInt("userTemp3")
            userTempHolder4 = savedInstanceState.getInt("userTemp4")
            userTempHolder5 = savedInstanceState.getInt("userTemp5")

            //Gets the computer's saved dice numbers at roll
            compTempHolder1 = savedInstanceState.getInt("compTemp1")
            compTempHolder2 = savedInstanceState.getInt("compTemp2")
            compTempHolder3 = savedInstanceState.getInt("compTemp3")
            compTempHolder4 = savedInstanceState.getInt("compTemp4")
            compTempHolder5 = savedInstanceState.getInt("compTemp5")

            //Gets the saved Win Count
            userWinCount = savedInstanceState.getInt("userWinCount")
            compWinCount = savedInstanceState.getInt("compWinCount")

            //Gets the saved Total Score of the game
            userTotalScore = savedInstanceState.getInt("userTotalScore")
            computerTotalScore = savedInstanceState.getInt("compTotalScore")

            //Gets the saved Each throw score
            userRollScore = savedInstanceState.getInt("userRollScore")
            machineRollScore = savedInstanceState.getInt("machineRollScore")

            //Gets the saved No of Rolls
            userNoOfRoll = savedInstanceState.getInt("userNoOfRoll")
            compNoOfRoll = savedInstanceState.getInt("compNoOfRoll")

            //Gets the saved image selection by user
            imagesSelectedUser = savedInstanceState.getIntegerArrayList("imagesSelectedUser")!!.toMutableList()

            //Gets the saved machine selected dices
            machineReRoll = savedInstanceState.getIntegerArrayList("machineReRoll")!!.toMutableList()

            //Gets to check whether game is tie
            tie = savedInstanceState.getBoolean("tie")

            //Gets to check whether user threw the dice
            didUserThrow = savedInstanceState.getBoolean("didUserThrow")

            //Gets to check whether computer wants to go for another roll
            computerWantReRoll = savedInstanceState.getBoolean("computerWantReRoll")

            //Checks whether computer is on strategy mode
            computerStrategyOn = savedInstanceState.getBoolean("computerStrategyOn")

            //Gets the number of rounds played
            noOfRound = savedInstanceState.getInt("noOfRound")

            //The below code restores the user's dices which were displayed before configuration change
            val userTempDices = listOf(userTempHolder1,userTempHolder2,userTempHolder3,userTempHolder4,userTempHolder5)
            for(i in userTempDices.indices){
                val index = userTempDices[i] - 1
                if (index >= 0) {
                    findViewById<ImageView>(userImageView[i]).setImageResource(diceImages[index])
                }
            }

            //The below code restores the computer's dices which were displayed before configuration change
            val compTempDices = listOf(compTempHolder1,compTempHolder2,compTempHolder3,compTempHolder4,compTempHolder5)
            for(i in compTempDices.indices){
                val index = compTempDices[i] - 1
                if (index >= 0) {
                    findViewById<ImageView>(computerImageView[i]).setImageResource(diceImages[index])
                }
            }

            //The selected dices remain the same after configuration change
            for(i in imagesSelectedUser){
                    findViewById<ImageView>(userImageView[i]).setBackgroundColor(Color.parseColor("#0AE11D"))
            }

            //After configuration change enabling the image selection for user for his re rolls
            if(userNoOfRoll>0) {
                for (i in userImageView) {
                    findViewById<ImageView>(i).isClickable = true
                    findViewById<ImageView>(i).isFocusable = true
                    findViewById<ImageView>(i).setOnClickListener {
                        selectImage(findViewById<ImageView>(i), imagesSelectedUser)
                    }
                }
            }
        }

        //Getting the winCount Explicit intent from the Main activity class
        userWinCount = intent.getIntExtra("setUserWins",0)
        compWinCount = intent.getIntExtra("setCompWins",0)

        //Getting the intent of the final score set by user
        val setScoreIntent = intent.getStringExtra("finalScore")

        //Displaying the score of the game
        val scoreTv = findViewById<TextView>(R.id.scoreText)
        scoreTv.text = "User Score:$userTotalScore\nComputer Score:$computerTotalScore"

        //Displaying the wins of the game
        val winsTv = findViewById<TextView>(R.id.noOfWins)
        winsTv.text = "H:$userWinCount/C:$compWinCount"

        //Updates about the game
        updateTv = findViewById(R.id.gameUpdateText)

        //Displays the round number
        val noOfRoundTv = findViewById<TextView>(R.id.roundText)
        noOfRoundTv.text = "Round:$noOfRound"

        //The two buttons
        val throwBtn = findViewById<Button>(R.id.throwBtn)
        val scoreBtn = findViewById<Button>(R.id.scoreBtn)


        //FUnction to make the images non-clickable
        fun notClickable(){
            for (i in userImageView) {
                findViewById<ImageView>(i).isClickable = false
                findViewById<ImageView>(i).isFocusable = false
            }
        }

        //Setting listener to throw button
        throwBtn.setOnClickListener {
            //Initial roll --> first Roll
            userRollScore = userRoll(diceImages,userImageView,imagesSelectedUser)

            //If Computer Wants to Re-Roll it can re roll, else only one throw is done
            if (computerWantReRoll) {
                machineRollScore = computerRoll(diceImages, computerImageView)
            }
            //Updating about the game
            updateTv.setText(R.string.updateThrow)

            //No of rolls by the user
            userNoOfRoll++

            //The strategy of computer (triggers when user total score is higher than computer total score)
            computerStrategy(setScoreIntent)

            //Is triggered for re-rolls
            when (userNoOfRoll) {
                //When user has thrown the dices for the 1st and 2nd time
                1,2 -> {
                    //If tie not equals true the below code executes
                    if (!tie) {
                        //Making the images clickable and giving them listener
                        for (i in userImageView) {
                            //If tie == false
                            findViewById<ImageView>(i).isClickable = true
                            findViewById<ImageView>(i).isFocusable = true
                            findViewById<ImageView>(i).setOnClickListener {
                                //Making images able to select and storing the selected to an list
                                selectImage(findViewById<ImageView>(i), imagesSelectedUser)
                            }
                        }
                    }

                    //Now computer can select dices to re-roll
                    computerReRoll(machineReRoll)
                    //Computer number of rolls
                    compNoOfRoll++
                }
                //Updating score and stopping Re-rolls
                3 -> {
                    //Making images non-clickable
                    notClickable()

                    //Updating the round and displaying it
                    noOfRound +=1
                    noOfRoundTv.text = "Round:$noOfRound"

                    //Updating total score
                    userTotalScore += userRollScore
                    computerTotalScore += machineRollScore

                    //Method to find the winner of the game
                    findWinner(userTotalScore,computerTotalScore,setScoreIntent)

                    //Number of rolls for both computer and human is set to 0 --> after the maximum of 3 rolls
                    userNoOfRoll = 0
                    compNoOfRoll = 0

                    //Update the game score after 3 rolls
                    scoreTv.text = "User Score: $userTotalScore \n Computer Score: $computerTotalScore"

                    //User has not thrown a dice to score but after 3 rolls score is automatically updated.
                    //Therefore, didUserThrow is set to false, so score button is not clicked again to add-up the score.
                    didUserThrow = false

                    //Computer should be able to roll again
                    computerWantReRoll = true

                    //Update about the game
                    updateTv.setText(R.string.updateScore)

                }
            }

        }

        //Button to score the roll
        scoreBtn.setOnClickListener {
            //Computer strategy is triggered when user total score is higher than computer total score
            computerStrategy(setScoreIntent)

            //If user threw the dice --> didUserThrow = true
            if (didUserThrow) {
                // if tie is not equal to true
                if (!tie) {
                    when (compNoOfRoll) {
                        //Generate random re roll for computer
                        1 -> {
                            //Computer can go for a re roll of maximum twice
                            for (i in 1..compNoOfRoll + 1) {
                                //Triggers the function that randomly generates the dices to be held before throw
                                computerReRoll(machineReRoll)
                                if (machineReRoll.isNotEmpty()) {
                                    //Calculate the machine roll score after the re rolls
                                    machineRollScore = computerRoll(diceImages, computerImageView)
                                }
                            }
                        }

                        2 -> {
                            //Remaining re-roll one
                            //Randomly says whether to go to re roll
                            computerReRoll(machineReRoll)
                            machineRollScore = computerRoll(diceImages, computerImageView)
                        }
                    }
                }
                //Making the images non-clickable for user
                notClickable()

                //Update about the game
                updateTv.setText(R.string.updateScore)

                //Updating the round number
                noOfRound +=1
                noOfRoundTv.text = "Round:$noOfRound"

                //Updating the score
                userTotalScore += userRollScore
                computerTotalScore += machineRollScore

                //Checking whether there are winners
                findWinner(userTotalScore,computerTotalScore,setScoreIntent)

                //Displaying the score of the game
                scoreTv.text = "User Score: $userTotalScore \n Computer Score: $computerTotalScore"

                //Removing the background color
                removeBackground(userImageView)

                //setting the number of rolls to 0 --. so next time can calculate from start
                userNoOfRoll = 0
                compNoOfRoll = 0

                //Making the did user throw to false so the score button is not clicked twice to double the total
                didUserThrow = false
            }
            //Making a Toast message to say the to throw the dice first and then score total
            else{
                Toast.makeText(this,"Please throw the dice to update score",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun computerRoll(diceImages: List<Int>, computerImageView: List<Int>): Int {
        //Computer Score
        var computerScore = 0
        val random = Random()
        var randomNum:Int

        //For each computer's imageView
        for (i in computerImageView.indices) {
            randomNum = random.nextInt(6)
            when (i) {
                0 -> {
                    //If the image is not selected by computer
                    if (!machineReRoll.contains(i)) {
                        //Generate a random image with dice number
                        findViewById<ImageView>(computerImageView[i]).setImageResource(diceImages[randomNum])
                        //temporary variable to hold the random number generated
                        compTempHolder1 = randomNum + 1
                        //Adding the score
                        computerScore += compTempHolder1
                    }
                    //If the image is selected, image doesn't change and the previously dice number is added as score
                    else {
                            computerScore += compTempHolder1
                        }
                    }
                //The above step repeats for all the image views
                1 -> {
                        if (!machineReRoll.contains(i)) {
                            findViewById<ImageView>(computerImageView[i]).setImageResource(
                                diceImages[randomNum]
                            )
                            compTempHolder2 = randomNum + 1
                            computerScore += compTempHolder2
                        } else {
                            computerScore += compTempHolder2
                        }

                }
                2 -> {
                        if (!machineReRoll.contains(i)) {
                            findViewById<ImageView>(computerImageView[i]).setImageResource(
                                diceImages[randomNum]
                            )
                            compTempHolder3 = randomNum + 1
                            computerScore += compTempHolder3
                        } else {
                            computerScore += compTempHolder3
                        }

                }
                3 -> {
                        if (!machineReRoll.contains(i)) {
                            findViewById<ImageView>(computerImageView[i]).setImageResource(
                                diceImages[randomNum]
                            )
                            compTempHolder4 = randomNum + 1
                            computerScore += compTempHolder4

                        } else {
                            computerScore += compTempHolder4
                        }
                }
                4 -> {
                        if (!machineReRoll.contains(i)) {
                            findViewById<ImageView>(computerImageView[i]).setImageResource(
                                diceImages[randomNum]
                            )
                            compTempHolder5 = randomNum + 1
                            computerScore += compTempHolder5

                        } else {
                            computerScore += compTempHolder5
                        }
                }
            }
        }
        //Clear the images selected by computer --> So it doesn't be the same for next roll
        machineReRoll.clear()
        //Returning the score
        return computerScore
    }


    private fun userRoll(diceImages: List<Int>, imageView: List<Int>, imagesSelectedUser: MutableList<Int>) : Int{

        //Setting it to show that user has rolled the dice
        didUserThrow = true
        //Score the roll
        var userScore = 0

        //Random number generation
        val random = Random()
        var randomNum:Int

        //For each user's imageView
        for (i in imageView.indices) {
            randomNum = random.nextInt(6)
            when (i) {
                0 -> {
                    //If the image is not selected by user
                    if (!imagesSelectedUser.contains(i)) {
                        //Generate a random image with dice number
                        findViewById<ImageView>(imageView[i]).setImageResource(diceImages[randomNum])
                        //temporary variable to hold the random number generated
                        userTempHolder1 = randomNum + 1
                        //Adding the score
                        userScore += userTempHolder1
                    }
                    //If the image is selected, image doesn't change and the previously dice number is added as score
                    else{
                        userScore += userTempHolder1
                    }
                }
                //The above step repeats for all the image views
                1 -> {
                    if (!imagesSelectedUser.contains(i)) {
                        findViewById<ImageView>(imageView[i]).setImageResource(diceImages[randomNum])
                        //temporary variable to hold the random number generated
                        userTempHolder2 = randomNum + 1
                        userScore += userTempHolder2

                    }
                    else{
                        userScore += userTempHolder2
                    }
                }
                2 -> {
                    if (!imagesSelectedUser.contains(i)) {
                        findViewById<ImageView>(imageView[i]).setImageResource(diceImages[randomNum])
                        //temporary variable to hold the random number generated
                        userTempHolder3 = randomNum + 1
                        userScore += userTempHolder3

                    }
                    else{
                        userScore += userTempHolder3
                    }
                }
                3 -> {
                    if (!imagesSelectedUser.contains(i)) {
                        findViewById<ImageView>(imageView[i]).setImageResource(diceImages[randomNum])
                        //temporary variable to hold the random number generated
                        userTempHolder4 = randomNum + 1
                        userScore += userTempHolder4

                    }
                    else{
                        userScore += userTempHolder4
                    }
                }
                4 -> {
                    if (!imagesSelectedUser.contains(i)) {
                        findViewById<ImageView>(imageView[i]).setImageResource(diceImages[randomNum])
                        //temporary variable to hold the random number generated
                        userTempHolder5 = randomNum + 1
                        userScore += userTempHolder5

                    }
                    else{
                        userScore += userTempHolder5
                    }
                }
            }
        }
        //Clear the images selected by user --> So it doesn't be the same for next roll
        imagesSelectedUser.clear()
        //Removing background of the image selected
        removeBackground(imageView)
        //Sending the score of the roll
        return userScore
    }

    //Function to find the winner
    private fun findWinner(userTotalScore: Int, computerTotalScore: Int, scoreIntent: String?) {

        //If user score is greater than the target and more than computer score --> then user wins
        if (userTotalScore >= scoreIntent!!.toInt() && userTotalScore > computerTotalScore){

            //Pop-up window
            val layout = layoutInflater.inflate(R.layout.win_or_lose,null)

            val width = LinearLayout.LayoutParams.MATCH_PARENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT


            val conclusionText = layout.findViewById<TextView>(R.id.gameConclusionText)

            conclusionText.text = "You win!"
            conclusionText.setTextColor(Color.parseColor("#00FF00"))

            //Increasing user win count
            userWinCount += 1

            //Displaying pop-up window
            popUpWindow = PopupWindow(layout,width,height)
            popUpWindow!!.showAtLocation(layout, Gravity.CENTER,0,100)

            //Making the buttons disappear after victory
            val btnLayout = findViewById<ConstraintLayout>(R.id.btnLayout)

            btnLayout.visibility = View.GONE


        }
        //If computer score is greater than the target and more than user score --> then computer wins
        else if (computerTotalScore >= scoreIntent.toInt() && computerTotalScore > userTotalScore){

            //pop-up Window
            val layout = layoutInflater.inflate(R.layout.win_or_lose,null)

            val width = LinearLayout.LayoutParams.MATCH_PARENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT


            val conclusionText = layout.findViewById<TextView>(R.id.gameConclusionText)

            conclusionText.setTextColor(Color.parseColor( "#FF0000"))
            conclusionText.text = "You lose"

            //Increasing the count of computer wins
            compWinCount += 1

            //Displaying pop-up window
            popUpWindow = PopupWindow(layout,width,height)
            popUpWindow!!.showAtLocation(layout, Gravity.CENTER,0,100)

            //Making the buttons disappear
            val btnLayout = findViewById<ConstraintLayout>(R.id.btnLayout)

            btnLayout.visibility = View.GONE


        }
        //Else game is tie
        else if(userTotalScore >= scoreIntent.toInt() && computerTotalScore >= scoreIntent.toInt() && userTotalScore == computerTotalScore){
            //Setting tie to true so re rolls are not allowed
            tie = true
        }
    }

    private fun selectImage(view: View, imagesSelectedUser: MutableList<Int>) {
        //Setting this to false as user has selected dices to be re-rolled
        didUserThrow = false
        if (!tie) {
            when (view.id) {
                //Checking whether user has already selected the image, if again selected --> then image background goes back to normal
                R.id.userImage1 -> {
                    if (imagesSelectedUser.contains(0)) {
                        view.setBackgroundColor(Color.TRANSPARENT)
                        imagesSelectedUser.remove(0)

                    }
                    //Adding the selected image number to the list
                    else {
                        view.setBackgroundColor(Color.parseColor("#0AE11D"))
                        imagesSelectedUser.add(0)

                    }
                }
                //Repeats for all the user image views
                R.id.userImage2 -> {
                    //Checking whether user has already selected the image, if again selected --> then image background goes back to normal
                    if (imagesSelectedUser.contains(1)) {
                        view.setBackgroundColor(Color.TRANSPARENT)
                        imagesSelectedUser.remove(1)
                    } else {
                        view.setBackgroundColor(Color.parseColor("#0AE11D"))
                        imagesSelectedUser.add(1)

                    }
                }
                R.id.userImage3 -> {
                    //Checking whether user has already selected the image, if again selected --> then image background goes back to normal
                    if (imagesSelectedUser.contains(2)) {
                        view.setBackgroundColor(Color.TRANSPARENT)
                        imagesSelectedUser.remove(2)
                    } else {
                        view.setBackgroundColor(Color.parseColor("#0AE11D"))
                        imagesSelectedUser.add(2)
                    }
                }
                R.id.userImage4 -> {
                    //Checking whether user has already selected the image, if again selected --> then image background goes back to normal
                    if (imagesSelectedUser.contains(3)) {
                        view.setBackgroundColor(Color.TRANSPARENT)
                        imagesSelectedUser.remove(3)
                    } else {
                        view.setBackgroundColor(Color.parseColor("#0AE11D"))
                        imagesSelectedUser.add(3)
                    }
                }
                R.id.userImage5 -> {
                    //Checking whether user has already selected the image, if again selected --> then image background goes back to normal
                    if (imagesSelectedUser.contains(4)) {
                        view.setBackgroundColor(Color.TRANSPARENT)
                        imagesSelectedUser.remove(4)
                    } else {
                        view.setBackgroundColor(Color.parseColor("#0AE11D"))
                        imagesSelectedUser.add(4)
                    }
                }
            }

        }
    }

    fun computerReRoll(machineReRoll: MutableList<Int>) {
        // When computer strategy is not true
        if (!computerStrategyOn) {
            //When game is not in tie
            if (!tie) {
                val random = Random()


                when (random.nextInt(2)) {
                    0 -> {
                        //Computer does not want to re-roll
                        computerWantReRoll = false
                    }
                    //true
                    1 -> {
                        //Computer wants to reRoll
                        computerWantReRoll = true
                        //How many number of dices computer would like to keep
                        for (i in 0..(random.nextInt(5) + 1)) {
                            //Which dice to keep
                            val randomDice = random.nextInt(random.nextInt(5) + 1)

                            //Random dices selected (unique)
                            if (!machineReRoll.contains(randomDice)) {
                                //Keeping track of dices that needs to be kept
                                machineReRoll.add(randomDice)
                            }
                        }
                    }
                }
            }
        }
    }

    /* The strategy used by me is triggered when user's total score is greater than computer's Total score,
    When the above statement is true then the computerStrategyOn becomes true an then checks whether machineReRoll is empty(this list stores
    the dices that has to kept when re rolling), if its empty I add all the dices position that are greater than 3 in the current computer throw
    to the machineReRoll list so it does not change in the next roll ( making computer to select all dices that are grater than 3, like an human and throw
    its roll to safe keep large values in order to win the game keep. Else, when machineReRoll is not empty (this means that computer has randomly selected
    few dices before) then I make computer to remove all the dices that has value less than 3 from the list and add dices which have value grater than
    3.
    Advantages --> The computer thinks like human to retain large value
    Disadvantages --> Sometimes the computer can lose to human because it is triggered when user total score is greater than computer total score
     */
    fun computerStrategy(targetScore: String?) {
        val compDicesTemp = listOf<Int>(compTempHolder1,compTempHolder2,compTempHolder3,compTempHolder4,compTempHolder5)
        if ((userTotalScore) > (computerTotalScore)) {

            computerStrategyOn = true
            if (machineReRoll.isEmpty()) {
                for (i in compDicesTemp.indices) {
                    if (compDicesTemp[i] > 3) {
                        machineReRoll.add(i)
                    }
                }
            }
            else {
                val n = machineReRoll.size
                for (i in 0 until n) {
                    if (compDicesTemp[machineReRoll[i]] < 3) {
                        machineReRoll.remove(i)
                    }
                }
                for (i in (compDicesTemp.indices - n)) {
                    if (compDicesTemp[i] > 3) {
                        machineReRoll.add(i)
                    }
                }
            }
        }
        else{
            computerStrategyOn = false
        }


    }

    //Function to remove the background color of images
    fun removeBackground(imageView: List<Int>){
        for(i in imageView){
            findViewById<ImageView>(i).setBackgroundColor(Color.TRANSPARENT)
        }
    }

    //Saving the activity state --> so that it can be restored during a configuration change
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("userWinCount", userWinCount)
        outState.putInt("compWinCount",compWinCount)

        outState.putInt("userTemp1",userTempHolder1)
        outState.putInt("userTemp2",userTempHolder2)
        outState.putInt("userTemp3",userTempHolder3)
        outState.putInt("userTemp4",userTempHolder4)
        outState.putInt("userTemp5",userTempHolder5)

        outState.putInt("compTemp1",compTempHolder1)
        outState.putInt("compTemp2",compTempHolder2)
        outState.putInt("compTemp3",compTempHolder3)
        outState.putInt("compTemp4",compTempHolder4)
        outState.putInt("compTemp5",compTempHolder5)

        outState.putInt("userTotalScore",userTotalScore)
        outState.putInt("compTotalScore",computerTotalScore)

        outState.putInt("userRollScore",userRollScore)
        outState.putInt("machineRollScore",machineRollScore)

        outState.putInt("userNoOfRoll",userNoOfRoll)
        outState.putInt("compNoOfRoll",compNoOfRoll)

        outState.putBoolean("tie", tie)
        outState.putBoolean("didUserThrow" ,didUserThrow)
        outState.putBoolean("computerWantReRoll",computerWantReRoll)
        outState.putBoolean("computerStrategyOn", computerStrategyOn)

        outState.putInt("noOfRound", noOfRound)

        outState.putIntegerArrayList("imagesSelectedUser", ArrayList(imagesSelectedUser))
        outState.putIntegerArrayList("machineReRoll", ArrayList(machineReRoll))
    }



    //When the back button is pressed by the user
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        //Intent of the win count is passed to main activity
        val winsIntent = Intent(this,MainActivity::class.java)
        winsIntent.putExtra("userNoOfWins", userWinCount)
        winsIntent.putExtra("compNoOfWins",compWinCount)

        startActivity(winsIntent)
    }


    //When activity destroys
    override fun onDestroy() {
        super.onDestroy()
        //if not null dismiss
        popUpWindow?.dismiss()
    }


}

