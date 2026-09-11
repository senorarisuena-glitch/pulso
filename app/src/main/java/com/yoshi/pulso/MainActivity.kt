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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var txtEstado: TextView
    private lateinit var txtDesglose: TextView
    private lateinit var edAviso: EditText
    private lateinit var chkSegundo: CheckBox
    private lateinit var chkDiez: CheckBox
    private lateinit var chkVibrar: CheckBox
    private lateinit var listaBloques: LinearLayout
    private lateinit var listaFrases: LinearLayout
    private lateinit var btnEmpezar: Button
    private lateinit var spPerfil: Spinner

    private val camposBloque = ArrayList<EditText>()
    private val camposFrase = ArrayList<EditText>()
    private var frases = ArrayList<Frase>()
    private var indiceAudio = -1
    private var perfilesEnPantalla = listOf<Perfil>()
    private var armandoSpinner = false

    private val reloj = Handler(Looper.getMainLooper())

    private val elegirAudio = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null && indiceAudio >= 0) {
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
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

    // ---------------------------------------------------------------- arranque

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtEstado = findViewById(R.id.txtEstado)
        txtDesglose = findViewById(R.id.txtDesglose)
        edAviso = findViewById(R.id.edAviso)
        chkSegundo = findViewById(R.id.chkSegundo)
        chkDiez = findViewById(R.id.chkDiez)
        chkVibrar = findViewById(R.id.chkVibrar)
        listaBloques = findViewById(R.id.listaBloques)
        listaFrases = findViewById(R.id.listaFrases)
        btnEmpezar = findViewById(R.id.btnEmpezar)
        spPerfil = findViewById(R.id.spPerfil)

        armarSpinner()
        cargarPerfil()

        btnEmpezar.setOnClickListener {
            guardar(false)
            pedirPermisoNotifs()
            mandarAlServicio(PulsoService.ACTION_EMPEZAR)
        }
        findViewById<Button>(R.id.btnTerminar).setOnClickListener {
            mandarAlServicio(PulsoService.ACTION_TERMINAR)
        }
        findViewById<Button>(R.id.btnNuevoPerfil).setOnClickListener { nuevoPerfil() }
        findViewById<Button>(R.id.btnRenombrarPerfil).setOnClickListener { renombrarPerfil() }
        findViewById<Button>(R.id.btnBorrarPerfil).setOnClickListener { borrarPerfil() }
        findViewById<Button>(R.id.btnRutina).setOnClickListener { elegirRutina() }
        findViewById<Button>(R.id.btnGuardarLista).setOnClickListener { guardarListaPropia() }
        findViewById<Button>(R.id.btnCargarLista).setOnClickListener { abrirMisListas() }
        findViewById<Button>(R.id.btnPaquete).setOnClickListener { elegirPaquete() }
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

    // ---------------------------------------------------------------- perfiles

    private fun armarSpinner() {
        armandoSpinner = true
        perfilesEnPantalla = Config.perfiles(this)
        val nombres = perfilesEnPantalla.map { it.nombre }
        val ad = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombres)
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPerfil.adapter = ad
        val actual = Config.perfilActual(this)
        val pos = perfilesEnPantalla.indexOfFirst { it.id == actual }
        if (pos >= 0) spPerfil.setSelection(pos)
        armandoSpinner = false

        spPerfil.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (armandoSpinner) return
                val elegido = perfilesEnPantalla[position]
                if (elegido.id == Config.perfilActual(this@MainActivity)) return
                guardar(false)
                Config.ponerPerfilActual(this@MainActivity, elegido.id)
                cargarPerfil()
                aviso("Perfil: " + elegido.nombre)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }

    private fun cargarPerfil() {
        edAviso.setText(Config.avisoFrases(this).toString())
        chkSegundo.isChecked = Config.cuentaSegundo(this)
        chkDiez.isChecked = Config.avisoDiez(this)
        chkVibrar.isChecked = Config.vibrar(this)
        frases = ArrayList(Config.frases(this))
        pintarBloques()
        pintarFrases()
        txtDesglose.text = Config.desgloseHoy(this)
    }

    private fun nuevoPerfil() {
        val campo = EditText(this)
        campo.hint = "Nombre"
        campo.setTextColor(0xFFF0E7DC.toInt())
        AlertDialog.Builder(this)
            .setTitle("Nuevo perfil")
            .setView(campo)
            .setPositiveButton("Crear") { _, _ ->
                guardar(false)
                Config.crearPerfil(this, campo.text.toString().trim())
                armarSpinner()
                cargarPerfil()
                aviso("Perfil creado")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun renombrarPerfil() {
        val campo = EditText(this)
        campo.setText(Config.nombrePerfilActual(this))
        campo.setTextColor(0xFFF0E7DC.toInt())
        AlertDialog.Builder(this)
            .setTitle("Renombrar perfil")
            .setView(campo)
            .setPositiveButton("Guardar") { _, _ ->
                Config.renombrarPerfil(this, Config.perfilActual(this), campo.text.toString().trim())
                armarSpinner()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun borrarPerfil() {
        val nombre = Config.nombrePerfilActual(this)
        AlertDialog.Builder(this)
            .setTitle("Borrar $nombre")
            .setMessage("Se borran sus frases, su rutina y su conteo. No se puede deshacer.")
            .setPositiveButton("Borrar") { _, _ ->
                val ok = Config.borrarPerfil(this, Config.perfilActual(this))
                if (!ok) { aviso("Debe quedar al menos un perfil"); return@setPositiveButton }
                armarSpinner()
                cargarPerfil()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ---------------------------------------------------------------- plantillas

    private fun elegirRutina() {
        val nombres = Plantillas.rutinas.map { it.nombre + "\n" + it.descripcion }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Rutinas listas")
            .setItems(nombres) { _, i ->
                val r = Plantillas.rutinas[i]
                Config.guardarBloques(this, r.bloques)
                pintarBloques()
                aviso("Rutina aplicada: " + r.nombre)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun elegirPaquete() {
        val nombres = Plantillas.paquetes.map { it.nombre + "\n" + it.descripcion }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Paquetes de frases")
            .setItems(nombres) { _, i ->
                val paq = Plantillas.paquetes[i]
                AlertDialog.Builder(this)
                    .setTitle(paq.nombre)
                    .setMessage("¿Reemplazar tus frases actuales o agregar estas al final?")
                    .setPositiveButton("Reemplazar") { _, _ -> aplicarPaquete(paq, false) }
                    .setNeutralButton("Agregar") { _, _ -> aplicarPaquete(paq, true) }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun aplicarPaquete(paq: Plantillas.Paquete, agregar: Boolean) {
        recogerTextosDeFrases()
        val base = System.currentTimeMillis()
        val nuevas = paq.frases.mapIndexed { i, t -> Frase("f" + (base + i), t, null) }
        if (agregar) frases.addAll(nuevas) else frases = ArrayList(nuevas)
        Config.guardarFrases(this, frases)
        pintarFrases()
        aviso(paq.nombre + " listo")
    }

    // ---------------------------------------------------------------- mis listas

    private fun guardarListaPropia() {
        recogerTextosDeFrases()
        val utiles = frases.filter { it.texto.isNotBlank() || it.audio != null }
        if (utiles.isEmpty()) {
            aviso("No hay frases que guardar")
            return
        }
        val campo = EditText(this)
        campo.hint = "Nombre de la lista"
        campo.setTextColor(0xFFF0E7DC.toInt())
        campo.setHintTextColor(0xFF7A6E8A.toInt())
        AlertDialog.Builder(this)
            .setTitle("Guardar mi lista")
            .setMessage("Se guardan " + utiles.size + " frases con su audio. Podrás usarla en cualquier perfil.")
            .setView(campo)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = campo.text.toString().trim()
                if (nombre.isBlank()) { aviso("Ponle un nombre"); return@setPositiveButton }
                Config.guardarLista(this, nombre, utiles)
                aviso("Lista guardada: " + nombre)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirMisListas() {
        val listas = Config.misListas(this)
        if (listas.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Mis listas")
                .setMessage("Todavía no guardas ninguna. Arma tus frases y dale a Guardar esta lista.")
                .setPositiveButton("Entendido", null)
                .show()
            return
        }
        val nombres = listas.map { it.nombre + "\n" + it.frases.size + " frases" }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Mis listas")
            .setItems(nombres) { _, i -> accionesDeLista(listas[i]) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun accionesDeLista(lista: ListaGuardada) {
        AlertDialog.Builder(this)
            .setTitle(lista.nombre)
            .setMessage("¿Qué quieres hacer con esta lista?")
            .setPositiveButton("Reemplazar") { _, _ -> usarLista(lista, false) }
            .setNeutralButton("Agregar") { _, _ -> usarLista(lista, true) }
            .setNegativeButton("Borrar lista") { _, _ ->
                Config.borrarLista(this, lista.nombre)
                aviso("Lista borrada")
            }
            .show()
    }

    private fun usarLista(lista: ListaGuardada, agregar: Boolean) {
        recogerTextosDeFrases()
        val base = System.currentTimeMillis()
        val copia = lista.frases.mapIndexed { i, f -> Frase("f" + (base + i), f.texto, f.audio) }
        if (agregar) frases.addAll(copia) else frases = ArrayList(copia)
        Config.guardarFrases(this, frases)
        pintarFrases()
        aviso(lista.nombre + " lista")
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
        val av = edAviso.text.toString().toIntOrNull() ?: 4
        Config.guardarNumeros(
            this,
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
