package ar.com.develup.tateti.actividades

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ar.com.develup.tateti.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.actividad_registracion.*

class ActividadRegistracion : AppCompatActivity() {

    //private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initialize Firebase Auth
       // var auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_registracion)

        val pruebaAnalytics = FirebaseAnalytics.getInstance(this);
        val bundle = Bundle();
        bundle.putString("Bienvenido", "Se abrio la app");
        pruebaAnalytics.logEvent("IniciarApp", bundle);

        registrar.setOnClickListener { registrarse() }

    }

    fun registrarse() {
        val passwordIngresada = password.text.toString()
        val confirmarPasswordIngresada = confirmarPassword.text.toString()
        val email = email.text.toString()

        if (email.isEmpty()) {
            // Si no completo el email, muestro mensaje de error
            Snackbar.make(rootView, "Email requerido", Snackbar.LENGTH_SHORT).show()
        } else if (passwordIngresada == confirmarPasswordIngresada) {
            // Si completo el email y las contraseñas coinciden, registramos el usuario en Firebase
            registrarUsuarioEnFirebase(email, passwordIngresada)

            //Eventos en FireBase
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Iniciar Sesion")
            FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

        } else {
            // No coinciden las contraseñas, mostramos mensaje de error
            Snackbar.make(rootView, "Las contraseñas no coinciden", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun registrarUsuarioEnFirebase(email: String, passwordIngresada: String) {
    // TODO-05-AUTHENTICATION
        // Crear el usuario con el email y passwordIngresada
        // Ademas, registrar en CompleteListener el listener registracionCompletaListener definido mas abajo
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, passwordIngresada).addOnCompleteListener(this, registracionCompletaListener)

    }

    private val registracionCompletaListener: OnCompleteListener<AuthResult?> = OnCompleteListener { task ->
        if (task.isSuccessful) {
            // Si se registro OK, muestro mensaje y envio mail de verificacion
            Snackbar.make(rootView, "Registro exitoso", Snackbar.LENGTH_SHORT).show()
            enviarEmailDeVerificacion()
        } else if (task.exception is FirebaseAuthUserCollisionException) {
            // Si el usuario ya existe, mostramos error
            Snackbar.make(rootView, "El usuario ya existe", Snackbar.LENGTH_SHORT).show()
        } else {
            // Por cualquier otro error, mostramos un mensaje de error
            Snackbar.make(rootView, "El registro fallo: " + task.exception, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun enviarEmailDeVerificacion() {
        // TODO-05-AUTHENTICATION
        // Enviar mail de verificacion al usuario currentUser
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()

    }

    private fun verPartidas() {
        val intent = Intent(this, ActividadPartidas::class.java)
        startActivity(intent)

        //Eventos en FireBase
        val bundle = Bundle()
        bundle.putString("VER_PARTIDAS", "VerPartidas")
        FirebaseAnalytics.getInstance(this).logEvent("VerPartidas", bundle)
    }

}