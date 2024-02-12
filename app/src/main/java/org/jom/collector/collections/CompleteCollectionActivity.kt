package org.jom.collector.collections

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.jom.collector.DashboardActivity
import org.jom.collector.R
import org.jom.collector.profile.ViewProfileActivity

class CompleteCollectionActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var backButton: ImageView
    private lateinit var complete: Button

    // status variables for validations
    var actual_status = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_collection)

        // initialize text views
        val collection_id: TextView = findViewById(R.id.collection_id)
        val name: TextView = findViewById(R.id.supplier_name)
        val phone: TextView = findViewById(R.id.phone)
        val address: TextView = findViewById(R.id.address)
        val date: TextView = findViewById(R.id.date)
        val time: TextView = findViewById(R.id.time)
        val amount: TextView = findViewById(R.id.amount)
        val payment: TextView = findViewById(R.id.payment)

        // get intent instance and bundle
        var i = intent
        var extras = i.extras!!

        // get put data from bundle and assign to text views
        collection_id.text = extras.getString("id")
        name.text = extras.getString("name")
        phone.text = extras.getString("phone")
        address.text = extras.getString("address")
        date.text = extras.getString("date")
        time.text = extras.getString("time")
        amount.text = extras.getString("amount")
        payment.text = extras.getString("payment")

        // get actual input and error text
        val actual: EditText = findViewById(R.id.actual)
        var actualError: TextView = findViewById(R.id.actualError)

        // Error handling
        fun actual_status_func(actual: String): Boolean {
            val trimmedActual = actual.trim()
            if (trimmedActual.isEmpty()) {
                actualError.text = "Actual amount cannot be empty"
                actual_status = false
                return false
            } else if (trimmedActual.toInt() < 1) {
                actualError.text = "Actual amount cannot be less than 0"
                actual_status = false
                return false
            } else {
                actualError.text = ""
                actual_status = true
                return true
            }
        }

        // handle onInput change errors
        val actualTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed
            }

            override fun afterTextChanged(s: Editable?) {
                actual_status_func(s.toString())
            }
        }
        actual.addTextChangedListener(actualTextWatcher)

        //back
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { this.onBackPressed() }

        // nav and status bar color
        window.navigationBarColor = ContextCompat.getColor(this, R.color.lightPrimaryColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightPrimaryColor)

        // complete collection
        complete = findViewById(R.id.complete)
        complete.setOnClickListener {
            // check if actual amount satisfy required conditions
            if (!actual_status_func(actual.text.toString())) {
                actual.requestFocus()
            }

            if (actual_status) {
                // put actual value to extra
                extras.putString("actual", actual.text.toString())

                // sweet alert for confirmation
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText("You won't be able to revert this!")
                    .setConfirmText("Complete!")
                    .showCancelButton(true)
                    .setCancelText("Cancel")
                    .setConfirmClickListener { sDialog ->
                        sDialog.setTitleText("Completed!")
                            .setContentText("Collection marked as complete and notify to supplier to verify amount.")
                            .setConfirmText("Ok")
                            .showCancelButton(false)
                            .setConfirmClickListener {
                                // complete collection with actual amount
                                val intent = Intent(this, VerifyAmountActivity::class.java)
                                intent.putExtras(extras)
                                startActivity(intent)
                            }
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    }.show()
            }
        }

        // bottom nav handler
        bottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.nav_assigned
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)

                    item.setIcon(R.drawable.icon_home)
                    true
                }

                R.id.nav_completed -> {
                    item.setIcon(R.drawable.icon_completed)
                    true
                }

                R.id.nav_assigned -> {
                    item.setIcon(R.drawable.icon_assigned)
                    true
                }

                R.id.nav_user -> {
                    val intent = Intent(this, ViewProfileActivity::class.java)
                    startActivity(intent)

                    item.setIcon(R.drawable.icon_user)
                    true
                }

                else -> false
            };
        }
    }
}