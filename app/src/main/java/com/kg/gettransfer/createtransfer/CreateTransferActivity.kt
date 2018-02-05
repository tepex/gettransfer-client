package com.kg.gettransfer.createtransfer


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.kg.gettransfer.R
import com.kg.gettransfer.login.LoginActivity
import com.kg.gettransfer.models.Location
import com.kg.gettransfer.modules.DBModule
import com.kg.gettransfer.modules.TransfersModule
import com.kg.gettransfer.modules.TransportTypesModule
import com.kg.gettransfer.modules.network.Api
import com.kg.gettransfer.modules.network.PassengerProfile
import com.kg.gettransfer.modules.network.NewTransfer
import com.kg.gettransfer.views.TransportTypesAdapter
import io.realm.Realm


/**
 * Created by denisvakulenko on 25/01/2018.
 */


class CreateTransferActivity : AppCompatActivity() {
    private val TAG = "CreateTransferActivity"

    //    private val api by lazy {
//        Api.api
//    }
//    private var disposable: Disposable? = null
    private var realm: Realm? = null

    private val transportTypesModule: TransportTypesModule = TransportTypesModule
    private val transfersModule: TransfersModule = TransfersModule

    val etFrom by lazy { findViewById<EditText>(R.id.etFrom) }
    val etTo by lazy { findViewById<EditText>(R.id.etTo) }

    val etDate by lazy { findViewById<EditText>(R.id.etDate) }
    val etTime by lazy { findViewById<EditText>(R.id.etTime) }
    val etPassengers by lazy { findViewById<EditText>(R.id.etPassengers) }

    val etEmail by lazy { findViewById<EditText>(R.id.etEmail) }
    val etPhone by lazy { findViewById<EditText>(R.id.etPhone) }
    val fab by lazy { findViewById<FloatingActionButton>(R.id.fabTransfer) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createtransfer)

        realm = DBModule.create(applicationContext)
        val api = Api.api

        initListTransportTypes()

//        transfersModule.updateTransfers()

        installEditTextWatcher()
    }


    private fun installEditTextWatcher() {
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                fab.visibility = if (validateFields()) View.VISIBLE else View.GONE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

        etFrom.addTextChangedListener(textWatcher)
        etTo.addTextChangedListener(textWatcher)

        etDate.addTextChangedListener(textWatcher)
        etTime.addTextChangedListener(textWatcher)
        etPassengers.addTextChangedListener(textWatcher)

        etEmail.addTextChangedListener(textWatcher)
        etPhone.addTextChangedListener(textWatcher)
    }


    // TODO:
    private fun validateFields(): Boolean =
            android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text).matches() &&
                    android.util.Patterns.PHONE.matcher(etPhone.text).matches()


    private fun initListTransportTypes() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvTypes)

        val types = transportTypesModule.get()
        val adapter = TransportTypesAdapter(types, true)

        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)

        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
    }


    private fun transferFromFields(): NewTransfer = NewTransfer(
            0,
            0,
            Location(etFrom.text.toString(), 0.0, 0.0),
            Location(etTo.text.toString(), 0.0, 0.0),
            etDate.text.toString(),
            etTime.text.toString(),
            intArrayOf(0),
            etPassengers.text.toString().toIntOrNull() ?: 0, // TODO: Assert here later
            "Sign",
            PassengerProfile(etEmail.text.toString(), etPhone.text.toString())
    )


    fun fabClick(v: View) {
        transfersModule.createTransfer()
//        transfersModule.createTransfer(transferFromFields())
    }


    fun passengersInc(v: View) {
        val n = etPassengers.text.toString().toIntOrNull() ?: 0
        etPassengers.setText((n + 1).toString())
    }

    fun passengersDec(v: View) {
        val n = etPassengers.text.toString().toIntOrNull() ?: 0
        etPassengers.setText((Math.max(1, n - 1)).toString())
    }


    val LOGIN_REQUEST = 1
    fun login(v: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, LOGIN_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == LOGIN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                transfersModule.updateTransfers()
            }
        }
    }
}
