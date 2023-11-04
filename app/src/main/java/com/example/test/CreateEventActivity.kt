package com.example.test

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dateButton: Button
    private lateinit var selectedTime: String
    private lateinit var timeButton: Button
    private lateinit var selectImageButton: Button
    private lateinit var userUploadedImageView: ImageView
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var selectedImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        selectedImageUri = Uri.parse("android.resource://your.package.name/drawable/default_image")

        initDatePicker()
        dateButton = findViewById(R.id.datePickerButton)
        timeButton = findViewById(R.id.timePickerButton)
        selectImageButton = findViewById(R.id.uploadButton)

        dateButton.setOnClickListener {
            datePickerDialog.show()
        }

        timeButton.setOnClickListener {
            openTimePicker()
        }

        selectImageButton.setOnClickListener {
            openImagePicker()
        }

        // Assuming you have the relevant EditText and Button IDs in your activity_main.xml
        val editTitle = findViewById<EditText>(R.id.titleEditText)
        val editDescription = findViewById<EditText>(R.id.descriptionEditText)
        val editDateAndTime = findViewById<Button>(R.id.datePickerButton)
        val editBuildingName = findViewById<EditText>(R.id.locationEditText)
        val editAddress = findViewById<EditText>(R.id.addressEditText)
        val continueButton = findViewById<Button>(R.id.continueButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val menuButton = findViewById<ImageView>(R.id.menuButton)
        val mapIcon = findViewById<ImageView>(R.id.mapIcon)

        val descriptionCounter = findViewById<TextView>(R.id.descriptionCounter)
        val locationCounter = findViewById<TextView>(R.id.locationCounter)

        // Add a TextWatcher to update the description counter
        editDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update the description counter
                val currentCount = s?.length ?: 0
                descriptionCounter.text = "$currentCount/200"
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing here
            }
        })

        // TextWatcher for locationEditText
        editBuildingName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update the location counter
                val currentCount = s?.length ?: 0
                locationCounter.text = "$currentCount/80"
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing here
            }
        })

        continueButton.setOnClickListener {
            // Get the entered data
            val title = editTitle.text.toString()
            val description = editDescription.text.toString()
            val date = dateButton.text.toString()
            val time = timeButton.text.toString()
            val buildingName = editBuildingName.text.toString()
            val address = editAddress.text.toString()

            // Check if the selected image is still the default image
            if (selectedImageUri.toString() == "android.resource://your.package.name/drawable/default_image") {
                // No image selected, set it to null
                selectedImageUri = Uri.EMPTY
            }

            if (title.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty() || buildingName.isEmpty() || address.isEmpty()) {
                // Display a message or toast indicating that all fields must be filled
                // For example:
                Toast.makeText(this@CreateEventActivity, "Please fill out all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Create an Intent to start the next activity
                val intent = Intent(this@CreateEventActivity, CreateEventCategoriesActivity::class.java)

                // Pass the data to the next activity
                intent.putExtra("title", title)
                intent.putExtra("description", description)
                intent.putExtra("date", date)
                intent.putExtra("time", time)
                intent.putExtra("buildingName", buildingName)
                intent.putExtra("address", address)

                // Check if an image is selected
                if (selectedImageUri != null) {
                    // Pass the image URI to the next activity
                    intent.putExtra("imageUri", selectedImageUri.toString())
                }

                // Start the next activity
                startActivity(intent)
            }
        }

        cancelButton.setOnClickListener {
            // If the user clicks the "Cancel" button, open the menu
            val intent = Intent(this@CreateEventActivity, MenuActivity::class.java)
            startActivity(intent)
        }

        menuButton.setOnClickListener {
            // Open the menu activity when the menu button is clicked
            val intent = Intent(this@CreateEventActivity, MenuActivity::class.java)
            startActivity(intent)
        }

        mapIcon.setOnClickListener {
            // Open the map activity when the map button is clicked
            val intent = Intent(this@CreateEventActivity, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            // Get the selected image URI
            selectedImageUri = data.data!!

            // Inflate the card_item.xml layout to find photoImageView
            val cardView: View = layoutInflater.inflate(R.layout.card_item, null)
            userUploadedImageView = cardView.findViewById(R.id.photoImageView)

            // Set the image to userUploadedImageView
            userUploadedImageView.setImageURI(selectedImageUri)
            userUploadedImageView.visibility = View.VISIBLE
        }
    }

    private fun openTimePicker() {
        val cal = Calendar.getInstance()
        val hourOfDay = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                // Convert 24-hour format to 12-hour format with AM/PM
                val amPm = if (hour < 12) "AM" else "PM"
                val displayHour = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm)

                // Update the text of the timeButton
                timeButton.text = selectedTime
            },
            hourOfDay,
            minute,
            false
        )

        timePickerDialog.show()
    }

    private fun initDatePicker() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val dateString = makeDateString(day, month + 1, year)
            dateButton.text = dateString
        }, year, month, day)
    }

    private fun getTodaysDate(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        var month = cal.get(Calendar.MONTH)
        month += 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return getMonthFormat(month) + " " + day + " " + year
    }

    private fun getMonthFormat(month: Int): String {
        return when (month) {
            1 -> "JAN"
            2 -> "FEB"
            3 -> "MAR"
            4 -> "APR"
            5 -> "MAY"
            6 -> "JUN"
            7 -> "JUL"
            8 -> "AUG"
            9 -> "SEP"
            10 -> "OCT"
            11 -> "NOV"
            12 -> "DEC"
            else -> "JAN"
        }
    }

    fun openDatePicker(view: View) {
        datePickerDialog.show()
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }
}