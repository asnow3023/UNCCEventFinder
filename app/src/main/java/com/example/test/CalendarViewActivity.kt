package com.example.test

import android.content.Context
import android.content.Intent
import android.media.metrics.Event
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID


class CalendarViewActivity : AppCompatActivity(), CalendarAdapter.OnItemListener {
    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var listView: ListView
    private var allEvents: List<EventData> = mutableListOf()
    private var userEventsDataList: List<UserEventsData> = mutableListOf()
    private val firestore = FirebaseStorageUtil.getFirebaseFireStoreInstance()


    private lateinit var eventTitle: TextView
    private lateinit var eventTime: TextView

    private lateinit var time: LocalTime
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_calendar_view)
        initWidgets()
        //Giving it the current date and time of local pc
        selectedDate = LocalDate.now()
        time = LocalTime.now()

        userId = getAuthorIdFromSharedPreferences() // gets userID from SharedPreference

        setListViewAdapter()
        setMonthView()

        // Find the menu button and set a click listener
        val menuButton = findViewById<ImageView>(R.id.menuButton)
        menuButton.setOnClickListener {
            // Open the MenuActivity when the menu button is clicked
            val intent = Intent(this@CalendarViewActivity, MenuActivity::class.java)
            startActivity(intent)
        }

        // Find the map icon and set a click listener
        val mapIcon = findViewById<ImageView>(R.id.mapIcon)
        mapIcon.setOnClickListener {
            // Open the MapActivity when the map icon is clicked
            val intent = Intent(this@CalendarViewActivity, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        setListViewAdapter()
    }

    //Sets the adapter and finds which events are on that day via eventsForDate
    private fun setListViewAdapter() {
        runBlocking {
            val dailyEvents: ArrayList<EventData> = eventsForDateUser(selectedDate)
            //val dailyEvents: ArrayList<EventData> = eventsForDate(selectedDate)
            val calendarListAdapter = CalendarListAdapter(this@CalendarViewActivity, dailyEvents)
            listView.adapter = calendarListAdapter
        }
    }


    //Queries the database to see which events are on which day and adds them to a temp array if dates match
    fun eventsForDate(date: LocalDate): ArrayList<EventData> {
        //Gets database access
        fetchEventDataFromFirestore()
        val events = ArrayList<EventData>()
        //Checking if dates match to put in the new array
        for (event in allEvents) {
            if (LocalDate.parse(event.date) == date)
                events.add(event)
        }
        return events
    }

    suspend fun eventsForDateUser(date: LocalDate): ArrayList<EventData> {

        val events = ArrayList<EventData>()

        if (userId != null) {
            fetchEventDataFromUser(userId)

            //for each list of event data for a single user
            for (eventData in userEventsDataList) {
                val sessiondId = eventData.eventSessionId
                val eventId = eventData.eventId

                //queries and fetches session by current session id
                val session = fetchSessionBySessionId(sessiondId)
                //if sessionData's staring date is equal to the selected date
                if(session != null){
                    val formatter = DateTimeFormatter.ofPattern("MMM d yyyy")
                    val sessionDate = LocalDate.parse(session.startDate, formatter)
                    if (LocalDate.parse(sessionDate.toString()) == date) {
                        val event: EventData? = fetchEventByEventId(eventId)
                        if (event != null) {
                            events.add(event)
                        }
                    }
                }
            }
//            for (event in allEvents) {
//                if (LocalDate.parse(event.date) == date) //if the event's date is equal to the selectedDate
//                    events.add(event)
//            }
        }
        return events
    }

    private fun fetchEventDataFromFirestore() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val querySnapshot: QuerySnapshot = FirebaseFirestore.getInstance()
                    .collection("Events")
                    .get()
                    .await()

                val events = querySnapshot.toObjects(EventData::class.java)
                allEvents = events // Update the allEvents list
                println("Fetched ${events.size} events from Firestore")
            } catch (e: Exception) {
                // Handle exceptions
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchEventDataFromUser(userId: String) {
        return withContext(Dispatchers.IO) {
            val eventCollectionRef = firestore.collection("UserEvents")
            val query = eventCollectionRef.whereEqualTo("userId", userId)

            try {
                val querySnapshot = Tasks.await(query.get())
                val userEvents = querySnapshot.toObjects(UserEventsData::class.java)
                userEventsDataList = userEvents
            } catch (exception: Exception) {
                Log.e("UserEvent FetchById Request", "UserEvent Fetch Failed: $exception")
            }
        }
    }

    suspend fun fetchSessionBySessionId(sessionId: String): EventSessionData? {
        return try {
            withContext(Dispatchers.IO) {

                val collectionRef = firestore.collection("EventSessions")
                val query = collectionRef.document(sessionId)

                val querySnapshot = Tasks.await(query.get())
                querySnapshot.toObject(EventSessionData::class.java)!!
            }
        } catch (exception: Exception) {
            Log.e("Session FetchById Request", "Session Fetch Failed: " + exception)
            null
        }

    }

    suspend fun fetchEventByEventId(eventId: String): EventData? {
        return try {
            withContext(Dispatchers.IO) {

                val collectionRef = firestore.collection("Events")
                val query = collectionRef.document(eventId)

                val querySnapshot = Tasks.await(query.get())
                querySnapshot.toObject(EventData::class.java)!!
            }
        } catch (exception: Exception) {
            Log.e("Event FetchById Request", "Event Fetch Failed: " + exception)
            null
        }
    }


    //Find the recycler view and text view on startup and make them variables
    private fun initWidgets() {
        listView = findViewById(R.id.calendarListView)
        monthYearText = findViewById(R.id.monthYearTV)
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
    }

    //Actually create the view based on the month out of 7 columns
    private fun setMonthView() {
        monthYearText.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)
        val calendarAdapter = CalendarAdapter(daysInMonth, this)
        val layoutManager = GridLayoutManager(applicationContext, 7)
        calendarRecyclerView.layoutManager = layoutManager
        calendarRecyclerView.adapter = calendarAdapter
    }

    //Go back a month button
    fun previousMonthAction(view: View) {
        selectedDate = selectedDate.minusMonths(1)
        setListViewAdapter()
        setMonthView()
    }

    //Go forward a month button
    fun nextMonthAction(view: View) {
        selectedDate = selectedDate.plusMonths(1)
        setListViewAdapter()
        setMonthView()
    }

    //Temp click listener
    override fun onItemClick(position: Int, date: LocalDate?) {
        if (date != null) {
            selectedDate = date
            setListViewAdapter()
            setMonthView()
        }
    }

    //gets the userId initialized during login
    private fun getAuthorIdFromSharedPreferences(): String {
        val sharedPreferences = applicationContext.getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val authorId = sharedPreferences.getString("authorId", null)
        return authorId.toString()
    }

}