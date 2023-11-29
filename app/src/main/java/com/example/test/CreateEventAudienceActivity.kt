package com.example.test

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class CreateEventAudienceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event_audiences)

        val checkBoxUndergrad = findViewById<CheckBox>(R.id.checkboxUndergrad)
        val checkBoxGrad = findViewById<CheckBox>(R.id.checkboxGrad)
        val checkBoxFacultyStaff = findViewById<CheckBox>(R.id.checkboxFacultyStaff)
        val checkBoxAlumni = findViewById<CheckBox>(R.id.checkboxAlumni)
        val checkBoxPublicCommunity = findViewById<CheckBox>(R.id.checkboxPublicCommunity)
        val checkBoxFamily = findViewById<CheckBox>(R.id.checkboxFamily)
        val checkBoxProspecStudents = findViewById<CheckBox>(R.id.checkboxProspecStudents)

        val continueButton = findViewById<Button>(R.id.continueButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val menuButton = findViewById<ImageView>(R.id.menuButton)
        val mapIcon = findViewById<ImageView>(R.id.mapIcon)

        continueButton.setOnClickListener {
            // Retrieve data from the previous activity
            val title = intent.getStringExtra("title")
            val description = intent.getStringExtra("description")
            val date = intent.getStringExtra("date")
            val time = intent.getStringExtra("time")
            val buildingName = intent.getStringExtra("buildingName")
            val address = intent.getStringExtra("address")
            val imageUri = intent.getStringExtra("imageUri")

            // Retrieve Categories checkbox information
            val checkboxAcademic = intent.getBooleanExtra("checkboxAcademic", false)
            val checkboxSocial = intent.getBooleanExtra("checkboxSocial", false)
            val checkboxSports = intent.getBooleanExtra("checkboxSports", false)
            val checkboxClubsOrg = intent.getBooleanExtra("checkboxClubsOrg", false)
            val checkboxWorkshops = intent.getBooleanExtra("checkboxWorkshops", false)
            val checkboxVolunteering = intent.getBooleanExtra("checkboxVolunteering", false)
            val checkboxStudentsOnly = intent.getBooleanExtra("checkboxStudentsOnly", false)

            val intent = Intent(this@CreateEventAudienceActivity, CreateEventDetailsActivity::class.java)

            // Pass the data to the next activity
            intent.putExtra("title", title)
            intent.putExtra("description", description)
            intent.putExtra("date", date)
            intent.putExtra("time", time)
            intent.putExtra("buildingName", buildingName)
            intent.putExtra("address", address)
            intent.putExtra("imageUri", imageUri)

            // Pass Categories checkbox information
            intent.putExtra("checkboxAcademic", checkboxAcademic)
            intent.putExtra("checkboxSocial", checkboxSocial)
            intent.putExtra("checkboxSports", checkboxSports)
            intent.putExtra("checkboxClubsOrg", checkboxClubsOrg)
            intent.putExtra("checkboxWorkshops", checkboxWorkshops)
            intent.putExtra("checkboxVolunteering", checkboxVolunteering)
            intent.putExtra("checkboxStudentsOnly", checkboxStudentsOnly)

            // Pass Audience checkbox information
            intent.putExtra("checkboxUndergrad", checkBoxUndergrad.isChecked)
            intent.putExtra("checkboxGrad", checkBoxGrad.isChecked)
            intent.putExtra("checkboxFacultyStaff", checkBoxFacultyStaff.isChecked)
            intent.putExtra("checkboxAlumni", checkBoxAlumni.isChecked)
            intent.putExtra("checkboxPublicCommunity", checkBoxPublicCommunity.isChecked)
            intent.putExtra("checkboxFamily", checkBoxFamily.isChecked)
            intent.putExtra("checkboxProspecStudents", checkBoxProspecStudents.isChecked)

            // Start the next activity
            startActivity(intent)
        }

        cancelButton.setOnClickListener {
            // If the user clicks the "Cancel" button, finish the activity
            finish()
        }

        menuButton.setOnClickListener {
            // Open the menu activity when the menu button is clicked
            val intent = Intent(this@CreateEventAudienceActivity, MenuActivity::class.java)
            startActivity(intent)
        }

        mapIcon.setOnClickListener {
            // Open the map activity when the map button is clicked
            val intent = Intent(this@CreateEventAudienceActivity, MapsActivity::class.java)
            startActivity(intent)
        }

    }
}