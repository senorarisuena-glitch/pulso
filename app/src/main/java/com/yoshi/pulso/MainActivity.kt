package com.yoshi.pulso

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var txtEstado: TextView
    private lateinit var txtDesglose: TextView
    private lateinit var edMin: EditText
    private lateinit var edAviso: EditText
    private lateinit var chkSegundo: CheckBox
    private lateinit var chkDiez: CheckBox
    private lateinit var chkVibrar: CheckBox
    private lateinit var listaBloques: LinearLayout
    private lateinit var listaFrases: LinearLayout
    private lateinit var btnEmpezar: Button

    private val camposBloque = ArrayList<EditText>()
    private val camposFrase = ArrayList<EditText>()
    private var frases = ArrayList<Frase>()
    private var indiceAudio = -1

    private val reloj = Handler(Looper.getMainLooper())

    private val elegirAudio = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null && indiceAudio >= 0) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) { }
            recogerTextosDeFrases()
            frases[indiceAudio] = frases[indiceAudio].copy(audio = uri.toString())
            Config.guardarFrases(this, frases)
            pintarFrases()
            aviso("Audio asignado")
        }
        indiceAudio = -1
    }

    private val pedirNotifs = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtEstado = findViewById(R.id.txtEstado)
        txtDesglose = findViewById(R.id.txtDesglose)
        edMin = findViewById(R.id.edMin)
        edAviso = findViewById(R.id.edAviso)
        chkSegundo = findViewById(R.id.chkSegundo)
        chkDiez = findViewById(R.id.chkDiez)
        chkVibrar = findViewById(R.id.chkVibrar)
        listaBloques = findViewById(R.id.listaBloques)
        listaFrases = findViewById(R.id.listaFrases)
        btnEmpezar = findViewById(R.id.btnEmpezar)

        edMin.setText(Config.minEnfoque(this).toString())
        edAviso.setText(Config.avisoFrases(this).toString())
        chkSegundo.isChecked = Config.cuentaSegundo(this)
        chkDiez.isChecked = Config.avisoDiez(this)
        chkVibrar.isChecked = Config.vibrar(this)

        frases = ArrayList(Config.frases(this))
        pintarBloques()
        pintarFrases()

        btnEmpezar.setOnClickListener {
            guardar(false)
            pedirPermisoNotifs()
            mandarAlServicio(PulsoService.ACTION_EMPEZAR)
        }
        findViewById<Button>(R.id.btnTerminar).setOnClickListener {
            mandarAlServicio(PulsoService.ACTION_TERMINAR)
        }
        findViewById<Button>(R.id.btnAgregarFrase).setOnClickListener {
            recogerTextosDeFrases()
            frases.add(Frase("f" + System.currentTimeMillis(), "", null))
            Config.guardarFrases(this, frases)
            pintarFrases()
        }
        findViewById<Button>(R.id.btnBorrarHoy).setOnClickListener {
            Config.borrarHoy(this)
            txtDesglose.text = Config.desgloseHoy(this)
        }
        findViewById<Button>(R.id.btnBateria).setOnClickListener { pedirSinBateria() }
        findViewById<Button>(R.id.btnGuardar).setOnClickListener { guardar(true) }

        pedirPermisoNotifs()
    }

    override fun onResume() {
        super.onResume()
        reloj.post(refresco)
    }

    override fun onPause() {
        super.onPause()
        reloj.removeCallbacksAndMessages(null)
        guardar(false)
    }

    private val refresco = object : Runnable {
        override fun run() {
            txtEstado.text = if (PulsoService.activo) PulsoService.resumen else "Detenido"
            btnEmpezar.text = if (PulsoService.activo) "Reiniciar ciclo" else "Empezar"
            txtDesglose.text = Config.desgloseHoy(this@MainActivity)
            reloj.postDelayed(this, 1000)
        }
    }

    // ---------------------------------------------------------------- bloques

    private fun pintarBloques() {
        listaBloques.removeAllViews()
        camposBloque.clear()
        for (b in Config.bloques(this)) {
            val etiqueta = TextView(this)
            etiqueta.text = b.nombre
            etiqueta.setTextColor(0xFF9C8FAE.toInt())
            etiqueta.textSize = 12f
            listaBloques.addView(etiqueta)

            val campo = EditText(this)
            campo.setText(Config.textoDeBloque(b))
            campo.setBackgroundResource(R.drawable.fondo_campo)
            campo.setTextColor(0xFFF0E7DC.toInt())
            campo.textSize = 14f
            campo.setPadding(24, 24, 24, 24)
            campo.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            campo.setSingleLine(false)
            campo.gravity = Gravity.TOP or Gravity.START
            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            lp.bottomMargin = 24
            campo.layoutParams = lp
            listaBloques.addView(campo)
            camposBloque.add(campo)
        }
    }

    // ---------------------------------------------------------------- frases

    private fun pintarFrases() {
        listaFrases.removeAllViews()
        camposFrase.clear()

        for (i in frases.indices) {
            val f = frases[i]

            val caja = LinearLayout(this)
            caja.orientation = LinearLayout.VERTICAL
            caja.setBackgroundResource(R.drawable.fondo_campo)
            caja.setPadding(20, 20, 20, 20)
            val cajaLp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            cajaLp.bottomMargin = 22
            caja.layoutParams = cajaLp

            val campo = EditText(this)
            campo.setText(f.texto)
            campo.hint = "Escribe la frase (o déjala vacía si solo usarás audio)"
            campo.setTextColor(0xFFF0E7DC.toInt())
            campo.setHintTextColor(0xFF7A6E8A.toInt())
            campo.textSize = 15f
            campo.setBackgroundColor(0x00000000)
            campo.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            campo.setSingleLine(false)
            campo.gravity = Gravity.TOP or Gravity.START
            caja.addView(campo)
            camposFrase.add(campo)

            if (f.audio != null) {
                val marca = TextView(this)
                marca.text = "▸ tiene tu audio"
                marca.setTextColor(0xFF58C0A0.toInt())
                marca.textSize = 12f
                caja.addView(marca)
            }

            val fila = LinearLayout(this)
            fila.orientation = LinearLayout.HORIZONTAL

            fila.addView(botonChico(if (f.audio == null) "Poner audio" else "Cambiar audio") {
                indiceAudio = i
                try {
                    elegirAudio.launch(arrayOf("audio/*"))
                } catch (e: Exception) {
                    aviso("No se pudo abrir el selector de archivos")
                }
            })

            if (f.audio != null) {
                fila.addView(botonChico("Quitar audio") {
                    recogerTextosDeFrases()
                    frases[i] = frases[i].copy(audio = null)
                    Config.guardarFrases(this, frases)
                    pintarFrases()
                })
            }

            fila.addView(botonChico("Borrar") {
                recogerTextosDeFrases()
                frases.removeAt(i)
                Config.guardarFrases(this, frases)
                pintarFrases()
            })

            caja.addView(fila)
            listaFrases.addView(caja)
        }
    }

    private fun botonChico(texto: String, accion: () -> Unit): Button {
        val b = Button(this)
        b.text = texto
        b.textSize = 11f
        b.setBackgroundResource(R.drawable.fondo_boton_hueco)
        b.setTextColor(0xFF9C8FAE.toInt())
        b.setPadding(18, 8, 18, 8)
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.rightMargin = 14
        lp.topMargin = 12
        b.layoutParams = lp
        b.setOnClickListener { accion() }
        return b
    }

    private fun recogerTextosDeFrases() {
        for (i in camposFrase.indices) {
            if (i < frases.size) {
                frases[i] = frases[i].copy(texto = camposFrase[i].text.toString().trim())
            }
        }
    }

    // ---------------------------------------------------------------- guardar

    private fun guardar(conAviso: Boolean) {
        val min = edMin.text.toString().toIntOrNull() ?: 25
        val av = edAviso.text.toString().toIntOrNull() ?: 4
        Config.guardarNumeros(
            this,
            if (min > 0) min else 25,
            if (av > 0) av else 4,
            chkSegundo.isChecked,
            chkDiez.isChecked,
            chkVibrar.isChecked
        )

        val originales = Config.bloques(this)
        val nuevos = ArrayList<Bloque>()
        for (i in camposBloque.indices) {
            val nombre = if (i < originales.size) originales[i].nombre else "Bloque " + (i + 1)
            nuevos.add(Config.bloqueDeTexto(nombre, camposBloque[i].text.toString()))
        }
        if (nuevos.isNotEmpty()) Config.guardarBloques(this, nuevos)

        recogerTextosDeFrases()
        Config.guardarFrases(this, frases)

        if (conAviso) aviso("Guardado")
    }

    // ---------------------------------------------------------------- permisos y servicio

    private fun pedirPermisoNotifs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val ok = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!ok) pedirNotifs.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun pedirSinBateria() {
        try {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (pm.isIgnoringBatteryOptimizations(packageName)) {
                aviso("Ya está sin optimización de batería")
                return
            }
            val i = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            i.data = Uri.parse("package:$packageName")
            startActivity(i)
        } catch (e: Exception) {
            try {
                startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
            } catch (e2: Exception) {
                aviso("Ábrelo a mano en Ajustes > Batería")
            }
        }
    }

    private fun mandarAlServicio(accion: String) {
        val i = Intent(this, PulsoService::class.java).setAction(accion)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i)
        else startService(i)
    }

    private fun aviso(t: String) {
        Toast.makeText(this, t, Toast.LENGTH_SHORT).show()
    }
}
