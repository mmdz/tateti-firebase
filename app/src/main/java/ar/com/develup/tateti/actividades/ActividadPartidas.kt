package ar.com.develup.tateti.actividades

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ar.com.develup.tateti.R
import ar.com.develup.tateti.adaptadores.AdaptadorPartidas
import ar.com.develup.tateti.modelo.Constantes
import ar.com.develup.tateti.modelo.Partida
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.actividad_partidas.*

class ActividadPartidas : AppCompatActivity() {

    companion object {
        private const val TAG = "ActividadPartidas"
    }

    private lateinit var adaptadorPartidas: AdaptadorPartidas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_partidas)
        adaptadorPartidas = AdaptadorPartidas(this)
        partidas.layoutManager = LinearLayoutManager(this)
        partidas.adapter = adaptadorPartidas
        nuevaPartida.setOnClickListener { nuevaPartida() }
        cerrarSesion.setOnClickListener { cerrarSesion() }

        // Obtener el usuario actual
        val usuario = FirebaseAuth.getInstance().currentUser?.email
        bienvenido.text = "Bienvenidx $usuario"
    }

    override fun onResume() {
        super.onResume()
        // TODO-06-DATABASE
        // Obtener una referencia a la base de datos, suscribirse a los cambios en Constantes.TABLA_PARTIDAS
        // y agregar como ChildEventListener el listenerTablaPartidas definido mas abajo
        FirebaseDatabase.getInstance().reference.child(Constantes.TABLA_PARTIDAS).addChildEventListener(listenerTablaPartidas)

    }

    fun nuevaPartida() {
        //Eventos de Firebase Analytics
        val bundle = Bundle()
        bundle.putString("email_creador", FirebaseAuth.getInstance().currentUser?.email)
        FirebaseAnalytics.getInstance(this).logEvent("Nueva_Partida", bundle)

        val intent = Intent(this, ActividadPartida::class.java)
        startActivity(intent)
    }

    private val listenerTablaPartidas: ChildEventListener = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            Log.i(TAG, "onChildAdded: $dataSnapshot")
            // Obtener el valor del dataSnapshot
            val partida = dataSnapshot.getValue(Partida::class.java)
            // Asignar el valor del campo "key" del dataSnapshot
            partida!!.id = dataSnapshot.key!!
            adaptadorPartidas.agregarPartida(partida)
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
            Log.i(TAG, "onChildChanged: $s")
            val partida = dataSnapshot.getValue(Partida::class.java)
            // Obtener el valor del dataSnapshot
            partida!!.id = dataSnapshot.key!!
            // Asignar el valor del campo "key" del dataSnapshot
            adaptadorPartidas.partidaCambio(partida)
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            Log.i(TAG, "onChildRemoved: ")
            val partida = dataSnapshot.getValue(Partida::class.java)
            // Obtener el valor del dataSnapshot
            partida!!.id = dataSnapshot.key!!
            // Asignar el valor del campo "key" del dataSnapshot
            adaptadorPartidas.remover(partida)
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            Log.i(TAG, "onChildMoved: $s")
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.i(TAG, "onCancelled: ")
        }
    }
    private fun cerrarSesion() {

        //Eventos en FireBase
        val bundle = Bundle()
        bundle.putString("se_deslogueo", FirebaseAuth.getInstance().currentUser?.email)
        FirebaseAnalytics.getInstance(this).logEvent("desloguearse", bundle)

        FirebaseAuth.getInstance().signOut() // para cerrar sesion
        val intent = Intent(this, ActividadInicial::class.java)
        startActivity(intent)
    }

}